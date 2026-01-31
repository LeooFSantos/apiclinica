import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { API_ENDPOINTS, setAuthToken } from '../../config';
import './Login.css';

export default function Login() {
  const [nomeUsuario, setNomeUsuario] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setErro('');
    setCarregando(true);

    try {
      const response = await fetch(API_ENDPOINTS.LOGIN, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nomeUsuario, senha }),
      });

      if (!response.ok) {
        throw new Error('Credenciais inválidas');
      }

      const data = await response.json();
      setAuthToken(data.token, data.nomeUsuario, 'USER');
      // Determinar tipo de usuário chamando /api/auth/me
      try {
        const meResp = await fetch(API_ENDPOINTS.ME, {
          headers: { 'Authorization': `Bearer ${data.token}` }
        });
        if (meResp.ok) {
          const me = await meResp.json();
          const roles = me.roles || [];
          const tipo = roles.includes('ROLE_ADMIN') || roles.includes('ADMIN') ? 'ADMIN' : roles.includes('ROLE_MEDICO') || roles.includes('MEDICO') ? 'MEDICO' : 'USER';
          localStorage.setItem('userType', tipo);
        } else {
          const tipoUsuario = nomeUsuario === 'admin' ? 'ADMIN' : 'USER';
          localStorage.setItem('userType', tipoUsuario);
        }
      } catch (e) {
        const tipoUsuario = nomeUsuario === 'admin' ? 'ADMIN' : 'USER';
        localStorage.setItem('userType', tipoUsuario);
      }
      
      navigate('/dashboard');
    } catch (err) {
      setErro(err.message || 'Erro ao fazer login');
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1>🏥 API Clínica</h1>
          <p>Sistema de Gestão de Clínica</p>
        </div>

        {erro && <div className="alert alert-error">{erro}</div>}

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="nomeUsuario">Nome de Usuário</label>
            <input
              id="nomeUsuario"
              type="text"
              value={nomeUsuario}
              onChange={(e) => setNomeUsuario(e.target.value)}
              placeholder="Digite seu nome de usuário"
              required
              disabled={carregando}
            />
          </div>

          <div className="form-group">
            <label htmlFor="senha">Senha</label>
            <input
              id="senha"
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              placeholder="Digite sua senha"
              required
              disabled={carregando}
            />
          </div>

          <button 
            type="submit" 
            className="btn btn-primary btn-full"
            disabled={carregando}
          >
            {carregando ? <span className="spinner"></span> : 'Entrar'}
          </button>
        </form>

        <div className="login-register-links">
          <p>Não tem conta?</p>
          <Link to="/registrar-paciente" className="btn btn-outline btn-full">
            Registrar como Paciente
          </Link>
          <Link to="/registrar-medico" className="btn btn-outline btn-full">
            Registrar como Médico
          </Link>
        </div>

        <div className="login-footer">
          <p>Credenciais de teste:</p>
          <code>admin / admin123</code>
          <code>paciente1 / pac1</code>
        </div>
      </div>

      <div className="login-background"></div>
    </div>
  );
}
