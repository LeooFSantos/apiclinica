# API Clínica

Sistema completo de gestão de clínica com backend Spring Boot e frontend React, incluindo autenticação JWT, perfis de acesso e fluxo de aprovação de médicos.

## Stack
- **Backend:** Java 21, Spring Boot 3, Spring Security, JPA/Hibernate
- **Frontend:** React 18 + React Router
- **Banco:** PostgreSQL
- **Infra:** Docker/Docker Compose

## Funcionalidades
- Autenticação JWT e controle por roles (ADMIN, MEDICO, USER)
- Cadastro e login de pacientes
- Solicitação de cadastro de médicos + aprovação/rejeição pelo admin
- Agendamento de consultas por especialidade e horários disponíveis
- Cancelamento de consultas (paciente e médico) com motivos
- Regras de negócio (antecedência, conflitos, horário de funcionamento)
- Configurações de perfil (dados, senha, endereço) e inativação

## Estrutura do projeto
```
.
├─ src/main/java/inf012/apiclinica
│  ├─ controller
│  ├─ dto
│  ├─ model
│  ├─ repository
│  └─ service
├─ src/main/resources
│  └─ application.properties
├─ frontend
├─ docker-compose.yml
├─ Dockerfile
└─ pom.xml
```

## Requisitos
- Docker e Docker Compose **ou**
- Java 21 + Maven + PostgreSQL

## Executando com Docker
1. Subir banco e backend:
   - `docker compose up -d --build`
2. Subir o frontend:
   - `cd frontend`
   - `npm install`
   - `npm start`
3. Acesse a API em: `http://localhost:8080`

> Se precisar resetar o banco:
> `docker compose down -v`

## Executando localmente (sem Docker)
1. Configure o PostgreSQL e ajuste `src/main/resources/application.properties`.
2. Execute o backend:
   - `./mvnw spring-boot:run` (Windows: `mvnw.cmd`)
3. Execute o frontend:
   - `cd frontend`
   - `npm install`
   - `npm start`
4. Acesse o frontend em: `http://localhost:3000`

## Autenticação
- O login retorna um token JWT.
- O frontend guarda o token e envia em `Authorization: Bearer <token>`.

## Perfis (roles)
- **ADMIN**: aprova/rejeita solicitações de médicos, acesso administrativo.
- **MEDICO**: agenda pessoal, cancelamento de consultas, configurações.
- **USER (PACIENTE)**: agendamento e cancelamento das próprias consultas.

## Endpoints principais
### Auth
- `POST /api/auth/login`
- `GET /api/auth/me`

### Pacientes
- `POST /api/pacientes`
- `GET /api/pacientes/me`
- `PUT /api/pacientes/me`
- `DELETE /api/pacientes/me`

### Médicos
- `POST /api/medicos/requests`
- `POST /api/medicos/requests/{id}/approve`
- `POST /api/medicos/requests/{id}/reject`
- `GET /api/medicos/me`
- `PUT /api/medicos/me`
- `DELETE /api/medicos/me`

### Consultas
- `POST /api/consultas`
- `GET /api/consultas` (paciente)
- `GET /api/consultas/me` (médico)
- `GET /api/consultas/disponibilidade`
- `DELETE /api/consultas/{id}`
- `POST /api/consultas/medico/cancelar-todas`

## Observações
- Cancelamento exige antecedência mínima de 24h.
- Consultas canceladas não bloqueiam novos agendamentos.
- Médicos inativos têm consultas futuras canceladas automaticamente.

## Licença
Projeto acadêmico/educacional.
