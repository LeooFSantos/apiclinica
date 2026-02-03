import React, { useState, useEffect } from 'react';
import { API_ENDPOINTS, authFetch } from '../../config';
import './AdminDashboard.css';
import EmailSender from '../EmailSender/EmailSender';

export default function AdminDashboard() {
  const [solicitacoes, setSolicitacoes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');
  const [selected, setSelected] = useState(null);
  const [hovered, setHovered] = useState(null);

  useEffect(() => {
    carregarSolicitacoes();
  }, []);

  const carregarSolicitacoes = async () => {
    try {
      const response = await authFetch(API_ENDPOINTS.MEDICOS_REQUESTS);

      if (!response.ok) throw new Error('Erro ao carregar solicitações');

      const data = await response.json();
      setSolicitacoes(data);
    } catch (err) {
      setErro(err.message);
    } finally {
      setCarregando(false);
    }
  };

  const aprovarSolicitacao = async (id) => {
    try {
      const response = await authFetch(
        `${API_ENDPOINTS.MEDICOS_REQUESTS}/${id}/approve`,
        {
          method: 'POST',
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Erro ao aprovar solicitação');
      }

      setErro('');
      setSolicitacoes(solicitacoes.filter(s => s.id !== id));
      setSelected(null);
    } catch (err) {
      setErro(err.message);
    }
  };

  const rejeitarSolicitacao = async (id) => {
    try {
      const response = await authFetch(
        `${API_ENDPOINTS.MEDICOS_REQUESTS}/${id}/reject`,
        {
          method: 'POST',
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Erro ao rejeitar solicitação');
      }

      setErro('');
      setSolicitacoes(solicitacoes.filter(s => s.id !== id));
      setSelected(null);
    } catch (err) {
      setErro(err.message);
    }
  };

  const abrirDetalhes = (sol) => {
    setSelected(sol);
  };

  const fecharDetalhes = () => setSelected(null);

  const onHover = (sol) => setHovered(sol);
  const onLeave = () => setHovered(null);

  return (
    <div className="admin-dashboard">
      <h1>Painel de Administrador</h1>

      <div className="dashboard-cards">
        <div className="card">
          <h3>📋 Solicitações Pendentes</h3>
          <p className="card-number">{solicitacoes.length}</p>
        </div>
      </div>

      {erro && <div className="alert alert-error">{erro}</div>}

      <div className="card">
        <h2>Solicitações de Registro de Médicos</h2>

        {carregando ? (
          <div className="loading">
            <div className="spinner"></div>
            <p>Carregando...</p>
          </div>
        ) : solicitacoes.length === 0 ? (
          <p className="no-data">Nenhuma solicitação pendente</p>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>CRM</th>
                <th>Especialidade</th>
                <th>Usuário</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {solicitacoes.map(solicitacao => (
                <tr key={solicitacao.id} onMouseEnter={() => onHover(solicitacao)} onMouseLeave={onLeave} onClick={() => abrirDetalhes(solicitacao)} style={{cursor:'pointer'}}>
                  <td>{solicitacao.nome}</td>
                  <td>{solicitacao.crm}</td>
                  <td>{solicitacao.especialidade}</td>
                  <td>{solicitacao.nomeUsuario}</td>
                  <td>
                    <button
                      className="btn btn-secondary btn-small"
                      onClick={(e) => {
                        e.stopPropagation();
                        aprovarSolicitacao(solicitacao.id);
                      }}
                    >
                      ✓ Aprovar
                    </button>
                    <button
                      className="btn btn-danger btn-small"
                      onClick={(e) => {
                        e.stopPropagation();
                        rejeitarSolicitacao(solicitacao.id);
                      }}
                      style={{marginLeft: '8px'}}
                    >
                      ⚠ Rejeitar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
      {hovered && (
        <div className="hover-card">
          <p><strong>{hovered.nome}</strong> — {hovered.crm}</p>
          <p>{hovered.especialidade} • {hovered.nomeUsuario}</p>
          <p>{hovered.email} • {hovered.telefone}</p>
        </div>
      )}

      {selected && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>Detalhes da Solicitação</h3>
            <div className="modal-body">
              <p><strong>Nome:</strong> {selected.nome}</p>
              <p><strong>CRM:</strong> {selected.crm}</p>
              <p><strong>UF do CRM:</strong> {selected.crmUf}</p>
              <p><strong>Especialidade:</strong> {selected.especialidade}</p>
              <p><strong>Usuário:</strong> {selected.nomeUsuario}</p>
              <p><strong>Email:</strong> {selected.email}</p>
              <p><strong>Telefone:</strong> {selected.telefone}</p>
              <p><strong>Endereço:</strong> {selected.logradouro} {selected.numero} {selected.complemento} - {selected.bairro}, {selected.cidade}/{selected.uf} - {selected.cep}</p>
            </div>
            <div className="modal-actions">
              <button className="btn btn-outline" onClick={fecharDetalhes}>Fechar</button>
              <button className="btn btn-danger" onClick={() => rejeitarSolicitacao(selected.id)}>Confirmar Rejeitar</button>
              <button className="btn btn-primary" onClick={() => { aprovarSolicitacao(selected.id); fecharDetalhes(); }}>Confirmar Aprovar</button>
            </div>
          </div>
        </div>
      )}
    <div style={{ marginTop: 18 }}>
      <EmailSender />
    </div>
    </div>
  );
}
