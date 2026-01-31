package inf012.apiclinica.service;

import inf012.apiclinica.model.Consulta;
import inf012.apiclinica.model.Paciente;
import inf012.apiclinica.model.Medico;
import inf012.apiclinica.dto.ConsultaCreateDTO;
import inf012.apiclinica.dto.ConsultaCancelamentoDTO;
import inf012.apiclinica.dto.DisponibilidadeMedicoDTO;
import inf012.apiclinica.repository.ConsultaRepository;
import inf012.apiclinica.repository.PacienteRepository;
import inf012.apiclinica.repository.MedicoRepository;
import inf012.apiclinica.model.MotivoCancelamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public ConsultaService(
            ConsultaRepository consultaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository
    ) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @Transactional
    public Consulta agendar(ConsultaCreateDTO dto) {

        LocalDateTime dataHora = dto.getDataHora();
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        LocalDateTime agora = LocalDateTime.now(zone);

        if (dataHora.isBefore(agora.plusMinutes(30))) {
            throw new RuntimeException("Consulta deve ser agendada com 30 minutos de antecedência");
        }

        DayOfWeek dia = dataHora.getDayOfWeek();
        LocalTime hora = dataHora.toLocalTime();

        if (dia == DayOfWeek.SUNDAY ||
            hora.isBefore(LocalTime.of(7, 0)) ||
            hora.isAfter(LocalTime.of(18, 0))) {
            throw new RuntimeException("Horário fora do funcionamento da clínica");
        }

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        if (!paciente.getAtivo()) {
            throw new RuntimeException("Paciente inativo");
        }

        LocalDateTime inicioDia = dataHora.toLocalDate().atStartOfDay();
        LocalDateTime fimDia = inicioDia.plusDays(1);

        if (consultaRepository.existsByPacienteIdAndDataHoraBetweenAndCanceladaEmIsNull(
            paciente.getId(), inicioDia, fimDia)) {
            throw new RuntimeException("Paciente já possui consulta neste dia");
        }

        Medico medico;

        if (dto.getMedicoId() != null) {

            medico = medicoRepository.findById(dto.getMedicoId())
                    .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

            if (!medico.getAtivo()) {
                throw new RuntimeException("Médico inativo");
            }

                if (consultaRepository.existsByMedicoIdAndDataHoraAndCanceladaEmIsNull(
                    medico.getId(), dataHora)) {
                throw new RuntimeException("Médico já possui consulta neste horário");
            }

        } else {
            if (dto.getEspecialidade() == null) {
                throw new RuntimeException("Especialidade é obrigatória quando não há médico selecionado");
            }

            List<Medico> medicos = medicoRepository.findAllByAtivoTrueAndEspecialidade(dto.getEspecialidade());
            medicos.removeIf(m ->
                    consultaRepository.existsByMedicoIdAndDataHoraAndCanceladaEmIsNull(m.getId(), dataHora)
            );

            if (medicos.isEmpty()) {
                throw new RuntimeException("Nenhum médico disponível");
            }

            medico = medicos.get(new Random().nextInt(medicos.size()));
        }

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setDataHora(dataHora);

        return consultaRepository.save(consulta);
    }

    @Transactional
    public void cancelar(Long consultaId, ConsultaCancelamentoDTO dto) {

        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        LocalDateTime agora = LocalDateTime.now(zone);
        if (consulta.getDataHora().isBefore(agora.plusHours(24))) {
            throw new RuntimeException("Consulta só pode ser cancelada com 24h de antecedência");
        }

        consulta.setMotivoCancelamento(dto.getMotivo());
        consulta.setCanceladaEm(LocalDateTime.now());
    }

    public java.util.List<Consulta> listarPorMedicoENoDia(String nomeUsuario, java.time.LocalDate dia) {
        Medico medico = medicoRepository.findByNomeUsuario(nomeUsuario);
        if (medico == null) return java.util.Collections.emptyList();
        java.time.LocalDateTime inicio = dia.atStartOfDay();
        java.time.LocalDateTime fim = inicio.plusDays(1);
        return consultaRepository.findByMedicoIdAndDataHoraBetween(medico.getId(), inicio, fim);
    }

    public Page<Consulta> listarPorPaciente(String nomeUsuario, Pageable pageable) {
        Paciente paciente = pacienteRepository.findByNomeUsuario(nomeUsuario);
        if (paciente == null) {
            return Page.empty(pageable);
        }
        return consultaRepository.findByPacienteId(paciente.getId(), pageable);
    }

    @Transactional
    public void cancelarTodasConsultasDoMedico(String nomeUsuario) {
        Medico medico = medicoRepository.findByNomeUsuario(nomeUsuario);
        if (medico == null) {
            throw new RuntimeException("Médico não encontrado");
        }
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        LocalDateTime agora = LocalDateTime.now(zone);
        List<Consulta> abertas = consultaRepository.findByMedicoIdAndCanceladaEmIsNull(medico.getId());
        for (Consulta c : abertas) {
            c.setMotivoCancelamento(MotivoCancelamento.MEDICO_INATIVO);
            c.setCanceladaEm(agora);
        }
        consultaRepository.saveAll(abertas);
    }

    public List<DisponibilidadeMedicoDTO> listarDisponibilidade( inf012.apiclinica.model.Especialidade especialidade, LocalDate dia) {
        LocalDate diaEfetivo = (dia == null) ? LocalDate.now() : dia;

        if (diaEfetivo.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return List.of();
        }

        List<Medico> medicos = medicoRepository.findAllByAtivoTrueAndEspecialidade(especialidade);
        LocalDateTime inicio = diaEfetivo.atStartOfDay();
        LocalDateTime fim = inicio.plusDays(1);

        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        LocalDateTime limite = LocalDateTime.now(zone).plusMinutes(30);

        return medicos.stream().map(m -> {
            List<Consulta> consultas = consultaRepository.findByMedicoIdAndDataHoraBetween(m.getId(), inicio, fim);
            List<LocalTime> ocupados = consultas.stream()
                    .filter(c -> c.getCanceladaEm() == null)
                    .map(c -> c.getDataHora().toLocalTime())
                    .collect(Collectors.toList());

            List<String> horarios = new ArrayList<>();
            for (int h = 7; h <= 18; h++) {
                LocalDateTime slot = LocalDateTime.of(diaEfetivo, LocalTime.of(h, 0));
                if (slot.isBefore(limite)) {
                    continue;
                }
                if (!ocupados.contains(LocalTime.of(h, 0))) {
                    horarios.add(String.format("%02d:00", h));
                }
            }

            return new DisponibilidadeMedicoDTO(
                    m.getId(),
                    m.getNome(),
                    m.getCrm(),
                    m.getCrmUf(),
                    m.getEspecialidade(),
                    horarios
            );
        }).collect(Collectors.toList());
    }
}
