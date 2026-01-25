package inf012.apiclinica.service;

import inf012.apiclinica.model.Consulta;
import inf012.apiclinica.model.Paciente;
import inf012.apiclinica.model.Medico;
import inf012.apiclinica.dto.ConsultaCreateDTO;
import inf012.apiclinica.dto.ConsultaCancelamentoDTO;
import inf012.apiclinica.repository.ConsultaRepository;
import inf012.apiclinica.repository.PacienteRepository;
import inf012.apiclinica.repository.MedicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;
import java.util.Random;

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

        if (dataHora.isBefore(LocalDateTime.now().plusMinutes(30))) {
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

        if (consultaRepository.existsByPacienteIdAndDataHoraBetween(
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

            if (consultaRepository.existsByMedicoIdAndDataHora(
                    medico.getId(), dataHora)) {
                throw new RuntimeException("Médico já possui consulta neste horário");
            }

        } else {

            List<Medico> medicos = medicoRepository.findAllByAtivoTrue();
            medicos.removeIf(m ->
                    consultaRepository.existsByMedicoIdAndDataHora(m.getId(), dataHora)
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

        if (consulta.getDataHora().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new RuntimeException("Consulta só pode ser cancelada com 24h de antecedência");
        }

        consulta.setMotivoCancelamento(dto.getMotivo());
        consulta.setCanceladaEm(LocalDateTime.now());
    }
}
