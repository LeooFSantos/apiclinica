import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { API_ENDPOINTS, authFetch, getAuthUser, clearAuth } from '../../config';
import './Navbar.css';

export default function Navbar() {
  const user = getAuthUser();
  const navigate = useNavigate();
  const [showSettings, setShowSettings] = useState(false);
  const [form, setForm] = useState({
    email: '',
    telefone: '',
    senha: '',
    logradouro: '',
    numero: '',
    complemento: '',
    bairro: '',
    cidade: '',
    uf: '',
    cep: '',
  });
  const [erro, setErro] = useState('');
  const [mensagem, setMensagem] = useState('');
  const [showInativar, setShowInativar] = useState(false);
  const [aceiteInativacao, setAceiteInativacao] = useState(false);
  const [carregando, setCarregando] = useState(false);

  const userType = localStorage.getItem('userType') || 'USER';

  useEffect(() => {
    if (showSettings) {
      carregarPerfil();
    }
  }, [showSettings]);

  const carregarPerfil = async () => {
    try {
      setErro('');
      setMensagem('');
      const endpoint = userType === 'MEDICO' ? API_ENDPOINTS.MEDICO_ME : API_ENDPOINTS.PACIENTE_ME;
      const resp = await authFetch(endpoint);
      if (!resp.ok) {
        const text = await resp.text();
        throw new Error(text || 'Erro ao carregar perfil');
      }
      const data = await resp.json();
      setForm({
        email: data.email || '',
        telefone: data.telefone || '',
        senha: '',
        logradouro: data.endereco?.logradouro || '',
        numero: data.endereco?.numero || '',
        complemento: data.endereco?.complemento || '',
        bairro: data.endereco?.bairro || '',
        cidade: data.endereco?.cidade || '',
        uf: data.endereco?.uf || '',
        cep: data.endereco?.cep || '',
      });
    } catch (e) {
      setErro(e.message);
    }
  };

  const handleLogout = () => {
    clearAuth();
    navigate('/');
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const salvarConfiguracoes = async () => {
    try {
      setErro('');
      setMensagem('');
      setCarregando(true);
      const endpoint = userType === 'MEDICO' ? API_ENDPOINTS.MEDICO_ME : API_ENDPOINTS.PACIENTE_ME;
      const resp = await authFetch(endpoint, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(form),
      });
      if (!resp.ok) {
        const text = await resp.text();
        throw new Error(text || 'Erro ao salvar configurações');
      }
      setMensagem('Configurações atualizadas');
    } catch (e) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  };

  const confirmarInativacao = async () => {
    try {
      setErro('');
      setCarregando(true);
      const endpoint = userType === 'MEDICO' ? API_ENDPOINTS.MEDICO_ME : API_ENDPOINTS.PACIENTE_ME;
      if (userType === 'MEDICO') {
        const cancelarResp = await authFetch(API_ENDPOINTS.CONSULTAS_MEDICO_CANCELAR_TODAS, {
          method: 'POST',
        });
        if (!cancelarResp.ok) {
          const text = await cancelarResp.text();
          throw new Error(text || 'Erro ao cancelar consultas do médico');
        }
      }
      const resp = await authFetch(endpoint, {
        method: 'DELETE',
      });
      if (!resp.ok) {
        const text = await resp.text();
        throw new Error(text || 'Erro ao inativar cadastro');
      }
      clearAuth();
      navigate('/');
    } catch (e) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  };

  return (
    <>
      <nav className="navbar">
        <div className="navbar-container">
          <div className="navbar-brand">
            <h2>🏥 API Clínica</h2>
          </div>

          <div className="navbar-menu">
            <span className="user-info">
              👤 {user?.nomeUsuario}
            </span>
            <button className="btn btn-secondary btn-small" onClick={() => setShowSettings(true)}>
              Configurações
            </button>
            <button className="btn btn-danger btn-small" onClick={handleLogout}>
              Sair
            </button>
          </div>
        </div>
      </nav>
      {showSettings && (
      <div className="modal-backdrop">
        <div className="modal">
          <h3>Configurações</h3>
          <div className="modal-body">
            {erro && <div className="alert alert-error">{erro}</div>}
            {mensagem && <div className="alert alert-success">{mensagem}</div>}

            <div className="form-row">
              <div className="form-group">
                <label>Email</label>
                <input name="email" value={form.email} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>Telefone</label>
                <input name="telefone" value={form.telefone} onChange={handleChange} />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Senha</label>
                <input type="password" name="senha" value={form.senha} onChange={handleChange} placeholder="Nova senha" />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Logradouro</label>
                <input name="logradouro" value={form.logradouro} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>Número</label>
                <input name="numero" value={form.numero} onChange={handleChange} />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Complemento</label>
                <input name="complemento" value={form.complemento} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>Bairro</label>
                <input name="bairro" value={form.bairro} onChange={handleChange} />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Cidade</label>
                <input name="cidade" value={form.cidade} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>UF</label>
                <input name="uf" value={form.uf} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>CEP</label>
                <input name="cep" value={form.cep} onChange={handleChange} />
              </div>
            </div>
          </div>

          <div className="modal-actions">
            <button className="btn btn-outline" onClick={() => setShowSettings(false)} disabled={carregando}>Fechar</button>
            <button className="btn btn-primary" onClick={salvarConfiguracoes} disabled={carregando}>Salvar</button>
            <button className="btn btn-danger" onClick={() => setShowInativar(true)} disabled={carregando}>Excluir cadastro</button>
          </div>
        </div>
      </div>
    )}

      {showInativar && (
      <div className="modal-backdrop">
        <div className="modal">
          <h3>Inativar cadastro</h3>
          <div className="modal-body">
            <p>Seu cadastro não será excluído, apenas inativado. Após a inativação você não poderá mais fazer login.</p>
            <label style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
              <input type="checkbox" checked={aceiteInativacao} onChange={(e) => setAceiteInativacao(e.target.checked)} />
              Eu li os termos e estou ciente da inativação da conta
            </label>
            {erro && <div className="alert alert-error">{erro}</div>}
          </div>
          <div className="modal-actions">
            <button className="btn btn-outline" onClick={() => setShowInativar(false)} disabled={carregando}>Cancelar</button>
            <button className="btn btn-danger" onClick={confirmarInativacao} disabled={!aceiteInativacao || carregando}>
              Inativar conta
            </button>
          </div>
        </div>
      </div>
    )}
    </>
  );
}
