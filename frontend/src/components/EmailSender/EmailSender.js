import React, { useState } from 'react';
import { API_ENDPOINTS, authFetch } from '../../config';
import './EmailSender.css';

export default function EmailSender({ defaultTo = '' }) {
  const [to, setTo] = useState(defaultTo);
  const [subject, setSubject] = useState('Confirmação - API Clínica');
  const [body, setBody] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [mensagem, setMensagem] = useState('');
  const [erro, setErro] = useState('');

  const enviar = async (e) => {
    e.preventDefault();
    setMensagem('');
    setErro('');
    setEnviando(true);

    try {
      const response = await authFetch(API_ENDPOINTS.EMAIL_SEND, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ to, subject, body }),
      });

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(data.message || 'Erro ao enviar e-mail');
      }

      setMensagem(data.message || 'E-mail enviado com sucesso!');
      setBody('');
    } catch (err) {
      setErro(err.message);
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="email-card">
      <h3>Enviar e-mail</h3>

      {mensagem && <div className="alert success">{mensagem}</div>}
      {erro && <div className="alert error">{erro}</div>}

      <form onSubmit={enviar} className="email-form">
        <label>
          Para
          <input
            type="email"
            value={to}
            onChange={(e) => setTo(e.target.value)}
            placeholder="ex: paciente@email.com"
            required
          />
        </label>

        <label>
          Assunto
          <input
            type="text"
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
            required
          />
        </label>

        <label>
          Mensagem
          <textarea
            value={body}
            onChange={(e) => setBody(e.target.value)}
            placeholder="Digite o conteúdo do e-mail..."
            rows={6}
            required
          />
        </label>

        <button type="submit" disabled={enviando}>
          {enviando ? 'Enviando...' : 'Enviar'}
        </button>
      </form>
      <p className="hint">
        Dica: este endpoint é roteado pelo Gateway (8080) para o microserviço (8082) via Eureka.
      </p>
    </div>
  );
}
