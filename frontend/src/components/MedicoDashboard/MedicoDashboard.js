import React, { useState } from 'react';
import { API_ENDPOINTS, getAuthToken } from '../../config';
import './MedicoDashboard.css';

export default function MedicoDashboard() {
  const [aba, setAba] = useState('consultas');
  const [formularioOpen, setFormularioOpen] = useState(false);
  const [consultas, setConsultas] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [dataFiltro, setDataFiltro] = useState(new Date().toISOString().slice(0, 10));

  React.useEffect(() => {
    carregarConsultas(dataFiltro);
  }, [dataFiltro]);

  const carregarConsultas = async (date) => {
    setCarregando(true);
    try {
      const token = getAuthToken();
      // endpoint criado no backend: GET /api/consultas/me?date=YYYY-MM-DD
      const url = `${API_ENDPOINTS.CONSULTAS}/me?date=${date}`;
      const response = await fetch(url, {
        headers: { 'Authorization': `Bearer ${token}` },
      });

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

            {carregando ? (
              <div className="loading">
                <div className="spinner"></div>
              </div>
            ) : consultas.length === 0 ? (
              <p className="no-data">Nenhuma consulta agendada para essa data</p>
            ) : (
              <table className="table">
                <thead>
                  <tr>
                    <th>Data</th>
                    <th>Paciente</th>
                    <th>Horário</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {consultas.map(consulta => {
                    const dateTime = consulta.dataHora || consulta.data || consulta.hora || consulta.horario;
                    const { data, hora } = formatDataHora(dateTime);
                    const pacienteNome = consulta.paciente?.nome || consulta.pacienteNome || consulta.paciente || consulta.nomePaciente || '—';
                    const status = consulta.status || (consulta.cancelado ? 'Cancelada' : 'Agendada');

                    return (
                      <tr key={consulta.id}>
                        <td>{data}</td>
                        <td>{pacienteNome}</td>
                        <td>{hora}</td>
                        <td>
                          <span className={`badge ${status === 'Cancelada' ? 'badge-danger' : 'badge-primary'}`}>
                            {status}
                          </span>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            )}
          </>
        )}

        {aba === 'pacientes' && (
          <>
            <h2>Meus Pacientes</h2>
            <p className="no-data">Dados dos pacientes serão carregados aqui</p>
          </>
        )}
      </div>
    </div>
  );
}
