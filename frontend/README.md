# API Clínica - Frontend React

Frontend moderno e responsivo para o Sistema de Gestão de Clínica.

## Características

- ✨ Interface intuitiva e atrativa
- 🔐 Autenticação com JWT
- 📱 Design responsivo (mobile-first)
- 👥 Painéis específicos para cada tipo de usuário
- 🎨 Design moderno com gradientes e animações
- ⚡ Performance otimizada

## Tipos de Usuário

### Administrador
- Gerenciar solicitações de registro de médicos
- Aprovar médicos
- Visualizar estatísticas da clínica

### Médico
- Visualizar consultas agendadas
- Gerenciar pacientes
- Acessar histórico de consultas

### Paciente
- Agendar consultas
- Visualizar histórico de consultas
- Gerenciar dados pessoais

## Instalação

```bash
cd frontend
npm install
```

## Desenvolvimento

```bash
npm start
```

A aplicação estará disponível em `http://localhost:3000`

## Variáveis de Ambiente

```bash
REACT_APP_API_URL=http://localhost:8080/api
```

## Build

```bash
npm run build
```

## Credenciais de Teste

- **Admin**: `admin` / `admin123`
- **Paciente**: `paciente1` / `pac1`

## Tecnologias

- React 18
- React Router v6
- CSS3 (Flexbox, Grid, Gradientes)
- JavaScript ES6+
