import React, { useState, useEffect } from 'react';
import { API_ENDPOINTS, authFetch } from '../../config';
import './PacienteDashboard.css';

export default function PacienteDashboard() {
  const [paciente, setPaciente] = useState(null);
  const [consultas, setConsultas] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');
  const [aba, setAba] = useState('consultas');
  const [especialidade, setEspecialidade] = useState('CARDIOLOGIA');
  const [dataConsulta, setDataConsulta] = useState(new Date().toISOString().slice(0, 10));
  const [medicosDisponiveis, setMedicosDisponiveis] = useState([]);
  const [medicoSelecionado, setMedicoSelecionado] = useState('');
  const [horariosDisponiveis, setHorariosDisponiveis] = useState([]);
  const [horarioSelecionado, setHorarioSelecionado] = useState('');
  const [mensagem, setMensagem] = useState('');
  const [erroAgendamento, setErroAgendamento] = useState('');
  const [cancelando, setCancelando] = useState(false);
  const [consultaCancelamento, setConsultaCancelamento] = useState(null);
  const [motivoCancelamento, setMotivoCancelamento] = useState('PACIENTE_DESISTIU');
  const [erroCancelamento, setErroCancelamento] = useState('');
  const [abaConsultas, setAbaConsultas] = useState('agendadas');
  const [medicosAtivos, setMedicosAtivos] = useState(0);

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    try {
      const responsePaciente = await authFetch(API_ENDPOINTS.PACIENTE_ME);

      if (responsePaciente.ok) {
        const dataPaciente = await responsePaciente.json();
        setPaciente(dataPaciente);
      } else {
        const text = await responsePaciente.text();
        setErro(text || 'Erro ao carregar paciente');
        setPaciente(null);
      }

      // Carregar consultas do paciente
      const responseConsultas = await authFetch(
        `${API_ENDPOINTS.CONSULTAS}?page=0&size=10`
      );

      if (responseConsultas.ok) {
        const data = await responseConsultas.json();
        const list = Array.isArray(data) ? data : data.content || [];
        setConsultas(list);
      }

      const responseMedicos = await authFetch(API_ENDPOINTS.MEDICOS_ATIVOS_COUNT);

      if (responseMedicos.ok) {
        const count = await responseMedicos.json();
        setMedicosAtivos(Number(count) || 0);
      }
    } catch (err) {
      setErro(err.message);
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    if (aba === 'agendar') {
      carregarDisponibilidade();
    }
  }, [aba, especialidade, dataConsulta]);

  useEffect(() => {
    if (!medicoSelecionado) {
      const all = medicosDisponiveis.flatMap(m => m.horariosDisponiveis || []);
      const unique = Array.from(new Set(all)).sort();
      setHorariosDisponiveis(unique);
      return;
    }
    const medico = medicosDisponiveis.find(m => String(m.medicoId) === String(medicoSelecionado));
    setHorariosDisponiveis(medico ? medico.horariosDisponiveis : []);
  }, [medicoSelecionado, medicosDisponiveis]);

  const isDomingo = (dateStr) => {
    const d = new Date(`${dateStr}T00:00:00`);
    return d.getDay() === 0;
  };

  const carregarDisponibilidade = async () => {
    try {
      setMensagem('');
      setErroAgendamento('');

      if (isDomingo(dataConsulta)) {
        setMedicosDisponiveis([]);
        setMedicoSelecionado('');
        setHorarioSelecionado('');
        setHorariosDisponiveis([]);
        setErroAgendamento('Domingo não há atendimento. Escolha outra data.');
        return;
      }
      const response = await authFetch(
        `${API_ENDPOINTS.CONSULTAS_DISPONIBILIDADE}?date=${dataConsulta}&especialidade=${especialidade}`
      );

      if (response.ok) {
        const data = await response.json();
        setMedicosDisponiveis(data || []);
        setMedicoSelecionado('');
        setHorarioSelecionado('');
      } else {
        const text = await response.text();
        setErroAgendamento(text || 'Erro ao carregar disponibilidade');
      }
    } catch (err) {
      setErroAgendamento(err.message);
    }
  };

  const agendarConsulta = async () => {
    try {
      if (!paciente?.id) {
        setErroAgendamento('Paciente não identificado. Recarregue a página e tente novamente.');
        return;
      }
      if (!horarioSelecionado) {
        setErroAgendamento('Selecione um horário');
        return;
      }
      if (isDomingo(dataConsulta)) {
        setErroAgendamento('Domingo não há atendimento.');
        return;
      }

      const dataHora = `${dataConsulta}T${horarioSelecionado}:00`;
      const payload = {
        pacienteId: paciente.id,
        medicoId: medicoSelecionado ? Number(medicoSelecionado) : null,
        especialidade,
        dataHora,
      };

      const response = await authFetch(API_ENDPOINTS.CONSULTAS, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || 'Erro ao agendar consulta');
      }

      setMensagem('Consulta agendada com sucesso');
      setErroAgendamento('');
      carregarDados();
      carregarDisponibilidade();
    } catch (err) {
      setErroAgendamento(err.message);
    }
  };

  const podeCancelar = (consulta) => {
    if (!consulta?.dataHora || consulta.canceladaEm) return false;
    const dataHora = new Date(consulta.dataHora).getTime();
    const agora = Date.now();
    const diffHoras = (dataHora - agora) / (1000 * 60 * 60);
    return diffHoras >= 24;
  };

  const abrirCancelamento = (consulta) => {
    setConsultaCancelamento(consulta);
    setMotivoCancelamento('PACIENTE_DESISTIU');
    setErroCancelamento('');
  };

  const fecharCancelamento = () => {
    setConsultaCancelamento(null);
    setErroCancelamento('');
  };

  const confirmarCancelamento = async () => {
    if (!consultaCancelamento) return;
    try {
      setCancelando(true);
      const response = await authFetch(`${API_ENDPOINTS.CONSULTAS}/${consultaCancelamento.id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ motivo: motivoCancelamento }),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || 'Erro ao cancelar consulta');
      }

      setMensagem('Consulta cancelada com sucesso');
      fecharCancelamento();
      carregarDados();
    } catch (err) {
      setErroCancelamento(err.message);
    } finally {
      setCancelando(false);
    }
  };

  return (
    <div className="paciente-dashboard">
      <h1>Bem-vindo ao Portal do Paciente</h1>

      <div className="dashboard-cards">
        <div className="card">
          <h3>📅 Consultas Agendadas</h3>
          <p className="card-number">{consultas.filter(c => !c.canceladaEm).length}</p>
        </div>
        <div className="card">
          <h3>👨‍⚕️ Médicos Disponíveis</h3>
          <p className="card-number">{medicosAtivos}</p>
        </div>
        <div className="card">
          <h3>⛔ Consultas Canceladas</h3>
          <p className="card-number">{consultas.filter(c => c.canceladaEm).length}</p>
        </div>
      </div>

      {erro && <div className="alert alert-error">{erro}</div>}

      <div className="tabs">
        <button
          className={`tab ${aba === 'consultas' ? 'active' : ''}`}
          onClick={() => setAba('consultas')}
        >
          📅 Minhas Consultas
        </button>
        <button
          className={`tab ${aba === 'agendar' ? 'active' : ''}`}
          onClick={() => setAba('agendar')}
        >
          ➕ Marcar Consulta
        </button>
      </div>

      <div className="card">
        {aba === 'consultas' && (
          <>
            <h2>Minhas Consultas</h2>

        {carregando ? (
          <div className="loading">
            <div className="spinner"></div>
            <p>Carregando consultas...</p>
          </div>
        ) : consultas.length === 0 ? (
          <p className="no-data">Nenhuma consulta agendada</p>
        ) : (
          <>
            <div className="tabs">
              <button
                className={`tab ${abaConsultas === 'agendadas' ? 'active' : ''}`}
                onClick={() => setAbaConsultas('agendadas')}
              >
                Agendadas
              </button>
              <button
                className={`tab ${abaConsultas === 'canceladas' ? 'active' : ''}`}
                onClick={() => setAbaConsultas('canceladas')}
              >
                Canceladas
              </button>
              <button
                className={`tab ${abaConsultas === 'todas' ? 'active' : ''}`}
                onClick={() => setAbaConsultas('todas')}
              >
                Todas
              </button>
            </div>

            <table className="table">
              <thead>
                <tr>
                  <th>Data</th>
                  <th>Horário</th>
                  <th>Médico</th>
                  <th>Especialidade</th>
                  <th>Status</th>
                  <th>Motivo</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {[...consultas]
                  .filter(c => {
                    if (abaConsultas === 'agendadas') return !c.canceladaEm;
                    if (abaConsultas === 'canceladas') return !!c.canceladaEm;
                    return true;
                  })
                  .sort((a, b) => {
                    const da = a.dataHora ? new Date(a.dataHora).getTime() : 0;
                    const db = b.dataHora ? new Date(b.dataHora).getTime() : 0;
                    return da - db;
                  })
                  .map(consulta => {
                    const motivo = consulta.motivoCancelamento || consulta.motivo || '—';
                    return (
                  <tr key={consulta.id}>
                    <td>{consulta.dataHora ? new Date(consulta.dataHora).toLocaleDateString() : '—'}</td>
                    <td>{consulta.dataHora ? new Date(consulta.dataHora).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '—'}</td>
                    <td>{consulta.medico?.nome || '—'}</td>
                    <td>{consulta.medico?.especialidade || '—'}</td>
                    <td>
                      <span
                        className={`badge ${consulta.canceladaEm ? 'badge-danger' : 'badge-success'}`}
                        title={consulta.canceladaEm ? `Motivo: ${motivo}` : ''}
                      >
                        {consulta.canceladaEm ? 'Cancelada' : 'Agendada'}
                      </span>
                    </td>
                    <td>{consulta.canceladaEm ? motivo : '—'}</td>
                    <td>
                      {consulta.canceladaEm ? (
                        <span className="badge badge-secondary">—</span>
                      ) : (
                        <button
                          className="btn btn-outline btn-small"
                          onClick={() => abrirCancelamento(consulta)}
                          disabled={!podeCancelar(consulta)}
                          title={!podeCancelar(consulta) ? 'Cancelamento permitido apenas com 24h de antecedência' : ''}
                        >
                          Cancelar
                        </button>
                      )}
                    </td>
                  </tr>
                    );
                  })}
              </tbody>
            </table>
          </>
        )}
          </>
        )}

        {aba === 'agendar' && (
          <>
            <h2>Marcar Consulta</h2>
            {erroAgendamento && <div className="alert alert-error">{erroAgendamento}</div>}
            {mensagem && <div className="alert alert-success">{mensagem}</div>}

            <div className="form-row">
              <div className="form-group">
                <label>Especialidade</label>
                <select value={especialidade} onChange={(e) => setEspecialidade(e.target.value)}>
                  {['CARDIOLOGIA','DERMATOLOGIA','OFTALMOLOGIA','ORTOPEDIA','PNEUMOLOGIA','GASTROENTEROLOGIA','GINECOLOGIA'].map(esp => (
                    <option key={esp} value={esp}>{esp}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Data</label>
                <input type="date" value={dataConsulta} onChange={(e) => setDataConsulta(e.target.value)} />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Médico (opcional)</label>
                <select value={medicoSelecionado} onChange={(e) => setMedicoSelecionado(e.target.value)}>
                  <option value="">Qualquer disponível</option>
                  {medicosDisponiveis.map(m => (
                    <option key={m.medicoId} value={m.medicoId}>
                      {m.nome} • {m.crm}/{m.crmUf}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Horário</label>
                <select
                  value={horarioSelecionado}
                  onChange={(e) => setHorarioSelecionado(e.target.value)}
                  disabled={isDomingo(dataConsulta) || horariosDisponiveis.length === 0}
                >
                  <option value="">Selecione</option>
                  {horariosDisponiveis.map(h => (
                    <option key={h} value={h}>{h}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="horarios-grid">
              {medicosDisponiveis.length === 0 ? (
                <p className="no-data">Nenhum médico disponível para a data/especialidade selecionada.</p>
              ) : (
                medicosDisponiveis.map(m => (
                  <div key={m.medicoId} className="horarios-coluna">
                    <div className="horarios-coluna-header">
                      <strong>{m.nome}</strong>
                      <span>{m.crm}/{m.crmUf}</span>
                    </div>
                    <div className="horarios-coluna-body">
                      {(m.horariosDisponiveis || []).length === 0 ? (
                        <span className="no-data">Sem horários</span>
                      ) : (
                        m.horariosDisponiveis.map(h => (
                          <button
                            key={`${m.medicoId}-${h}`}
                            type="button"
                            className={`btn btn-small ${String(m.medicoId) === String(medicoSelecionado) && h === horarioSelecionado ? 'btn-primary' : 'btn-outline'}`}
                            onClick={() => {
                              setMedicoSelecionado(String(m.medicoId));
                              setHorarioSelecionado(h);
                            }}
                          >
                            {h}
                          </button>
                        ))
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>

            <button className="btn btn-primary" onClick={agendarConsulta}>Agendar</button>
          </>
        )}
      </div>

      {consultaCancelamento && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>Cancelar Consulta</h3>
            <div className="modal-body">
              <p><strong>Consulta:</strong> {consultaCancelamento.dataHora ? new Date(consultaCancelamento.dataHora).toLocaleString() : '—'}</p>
              <p><strong>Médico:</strong> {consultaCancelamento.medico?.nome || '—'}</p>

              <div className="form-group">
                <label>Motivo do cancelamento</label>
                <select value={motivoCancelamento} onChange={(e) => setMotivoCancelamento(e.target.value)}>
                  <option value="PACIENTE_DESISTIU">Paciente desistiu</option>
                  <option value="PROBLEMA_AGENDA">Problema de agenda</option>
                  <option value="OUTROS">Outros</option>
                </select>
              </div>

              {erroCancelamento && <div className="alert alert-error">{erroCancelamento}</div>}
            </div>
            <div className="modal-actions">
              <button className="btn btn-outline" onClick={fecharCancelamento} disabled={cancelando}>Voltar</button>
              <button className="btn btn-danger" onClick={confirmarCancelamento} disabled={cancelando}>
                {cancelando ? 'Cancelando...' : 'Confirmar Cancelamento'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
