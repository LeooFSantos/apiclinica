// Configuração da API (CRA): variáveis de ambiente precisam começar com REACT_APP_
// Ex.: REACT_APP_API_URL=http://localhost:8080/api
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const API_ENDPOINTS = {
  LOGIN: `${API_BASE_URL}/auth/login`,
  REFRESH: `${API_BASE_URL}/auth/refresh`,
  ME: `${API_BASE_URL}/auth/me`,
  PACIENTE_ME: `${API_BASE_URL}/pacientes/me`,
  MEDICO_ME: `${API_BASE_URL}/medicos/me`,
  PACIENTES: `${API_BASE_URL}/pacientes`,
  MEDICOS: `${API_BASE_URL}/medicos`,
  MEDICOS_ATIVOS_COUNT: `${API_BASE_URL}/medicos/ativos/count`,
  CONSULTAS: `${API_BASE_URL}/consultas`,
  CONSULTAS_DISPONIBILIDADE: `${API_BASE_URL}/consultas/disponibilidade`,
  CONSULTAS_MEDICO_CANCELAR_TODAS: `${API_BASE_URL}/consultas/medico/cancelar-todas`,
  MEDICOS_REQUESTS: `${API_BASE_URL}/medicos/requests`,

  // Envio manual de e-mail (integração com microserviço via gateway, se houver rota)
  // Obs: o sistema também envia automaticamente em cadastro/agendamento/cancelamento.
  EMAIL_SEND: `${API_BASE_URL}/emails/send`,
};

export const AUTH_TOKEN_KEY = 'authToken';
export const AUTH_USER_KEY = 'authUser';

// Utilitários de autenticação
export const setAuthToken = (token, nomeUsuario, tipo) => {
  localStorage.setItem(AUTH_TOKEN_KEY, token);
  localStorage.setItem(AUTH_USER_KEY, JSON.stringify({ nomeUsuario, tipo }));
};

export const getAuthToken = () => {
  return localStorage.getItem(AUTH_TOKEN_KEY);
};

export const buildAuthHeaders = (headers = {}) => {
  const token = getAuthToken();
  if (!token) return { ...headers };
  return { ...headers, Authorization: `Bearer ${token}` };
};

export const authFetch = (url, options = {}) => {
  const mergedHeaders = buildAuthHeaders(options.headers || {});
  return fetch(url, { ...options, headers: mergedHeaders });
};

export const getAuthUser = () => {
  const user = localStorage.getItem(AUTH_USER_KEY);
  return user ? JSON.parse(user) : null;
};

export const clearAuth = () => {
  localStorage.removeItem(AUTH_TOKEN_KEY);
  localStorage.removeItem(AUTH_USER_KEY);
};

export const isAuthenticated = () => {
  return getAuthToken() !== null;
};
