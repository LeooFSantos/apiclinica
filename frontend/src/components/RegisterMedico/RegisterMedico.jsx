import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { API_ENDPOINTS } from '../../config';
import './RegisterMedico.css';

export default function RegisterMedico() {
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    telefone: '',
    crm: '',
    crmUf: '',
    especialidade: 'CARDIOLOGIA',
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

  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState(false);
  const navigate = useNavigate();

  const especialidades = [
    'CARDIOLOGIA',
    'DERMATOLOGIA',
    'OFTALMOLOGIA',
    'ORTOPEDIA',
    'PNEUMOLOGIA',
    'GASTROENTEROLOGIA',
    'GINECOLOGIA',
  ];

  const ufs = [
    'AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'
  ];

  const sanitizeValue = (name, value) => {
    if (['cep', 'telefone', 'numero', 'crm'].includes(name)) {
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
      const payload = {
        nome: formData.nome,
        email: formData.email,
        telefone: formData.telefone,
        crm: formData.crm,
        crmUf: formData.crmUf,
        especialidade: formData.especialidade,
        nomeUsuario: formData.nomeUsuario,
        senha: formData.senha,
        endereco: {
          logradouro: formData.logradouro,
          numero: formData.numero,
          complemento: formData.complemento,
          bairro: formData.bairro,
          cidade: formData.cidade,
          uf: formData.uf,
          cep: formData.cep,
        },
      };

      const response = await fetch(`${API_ENDPOINTS.MEDICOS_REQUESTS}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || 'Erro ao enviar solicitação');
      }

      setSucesso(true);
      setTimeout(() => navigate('/'), 3000);
    } catch (err) {
      setErro(err.message);
    } finally {
      setCarregando(false);
    }
  };

  if (sucesso) {
    return (
      <div className="register-container">
        <div className="register-card">
          <div className="success-message">
            <h2>✓ Solicitação Enviada!</h2>
            <p>Sua solicitação foi recebida e está aguardando aprovação do administrador.</p>
            <p>Você será notificado quando sua conta for aprovada.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="register-container">
      <div className="register-card">
        <h1>Registrar como Médico</h1>

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
              <label>CRM</label>
              <input
                type="text"
                name="crm"
                value={formData.crm}
                onChange={handleChange}
                inputMode="numeric"
                maxLength="20"
                pattern="\d{3,20}"
                title="Digite de 3 a 20 números"
                required
                disabled={carregando}
              />
            </div>

            <div className="form-group">
              <label>UF do CRM</label>
              <select
                name="crmUf"
                value={formData.crmUf}
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
              <label>Especialidade</label>
              <select
                name="especialidade"
                value={formData.especialidade}
                onChange={handleChange}
                disabled={carregando}
              >
                {especialidades.map(esp => (
                  <option key={esp} value={esp}>{esp}</option>
                ))}
              </select>
            </div>
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
            {carregando ? <span className="spinner"></span> : 'Enviar Solicitação'}
          </button>
        </form>

        <p className="register-footer">
          Já tem conta? <Link to="/">Faça login</Link>
        </p>
      </div>
    </div>
  );
}
