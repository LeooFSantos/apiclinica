# 🏥 API Clínica - Documentação

Uma API REST completa para gerenciamento de clínicas, desenvolvida com Spring Boot 3.5.9 e autenticação JWT.

## 📋 Índice
- [Visão Geral](#visão-geral)
- [Tecnologias](#-tecnologias)
- [Configuração Inicial](#configuração-inicial)
- [Executando o Projeto](#executando-o-projeto)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Autenticação JWT](#autenticação-jwt)
- [Endpoints da API](#endpoints-da-api)
- [Testando a API](#testando-a-api)
- [Banco de Dados](#banco-de-dados)

---

## Visão Geral

A **API Clínica** é um serviço backend que permite gerenciar:
- 👨‍⚕️ **Médicos** - Cadastro e gerenciamento de profissionais
- 👥 **Pacientes** - Dados dos pacientes
- 📅 **Consultas** - Agendamento e cancelamento de consultas
- 🔐 **Autenticação JWT** - Segurança com tokens JWT

---

## 🛠 Tecnologias

| Tecnologia | Versão | Propósito |
|------------|--------|----------|
| **Java** | 21 | Linguagem principal |
| **Spring Boot** | 3.5.9 | Framework web |
| **Spring Security** | 6.5.7 | Autenticação e autorização |
| **Spring Data JPA** | 2025.0.7 | ORM e persistência |
| **PostgreSQL** | 16 | Banco de dados |
| **JJWT** | 0.12.3 | Geração e validação de JWT |
| **SpringDoc OpenAPI** | 2.6.0 | Documentação Swagger/OpenAPI |
| **Docker** | Latest | Containerização |
| **Maven** | 3.9.6 | Gerenciador de dependências |

---

## Configuração Inicial

### Pré-requisitos
- **Docker** e **Docker Compose** instalados
- **Java 21** (para desenvolvimento local)
- **Maven 3.9.6** (para compilação)

### Clonar o Repositório
```bash
git clone <seu-repo>
cd apiclinica
```

### Variáveis de Ambiente
O arquivo `application.properties` já está configurado com valores padrão:
```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/clinica
spring.datasource.username=postgres
spring.datasource.password=postgres
jwt.secret=minhaCh6veSuperSecretaParaAssinaduraDoTokenJWTDaClinicaComMaisDe512Bits!@#$%^&*()_+=-`~[]{}
jwt.expiration=86400000  # 24 horas em ms
```

---

## Executando o Projeto

### 🐳 Com Docker Compose (Recomendado)

```bash
# Parar containers anteriores
docker-compose down

# Remover imagem antiga (para rebuild)
docker rmi apiclinica-api

# Construir e iniciar
docker-compose up -d
```

A API estará disponível em: **http://localhost:8080**

### 🖥️ Localmente (Sem Docker)

```bash
# Compilar
mvn clean install

# Executar
mvn spring-boot:run
```

**Nota:** Você precisará de um PostgreSQL rodando localmente na porta 5432.

---

## Estrutura do Projeto

```
apiclinica/
├── src/main/java/inf012/apiclinica/
│   ├── ApiclinicaApplication.java         # Classe principal
│   ├── config/                            # Configurações
│   │   ├── SecurityConfig.java            # Spring Security + JWT
│   │   ├── PasswordEncoderConfig.java     # BCrypt encoder
│   │   └── UserDetailsConfig.java         # Usuários em memória
│   ├── controller/                        # Controllers REST
│   │   ├── AuthController.java            # Autenticação
│   │   ├── PacienteController.java        # Gestão de pacientes
│   │   ├── MedicoController.java          # Gestão de médicos
│   │   └── ConsultaController.java        # Gestão de consultas
│   ├── service/                           # Lógica de negócio
│   ├── repository/                        # Data Access
│   ├── model/                             # Entidades JPA
│   ├── dto/                               # Data Transfer Objects
│   │   ├── LoginDTO.java                  # Login
│   │   ├── JwtAuthResponseDTO.java        # Resposta JWT
│   │   ├── PacienteCreateDTO.java         # Criar paciente
│   │   └── ...
│   └── security/                          # JWT
│       ├── JwtTokenProvider.java          # Gerador/Validador JWT
│       └── JwtTokenFilter.java            # Filtro JWT
├── src/main/resources/
│   └── application.properties             # Configurações
├── docker-compose.yml                     # Orquestração de containers
├── Dockerfile                             # Build da aplicação
└── pom.xml                                # Dependências Maven
```

---

## Autenticação JWT

### Como Funciona

1. **Login**: Envie credenciais para `/api/auth/login`
2. **Receba Token**: API retorna um JWT token
3. **Autorize Requisições**: Inclua o token no header `Authorization: Bearer {token}`
4. **Validação**: Filtro JWT valida o token automaticamente

### Usuários Padrão

| Usuário | Senha | Role |
|---------|-------|------|
| `admin` | `admin123` | ADMIN |
| `user` | `user123` | USER |

### Exemplo de Token JWT

```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc2OTM1ODI3NywiZXhwIjoxNzY5NDQ0Njc3fQ.2I_43Mnk1mE6cMYURxCrQtkLFqC4phWeYerBFsPoVjobHID8aUeT6HRK0uU6g-qdfF0mTZYdNmgETQodYlMDpA
```

---

## Endpoints da API

### 🔐 Autenticação

#### POST `/api/auth/login`
Faz login e retorna JWT token

**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "type": "Bearer"
}
```

#### POST `/api/auth/refresh`
Renova o token JWT

**Query Parameters:**
- `username` (obrigatório): Nome do usuário

---

### 👥 Pacientes

#### GET `/api/pacientes`
Lista todos os pacientes (autenticado)

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "nome": "João Silva",
      "cpf": "123.456.789-00",
      "email": "joao@example.com",
      "endereco": { ... }
    }
  ],
  "totalElements": 10,
  "totalPages": 1,
  "size": 10
}
```

#### POST `/api/pacientes`
Cria um novo paciente

**Request:**
```json
{
  "nome": "Maria Santos",
  "cpf": "987.654.321-00",
  "email": "maria@example.com",
  "endereco": {
    "logradouro": "Rua A",
    "numero": 123,
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01310-100"
  }
}
```

#### GET `/api/pacientes/{id}`
Obtém um paciente específico

#### PUT `/api/pacientes/{id}`
Atualiza um paciente

#### DELETE `/api/pacientes/{id}`
Deleta um paciente

---

### 👨‍⚕️ Médicos

#### GET `/api/medicos`
Lista todos os médicos (autenticado)

#### POST `/api/medicos`
Cria um novo médico

**Request:**
```json
{
  "nome": "Dr. Carlos",
  "crm": "123456",
  "especialidade": "CARDIOLOGIA",
  "endereco": { ... }
}
```

#### GET `/api/medicos/{id}`
Obtém um médico específico

#### PUT `/api/medicos/{id}`
Atualiza um médico

#### DELETE `/api/medicos/{id}`
Deleta um médico

---

### 📅 Consultas

#### GET `/api/consultas`
Lista todas as consultas (autenticado)

#### POST `/api/consultas`
Cria uma nova consulta

**Request:**
```json
{
  "idPaciente": 1,
  "idMedico": 1,
  "data": "2026-02-15T14:30:00"
}
```

#### GET `/api/consultas/{id}`
Obtém uma consulta específica

#### DELETE `/api/consultas/{id}`
Cancela uma consulta

**Request:**
```json
{
  "motivo": "PACIENTE_DESISTIU"
}
```

---

## Testando a API

### 🌐 Opção 1: Swagger UI (Recomendado)
Acesse no navegador:
```
http://localhost:8080/swagger-ui.html
```

Benefícios:
- ✅ Documentação interativa
- ✅ Testar endpoints diretamente
- ✅ Visualizar models
- ✅ Autenticação integrada

### 📄 Opção 2: Testador JWT HTML
Abra o arquivo `jwt-tester.html` no navegador:
```
C:\Users\Leo\Documents\apiclinica\jwt-tester.html
```

Funcionalidades:
- Interface bonita e intuitiva
- Login automático
- Testa endpoints protegidos
- Copia token facilmente

### 📮 Opção 3: Postman
1. Baixe em [postman.com](https://www.postman.com/downloads/)
2. Crie uma requisição POST: `http://localhost:8080/api/auth/login`
3. Body (raw JSON):
```json
{
  "username": "admin",
  "password": "admin123"
}
```
4. Copie o token da resposta
5. Em outros endpoints, adicione header:
   - Key: `Authorization`
   - Value: `Bearer {token}`

### 🖥️ Opção 4: cURL (Terminal)
```bash
# 1. Fazer login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Usar o token retornado
curl -X GET http://localhost:8080/api/pacientes \
  -H "Authorization: Bearer {seu_token_aqui}"
```

---

## Banco de Dados

### Conexão
- **Host**: postgres_clinica (em Docker) / localhost
- **Port**: 5432
- **Database**: clinica
- **User**: postgres
- **Password**: postgres

### Schema
As tabelas são criadas automaticamente via Hibernate (`ddl-auto=update`):

- **pacientes** - Dados dos pacientes
- **medicos** - Dados dos médicos
- **especialidades** - Especialidades médicas
- **consultas** - Agendamentos
- **motivos_cancelamento** - Motivos de cancelamento
- **enderecos** - Endereços (compartilhado)

---

## Troubleshooting

### ❌ Erro: "Weak Key Exception"
**Solução:** A chave JWT é muito fraca. Verifique que `jwt.secret` tem mais de 512 bits.

### ❌ Erro: "Forbidden (403)"
**Solução:** Token JWT inválido ou expirado. Faça login novamente.

### ❌ Erro: "Connection refused"
**Solução:** PostgreSQL não está rodando. Verifique:
```bash
docker-compose ps
```

### ❌ Erro: "Circular reference"
**Solução:** Reinicie os containers:
```bash
docker-compose restart
```

---

## 📚 Referências

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/3.5.9/reference/)
- [Spring Security](https://docs.spring.io/spring-boot/3.5.9/reference/web/spring-security.html)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Documentation](https://docs.docker.com/)

---

## 📝 Notas Importantes

- ⚠️ **Desenvolvimento**: Os usuários estão em memória. Para produção, integre com um banco de dados.
- ⚠️ **Segurança**: Altere o `jwt.secret` em produção para uma chave mais segura.
- ⚠️ **CORS**: Atualmente sem restrições CORS. Configure em produção.

---

Desenvolvido com ❤️ usando Spring Boot 3.5.9
