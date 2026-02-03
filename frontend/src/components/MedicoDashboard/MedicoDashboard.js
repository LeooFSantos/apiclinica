import React, { useState } from 'react';
import { API_ENDPOINTS, authFetch } from '../../config';
import './MedicoDashboard.css';
import EmailSender from '../EmailSender/EmailSender';

export default function MedicoDashboard() {
  const [aba, setAba] = useState('consultas');
  const [formularioOpen, setFormularioOpen] = useState(false);
  const [consultas, setConsultas] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [dataFiltro, setDataFiltro] = useState(new Date().toISOString().slice(0, 10));
  const [consultaCancelamento, setConsultaCancelamento] = useState(null);
  const [motivoCancelamento, setMotivoCancelamento] = useState('MEDICO_CANCELOU');
  const [erroCancelamento, setErroCancelamento] = useState('');
  const [cancelando, setCancelando] = useState(false);
  const [filtroStatus, setFiltroStatus] = useState('agendadas');

  React.useEffect(() => {
    carregarConsultas(dataFiltro);
  }, [dataFiltro]);

  const carregarConsultas = async (date) => {
    setCarregando(true);
    try {
      // endpoint criado no backend: GET /api/consultas/me?date=YYYY-MM-DD
      const url = `${API_ENDPOINTS.CONSULTAS}/me?date=${date}`;
      const response = await authFetch(url);

      if (response.ok) {
        const data = await response.json();
        // backend pode retornar array ou objeto paginado
        const list = Array.isArray(data) ? data : data.content || [];
        setConsultas(list);
      } else {
        console.error('Falha ao buscar consultas:', response.status);
      }
    } catch (err) {
      console.error('Erro ao carregar consultas:', err);
    } finally {
      setCarregando(false);
    }
  };

  const formatDataHora = (value) => {
    if (!value) return { data: '—', hora: '—' };
    try {
      const dt = new Date(value);
      const data = dt.toLocaleDateString();
      const hora = dt.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      return { data, hora };
    } catch (e) {
      return { data: value, hora: '' };
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
    setMotivoCancelamento('MEDICO_CANCELOU');
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

      fecharCancelamento();
      carregarConsultas(dataFiltro);
    } catch (err) {
      setErroCancelamento(err.message);
    } finally {
      setCancelando(false);
    }
  };

  const totalConsultas = consultas.length;
  const totalCanceladas = consultas.filter((c) => c.canceladaEm).length;
  const totalAgendadas = totalConsultas - totalCanceladas;
  const consultasFiltradas = consultas.filter((c) => {
    if (filtroStatus === 'agendadas') return !c.canceladaEm;
    if (filtroStatus === 'canceladas') return !!c.canceladaEm;
    return true;
  });

  return (
    <div className="medico-dashboard">
      <h1>Painel do Médico</h1>

      <div className="dashboard-cards">
        <div className="card">
          <h3>📅 Consultas Hoje</h3>
          <p className="card-number">{consultas.length}</p>
        </div>
        <div className="card">
          <h3>👥 Pacientes</h3>
          <p className="card-number">--</p>
        </div>
        <div className="card">
          <h3>✓ Consultas Realizadas</h3>
          <p className="card-number">--</p>
        </div>
      </div>

      <div className="tabs">
        <button
          className={`tab ${aba === 'consultas' ? 'active' : ''}`}
          onClick={() => setAba('consultas')}
        >
          📅 Minhas Consultas
        </button>
        <button
          className={`tab ${aba === 'pacientes' ? 'active' : ''}`}
          onClick={() => setAba('pacientes')}
        >
          👥 Meus Pacientes
        </button>
        <button
          className={`tab ${aba === 'email' ? 'active' : ''}`}
          onClick={() => setAba('email')}
        >
          ✉️ Enviar E-mail
        </button>
      </div>

      <div className="card">
        {aba === 'consultas' && (
          <>
            <h2>Minhas Consultas</h2>

            <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', marginBottom: '1rem' }}>
              <label>Data:</label>
              <input
                type="date"
                value={dataFiltro}
                onChange={(e) => setDataFiltro(e.target.value)}
              />
            </div>

            <div className="tabs" style={{ marginBottom: '1rem' }}>
              <button
                className={`tab ${filtroStatus === 'agendadas' ? 'active' : ''}`}
                onClick={() => setFiltroStatus('agendadas')}
              >
                Agendadas ({totalAgendadas})
              </button>
              <button
                className={`tab ${filtroStatus === 'canceladas' ? 'active' : ''}`}
                onClick={() => setFiltroStatus('canceladas')}
              >
                Canceladas ({totalCanceladas})
              </button>
              <button
                className={`tab ${filtroStatus === 'todas' ? 'active' : ''}`}
                onClick={() => setFiltroStatus('todas')}
              >
                Todas ({totalConsultas})
              </button>
            </div>

            {carregando ? (
              <div className="loading">
                <div className="spinner"></div>
              </div>
            ) : consultasFiltradas.length === 0 ? (
              <p className="no-data">Nenhuma consulta para esse filtro</p>
            ) : (
              <table className="table">
                <thead>
                  <tr>
                    <th>Data</th>
                    <th>Paciente</th>
                    <th>Horário</th>
                    <th>Status</th>
                    <th>Motivo</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {consultasFiltradas.map(consulta => {
                    const dateTime = consulta.dataHora || consulta.data || consulta.hora || consulta.horario;
                    const { data, hora } = formatDataHora(dateTime);
                    const pacienteNome = consulta.paciente?.nome || consulta.pacienteNome || consulta.paciente || consulta.nomePaciente || '—';
                    const status = consulta.status || (consulta.canceladaEm ? 'Cancelada' : 'Agendada');
                    const motivo = consulta.motivoCancelamento || consulta.motivo || '';

                    return (
                      <tr key={consulta.id}>
                        <td>{data}</td>
                        <td>{pacienteNome}</td>
                        <td>{hora}</td>
                        <td>
                          <span
                            className={`badge ${status === 'Cancelada' ? 'badge-danger' : 'badge-primary'}`}
                            title={status === 'Cancelada' ? `Motivo: ${motivo || '—'}` : ''}
                          >
                            {status}
                          </span>
                        </td>
                        <td>{status === 'Cancelada' ? (motivo || '—') : '—'}</td>
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
            )}
          </>
        )}

        {aba === 'email' && (
          <EmailSender />
        )}

        {aba === 'pacientes' && (
          <>
            <h2>Meus Pacientes</h2>
            <p className="no-data">Dados dos pacientes serão carregados aqui</p>
          </>
        )}
      </div>

      {consultaCancelamento && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>Cancelar Consulta</h3>
            <div className="modal-body">
              <p><strong>Consulta:</strong> {consultaCancelamento.dataHora ? new Date(consultaCancelamento.dataHora).toLocaleString() : '—'}</p>
              <p><strong>Paciente:</strong> {consultaCancelamento.paciente?.nome || '—'}</p>

              <div className="form-group">
                <label>Motivo do cancelamento</label>
                <select value={motivoCancelamento} onChange={(e) => setMotivoCancelamento(e.target.value)}>
                  <option value="MEDICO_CANCELOU">Médico cancelou</option>
                  <option value="PROBLEMA_AGENDA">Problema de agenda</option>
                  <option value="EMERGENCIA">Emergência</option>
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
