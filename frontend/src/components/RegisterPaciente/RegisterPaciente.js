import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { API_ENDPOINTS } from '../../config';
import './RegisterPaciente.css';

export default function RegisterPaciente() {
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    telefone: '',
    cpf: '',
    nomeUsuario: '',
    senha: '',
    logradouro: '',
    numero: '',
    complemento: '',
    bairro: '',
    cidade: '',
    uf: '',
    cep: '',
  });
  const ufs = [
    'AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'
  ];

  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

  const sanitizeValue = (name, value) => {
    if (['cpf', 'cep', 'telefone', 'numero'].includes(name)) {
      return value.replace(/\D/g, '');
    }
    if (name === 'uf') {
      return value.replace(/[^a-zA-Z]/g, '').toUpperCase();
    }
    if (name === 'nomeUsuario') {
      return value.replace(/\s/g, '');
    }
    return value;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    const sanitized = sanitizeValue(name, value);
    setFormData(prev => ({ ...prev, [name]: sanitized }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');
    setCarregando(true);

    try {
      const response = await fetch(`${API_ENDPOINTS.PACIENTES}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || 'Erro ao registrar paciente');
      }

      navigate('/?registered=true');
    } catch (err) {
      setErro(err.message);
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="register-container">
      <div className="register-card">
        <h1>Registrar como Paciente</h1>

        {erro && <div className="alert alert-error">{erro}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Nome Completo</label>
              <input
                type="text"
                name="nome"
                value={formData.nome}
                onChange={handleChange}
                required
                disabled={carregando}
              />
            </div>

            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                pattern="^[^@\s]+@[^@\s]+\.[^@\s]+$"
                title="Digite um email válido (ex: nome@dominio.com)"
                required
                disabled={carregando}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>CPF</label>
              <input
                type="text"
                name="cpf"
                value={formData.cpf}
                onChange={handleChange}
                placeholder="000.000.000-00"
                inputMode="numeric"
                pattern="\d{11}"
                maxLength="11"
                title="Digite 11 números"
                required
                disabled={carregando}
              />
            </div>

            <div className="form-group">
              <label>Telefone</label>
              <input
                type="tel"
                name="telefone"
                value={formData.telefone}
                onChange={handleChange}
                placeholder="(11) 99999-9999"
                inputMode="numeric"
                pattern="\d{10,11}"
                maxLength="11"
                title="Digite 10 ou 11 números"
                required
                disabled={carregando}
              />
            </div>
          </div>

          <hr className="divider" />

          <div className="form-row">
            <div className="form-group">
              <label>Nome de Usuário</label>
              <input
                type="text"
                name="nomeUsuario"
                value={formData.nomeUsuario}
                onChange={handleChange}
                pattern="^(?:[A-Za-z0-9_.]|-){3,30}$"
                maxLength="30"
                title="De 3 a 30 caracteres: letras, números, ponto, hífen ou underscore"
                required
                disabled={carregando}
              />
            </div>

            <div className="form-group">
              <label>Senha</label>
              <input
                type="password"
                name="senha"
                value={formData.senha}
                onChange={handleChange}
                minLength="6"
                maxLength="60"
                title="Mínimo de 6 caracteres"
                required
                disabled={carregando}
              />
            </div>
          </div>

          <hr className="divider" />

          <div className="form-row">
            <div className="form-group">
              <label>Logradouro</label>
              <input
                type="text"
                name="logradouro"
                value={formData.logradouro}
                onChange={handleChange}
                required
                disabled={carregando}
              />
            </div>

            <div className="form-group">
              <label>Número</label>
              <input
                type="text"
                name="numero"
                value={formData.numero}
                onChange={handleChange}
                inputMode="numeric"
                pattern="\d*"
                maxLength="10"
                title="Somente números"
                disabled={carregando}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Complemento</label>
              <input
                type="text"
                name="complemento"
                value={formData.complemento}
                onChange={handleChange}
                disabled={carregando}
              />
            </div>

            <div className="form-group">
              <label>Bairro</label>
              <input
                type="text"
                name="bairro"
                value={formData.bairro}
                onChange={handleChange}
                required
                disabled={carregando}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Cidade</label>
              <input
                type="text"
                name="cidade"
                value={formData.cidade}
                onChange={handleChange}
                required
                disabled={carregando}
              />
            </div>

            <div className="form-group">
              <label>UF</label>
              <select
                name="uf"
                value={formData.uf}
                onChange={handleChange}
                required
                disabled={carregando}
              >
                <option value="">Selecione</option>
                {ufs.map(uf => (
                  <option key={uf} value={uf}>{uf}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>CEP</label>
              <input
                type="text"
                name="cep"
                value={formData.cep}
                onChange={handleChange}
                placeholder="00000-000"
                inputMode="numeric"
                pattern="\d{8}"
                maxLength="8"
                title="Digite 8 números"
                required
                disabled={carregando}
              />
            </div>
          </div>

          <button 
            type="submit" 
            className="btn btn-primary btn-full"
            disabled={carregando}
          >
            {carregando ? <span className="spinner"></span> : 'Registrar'}
          </button>
        </form>

        <p className="register-footer">
          Já tem conta? <Link to="/">Faça login</Link>
        </p>
      </div>
    </div>
  );
}
