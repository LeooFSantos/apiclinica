import React, { useState, useEffect } from 'react';
import { API_ENDPOINTS, getAuthToken } from '../../config';
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

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    try {
      const token = getAuthToken();

      const responsePaciente = await fetch(
        API_ENDPOINTS.PACIENTE_ME,
        { headers: { 'Authorization': `Bearer ${token}` } }
      );

      if (responsePaciente.ok) {
        const dataPaciente = await responsePaciente.json();
        setPaciente(dataPaciente);
      } else {
        const text = await responsePaciente.text();
        setErro(text || 'Erro ao carregar paciente');
        setPaciente(null);
      }

      // Carregar consultas do paciente
      const responseConsultas = await fetch(
        `${API_ENDPOINTS.CONSULTAS}?page=0&size=10`,
        {
          headers: { 'Authorization': `Bearer ${token}` },
        }
      );

      if (responseConsultas.ok) {
        const data = await responseConsultas.json();
        const list = Array.isArray(data) ? data : data.content || [];
        setConsultas(list);
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
      const token = getAuthToken();
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
      const response = await fetch(
        `${API_ENDPOINTS.CONSULTAS_DISPONIBILIDADE}?date=${dataConsulta}&especialidade=${especialidade}`,
        { headers: { 'Authorization': `Bearer ${token}` } }
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
        dataHora,
      };

      const token = getAuthToken();
      const response = await fetch(API_ENDPOINTS.CONSULTAS, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
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

  return (
    <div className="paciente-dashboard">
      <h1>Bem-vindo ao Portal do Paciente</h1>

      <div className="dashboard-cards">
        <div className="card">
          <h3>📅 Consultas Agendadas</h3>
          <p className="card-number">{consultas.length}</p>
        </div>
        <div className="card">
          <h3>👨‍⚕️ Médicos Disponíveis</h3>
          <p className="card-number">--</p>
        </div>
        <div className="card">
          <h3>📋 Histórico Médico</h3>
          <p className="card-number">--</p>
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
          <table className="table">
            <thead>
              <tr>
                <th>Data</th>
                <th>Médico</th>
                <th>Especialidade</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {consultas.map(consulta => (
                <tr key={consulta.id}>
                  <td>{consulta.dataHora ? new Date(consulta.dataHora).toLocaleDateString() : '—'}</td>
                  <td>{consulta.medico?.nome || '—'}</td>
                  <td>{consulta.medico?.especialidade || '—'}</td>
                  <td>
                    <span className={`badge ${consulta.canceladaEm ? 'badge-danger' : 'badge-success'}`}>
                      {consulta.canceladaEm ? 'Cancelada' : 'Agendada'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
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
    </div>
  );
}
