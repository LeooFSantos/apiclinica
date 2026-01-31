# Setup Completo - API Clínica

## 🚀 Início Rápido

Este documento descreve como configurar e executar a aplicação completa (Backend + Frontend).

## Pré-requisitos

- **Java 21** ou superior
- **Node.js 18** ou superior
- **Docker & Docker Compose** (opcional, para ambiente containerizado)
- **PostgreSQL 16** (opcional, se não usar Docker)
- **Maven 3.9.6** ou superior

## Backend - Java Spring Boot

### 1. Configurar Backend

```bash
cd c:\Users\Leo\Documents\apiclinica
```

### 2. Compilar com Maven

```bash
mvn clean install
```

### 3. Executar a Aplicação

**Opção A: Com Docker Compose (Recomendado)**

```bash
docker-compose up -d
```

Isso inicia:
- API Java na porta `8080`
- PostgreSQL na porta `5432`

**Opção B: Executar Localmente**

```bash
mvn spring-boot:run
```

> Certifique-se de que o PostgreSQL está rodando e a configuração em `application.properties` está correta.

### 4. Verificar se está rodando

```bash
curl http://localhost:8080/api/auth/login
```

## Frontend - React

### 1. Instalar Dependências

```bash
cd frontend
npm install
```

### 2. Configurar Variáveis de Ambiente (opcional)

Copie `.env.example` para `.env`:

```bash
cp .env.example .env
```

Edite `.env` se necessário:
```
REACT_APP_API_URL=http://localhost:8080/api
```

### 3. Executar em Desenvolvimento

```bash
npm start
```

A aplicação estará disponível em `http://localhost:3000`

### 4. Build para Produção

```bash
npm run build
```

## 🔐 Credenciais de Teste

| Tipo | Usuário | Senha |
|------|---------|-------|
| Admin | `admin` | `admin123` |
| Paciente | `paciente1` | `pac1` |

## 📋 Fluxo de Teste

### 1. Login como Admin
```
Usuário: admin
Senha: admin123
```
- Dashboard mostrará solicitações de médicos pendentes

### 2. Registrar Novo Paciente
- Clique em "Registrar como Paciente" na tela de login
- Preencha o formulário completo
- Após registro, faça login com as credenciais criadas

### 3. Registrar Novo Médico
- Clique em "Registrar como Médico" na tela de login
- Preencha o formulário com dados de médico
- Admin deve aprovar a solicitação
- Após aprovação, médico pode fazer login

## 🐳 Docker Compose

### Estrutura de Serviços

```yaml
services:
  api:
    - Java Spring Boot API
    - Porta: 8080
    
  postgres:
    - Banco de dados PostgreSQL
    - Porta: 5432
    
  frontend: (opcional)
    - React Frontend
    - Porta: 3000
```

### Iniciar Tudo

```bash
docker-compose up -d
```

### Parar Tudo

```bash
docker-compose down
```

### Ver Logs

```bash
docker-compose logs -f api
```

## 🛠️ Troubleshooting

### Erro: "Cannot connect to PostgreSQL"
- Verifique se o Docker está rodando: `docker ps`
- Aguarde a inicialização do PostgreSQL (pode levar alguns segundos)
- Verifique a configuração em `application.properties`

### Erro: "Port 8080 already in use"
- Mude a porta em `application.properties`: `server.port=8081`

### Erro: "Frontend não consegue conectar à API"
- Verifique `frontend/.env` ou `src/config.js`
- Certifique-se de que `REACT_APP_API_URL` aponta para `http://localhost:8080/api`

### Erro ao instalar dependências Node
```bash
# Limpar cache
npm cache clean --force

# Reintentar
npm install
```

## 📝 Estrutura do Projeto

```
apiclinica/
├── src/                          # Backend Java
│   ├── main/java/inf012/apiclinica/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── model/
│   │   ├── repository/
│   │   └── dto/
│   └── resources/
│
├── frontend/                     # Frontend React
│   ├── src/
│   │   ├── components/
│   │   ├── config.js
│   │   ├── index.css
│   │   └── App.js
│   ├── public/
│   └── package.json
│
├── docker-compose.yml
├── Dockerfile                    # Backend
└── pom.xml

```

## 🔄 Workflow Completo

1. **Iniciar Backend**
   ```bash
   docker-compose up -d
   ```

2. **Iniciar Frontend** (novo terminal)
   ```bash
   cd frontend && npm start
   ```

3. **Acessar Aplicação**
   - Frontend: http://localhost:3000
   - API: http://localhost:8080/api
   - Banco de dados: localhost:5432

4. **Fazer Login**
   - Use admin/admin123 ou paciente1/pac1

## 📚 Endpoints da API

### Autenticação
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Renovar token

### Pacientes
- `GET /api/pacientes` - Listar pacientes
- `POST /api/pacientes` - Criar paciente
- `GET /api/pacientes/{id}` - Obter paciente
- `PUT /api/pacientes/{id}` - Atualizar paciente

### Médicos
- `GET /api/medicos` - Listar médicos
- `POST /api/medicos` - Criar médico
- `GET /api/medicos/requests` - Listar solicitações (Admin)
- `POST /api/medicos/requests/{id}/approve` - Aprovar médico

### Consultas
- `GET /api/consultas` - Listar consultas
- `POST /api/consultas` - Agendar consulta
- `PUT /api/consultas/{id}` - Atualizar consulta
- `DELETE /api/consultas/{id}` - Cancelar consulta

## 🎯 Próximas Etapas

- [ ] Adicionar validação de formulários mais robusta
- [ ] Implementar upload de documentos
- [ ] Adicionar sistema de notificações
- [ ] Implementar paginação em tabelas
- [ ] Adicionar filtros e buscas
- [ ] Melhorar tratamento de erros

---

**Desenvolvido com ❤️ para API Clínica**
