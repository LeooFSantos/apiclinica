# API Clínica — Arquitetura de Microserviços

Sistema completo de gestão de clínica médica desenvolvido com arquitetura de microserviços, utilizando Spring Boot, Spring Cloud (Eureka), React, Docker e Swagger/OpenAPI.

O projeto implementa autenticação JWT, controle de acesso por perfis, fluxo de aprovação de médicos, agendamento de consultas e envio de notificações por e-mail através de um microserviço dedicado.

---

## Arquitetura Geral

- Frontend (React) consome apenas a API principal
- API Principal atua como Gateway
- Eureka Server realiza registro e descoberta de serviços
- Email Service é responsável exclusivamente pelo envio de e-mails
- PostgreSQL é utilizado para persistência de dados
- MailHog simula o envio de e-mails em ambiente de desenvolvimento

Fluxo simplificado:

Frontend  
→ API Principal (Gateway)  
→ Eureka (Service Discovery)  
→ Email Service  
→ MailHog (SMTP de desenvolvimento)

---

## Stack Tecnológica

### Backend
- Java 21
- Spring Boot 3
- Spring Security com JWT
- Spring Data JPA (Hibernate)
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway
- SpringDoc OpenAPI (Swagger)

### Frontend
- React 18
- React Router
- Fetch API / Axios

### Infraestrutura
- Docker
- Docker Compose
- PostgreSQL
- MailHog (SMTP de desenvolvimento)

---

## Funcionalidades

### Autenticação e Segurança
- Autenticação via JWT
- Controle de acesso por roles:
  - ADMIN
  - MEDICO
  - USER (Paciente)

### Gestão de Usuários
- Cadastro e login de pacientes
- Solicitação de cadastro de médicos
- Aprovação e rejeição de médicos pelo administrador
- Atualização de dados de perfil
- Inativação de contas

### Consultas Médicas
- Agendamento de consultas por especialidade
- Consulta de horários disponíveis
- Cancelamento de consultas por paciente ou médico
- Cancelamento em massa de consultas de um médico
- Regras de negócio:
  - Antecedência mínima para cancelamento
  - Prevenção de conflitos de horário
  - Respeito ao horário de funcionamento da clínica

### Envio de E-mails (Microserviço)
Os e-mails são enviados automaticamente nas seguintes situações:
- Cadastro de paciente (mensagem de boas-vindas)
- Agendamento de consulta
- Cancelamento de consulta
- Aprovação ou rejeição do cadastro de médico

O envio é feito pela API principal, que localiza o Email Service dinamicamente através do Eureka.

---

## Estrutura do Projeto

```
.
├─ apiclinica
├─ email-service
├─ eureka-server
├─ frontend
├─ docker-compose.yml
├─ Dockerfile
└─ README.md
```

---

## Serviços e Portas

| Serviço | Porta | Descrição |
|------|------|---------|
| Frontend | 3000 | Interface do usuário |
| API Principal (Gateway) | 8080 | Backend principal |
| Email Service | 8082 | Microserviço de e-mails |
| Eureka Server | 8761 | Registro e descoberta de serviços |
| MailHog (Web UI) | 8025 | Visualização de e-mails |
| MailHog (SMTP) | 1025 | SMTP de desenvolvimento |
| PostgreSQL | 5432 | Banco de dados |

---

## Executando com Docker

```bash
docker compose down -v --rmi local
docker compose build --no-cache
docker compose up
```

---

## Executando o Front

```bash
cd frontend
npm install
npm start
```

---

## Endereços Importantes

- Frontend: http://localhost:3000
- API Principal: http://localhost:8080
- Swagger API Principal: http://localhost:8080/swagger-ui.html
- Swagger Email Service: http://localhost:8082/swagger-ui/index.html
- Eureka Server: http://localhost:8761
- MailHog: http://localhost:8025

---

## Autenticação

O login retorna um token JWT que deve ser enviado no header:

Authorization: Bearer <token>

---


Projeto acadêmico e educacional.
