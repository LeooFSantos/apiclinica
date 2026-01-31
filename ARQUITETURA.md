# 🏗️ Arquitetura Técnica - API Clínica

## 1. Diagrama Geral

```
┌────────────────────────────────────────────────────────────────┐
│                                                                │
│                    CAMADA DE APRESENTAÇÃO                     │
│                                                                │
│    ┌─────────────────────────────────────────────────────┐   │
│    │              FRONTEND (React 18)                    │   │
│    │         http://localhost:3000                       │   │
│    ├─────────────────────────────────────────────────────┤   │
│    │                                                     │   │
│    │  • Login Component       → POST /auth/login         │   │
│    │  • Register Paciente     → POST /pacientes          │   │
│    │  • Register Médico       → POST /medicos/requests   │   │
│    │  • Admin Dashboard       → GET /medicos/requests    │   │
│    │  • Médico Dashboard      → GET /consultas           │   │
│    │  • Paciente Dashboard    → GET /consultas           │   │
│    │  • Navbar (Logout)       → Remove token             │   │
│    │                                                     │   │
│    └─────────────────────────────────────────────────────┘   │
│                                                                │
└────────────────────────────────────────────────────────────────┘
                            ↕ HTTP + JSON
            ┌──────────────────────────────────────┐
            │      JWT Token Management            │
            │ • Bearer in Authorization header     │
            │ • localStorage (token + userType)    │
            │ • Auto-refresh pending               │
            └──────────────────────────────────────┘
                            ↕
┌────────────────────────────────────────────────────────────────┐
│                                                                │
│                  CAMADA DE APLICAÇÃO                          │
│                                                                │
│    ┌─────────────────────────────────────────────────────┐   │
│    │        BACKEND (Spring Boot 3.5.9)                 │   │
│    │       http://localhost:8080/api                    │   │
│    ├─────────────────────────────────────────────────────┤   │
│    │                                                     │   │
│    │  ┌─ CONTROLLERS (REST Endpoints) ─────────────┐   │   │
│    │  │  • AuthController                          │   │   │
│    │  │    - POST /auth/login                       │   │   │
│    │  │    - POST /auth/refresh                     │   │   │
│    │  │  • PacienteController                       │   │   │
│    │  │    - GET/POST/PUT/DELETE /pacientes        │   │   │
│    │  │  • MedicoController                         │   │   │
│    │  │    - GET/POST/PUT/DELETE /medicos          │   │   │
│    │  │  • MedicoRegistrationController            │   │   │
│    │  │    - POST /medicos/requests                │   │   │
│    │  │    - GET /medicos/requests                 │   │   │
│    │  │    - POST /medicos/requests/{id}/approve   │   │   │
│    │  │  • ConsultaController                       │   │   │
│    │  │    - GET/POST/PUT/DELETE /consultas        │   │   │
│    │  └─────────────────────────────────────────────┘   │   │
│    │                       ↓                            │   │
│    │  ┌─ SERVICES (Business Logic) ──────────────────┐ │   │
│    │  │  • AuthService                              │ │   │
│    │  │    - Token generation/validation            │ │   │
│    │  │  • PacienteService                          │ │   │
│    │  │    - CRUD operations                        │ │   │
│    │  │    - Password hashing                       │ │   │
│    │  │  • MedicoService                            │ │   │
│    │  │    - CRUD operations                        │ │   │
│    │  │    - Request management                     │ │   │
│    │  │  • ConsultaService                          │ │   │
│    │  │    - Schedule/cancel appointments           │ │   │
│    │  └─────────────────────────────────────────────┘ │   │
│    │                       ↓                            │   │
│    │  ┌─ REPOSITORIES (Data Access) ─────────────────┐ │   │
│    │  │  • PacienteRepository (JPA)                 │ │   │
│    │  │  • MedicoRepository (JPA)                   │ │   │
│    │  │  • MedicoRequestRepository (JPA)            │ │   │
│    │  │  • ConsultaRepository (JPA)                 │ │   │
│    │  └─────────────────────────────────────────────┘ │   │
│    │                                                     │   │
│    └─────────────────────────────────────────────────────┘   │
│                                                                │
│    ┌─ SECURITY LAYER ──────────────────────────────────────┐ │
│    │  • SecurityConfig (Spring Security 6.5.x)             │ │
│    │    - CORS configuration                              │ │
│    │    - Role-based endpoint protection                  │ │
│    │  • JwtTokenProvider                                  │ │
│    │    - Token generation                               │ │
│    │    - Token validation                               │ │
│    │    - Claim extraction                               │ │
│    │  • JwtTokenFilter                                   │ │
│    │    - Request interception                           │ │
│    │    - Token validation per request                   │ │
│    │  • PasswordEncoderConfig                            │ │
│    │    - BCrypt password hashing                        │ │
│    └─────────────────────────────────────────────────────┘ │
│                                                                │
│    ┌─ DTO LAYER ───────────────────────────────────────────┐ │
│    │  Camada de transferência de dados (não expõe JPA)    │ │
│    │  • LoginDTO, JwtAuthResponseDTO                      │ │
│    │  • PacienteCreateDTO, PacienteListDTO                │ │
│    │  • MedicoCreateDTO, MedicoListDTO                    │ │
│    │  • ConsultaCancelamentoDTO                           │ │
│    └─────────────────────────────────────────────────────┘ │
│                                                                │
└────────────────────────────────────────────────────────────────┘
                            ↕ JDBC/JPA
┌────────────────────────────────────────────────────────────────┐
│                                                                │
│                  CAMADA DE DADOS                              │
│                                                                │
│    ┌─────────────────────────────────────────────────────┐   │
│    │      DATABASE (PostgreSQL 16)                       │   │
│    │        localhost:5432                              │   │
│    ├─────────────────────────────────────────────────────┤   │
│    │                                                     │   │
│    │  TABELAS:                                          │   │
│    │  • paciente                                        │   │
│    │    - id, nome, email, cpf, telefone               │   │
│    │    - nome_usuario (unique), senha (bcrypt)        │   │
│    │    - endereco (embedded)                          │   │
│    │                                                   │   │
│    │  • medico                                          │   │
│    │    - id, nome, email, crm, especialidade         │   │
│    │    - nome_usuario (unique), senha (bcrypt)       │   │
│    │    - endereco (embedded)                         │   │
│    │                                                   │   │
│    │  • medico_request                                 │   │
│    │    - id, nome, crm, especialidade                │   │
│    │    - nome_usuario, senha                         │   │
│    │    - approved (boolean)                          │   │
│    │    - endereco (embedded)                         │   │
│    │                                                   │   │
│    │  • consulta                                        │   │
│    │    - id, data_hora, motivo, status               │   │
│    │    - medico_id (FK)                              │   │
│    │    - paciente_id (FK)                            │   │
│    │                                                   │   │
│    │  INDICES:                                         │   │
│    │  • paciente(nome_usuario)                        │   │
│    │  • medico(nome_usuario)                          │   │
│    │  • consulta(medico_id, paciente_id)             │   │
│    │                                                     │   │
│    └─────────────────────────────────────────────────────┘   │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---

## 2. Fluxo de Autenticação

```
┌─────────────┐
│   Browser   │
│ localStorage│
│   token     │
└─────────────┘
       ↓
  [Login Page]
       ↓
  Input: nomeUsuario + senha
       ↓
POST /api/auth/login {nomeUsuario, senha}
       ↓
  [Backend]
  1. Find user in InMemoryUserDetailsManager
  2. Compare password (BCrypt)
  3. Load user authorities (roles)
  4. Generate JWT token with roles as claims
  5. Return { token, nomeUsuario, tipo }
       ↓
  [Frontend]
  1. Store token in localStorage
  2. Store nomeUsuario in localStorage
  3. Detect tipo (admin vs USER)
  4. Store userType in localStorage
  5. Redirect to /dashboard
       ↓
  [Protected Routes]
  1. Check localStorage for token
  2. If exists: add to Authorization header
  3. If missing: redirect to /
       ↓
  [Each Request]
Authorization: Bearer {token}
       ↓
  [JwtTokenFilter]
  1. Extract token from header
  2. Validate JWT signature
  3. Check expiration
  4. Extract authorities (roles)
  5. Set SecurityContext
  6. Allow/deny based on role
```

---

## 3. Fluxo de Registro (Paciente)

```
[Login Page]
    ↓
"Registrar como Paciente"
    ↓
[RegisterPaciente Form]
    ↓
Fill form:
• nome, email, cpf, telefone
• nomeUsuario (unique), senha
• endereco details
    ↓
Submit POST /api/pacientes
    ↓
[Backend]
1. Validate DTO
2. Hash password (BCrypt)
3. Create Paciente entity
4. Save to database
5. Return JwtAuthResponseDTO
    ↓
[Frontend]
Success → Redirect to /
    ↓
Can now login with nomeUsuario + senha
```

---

## 4. Fluxo de Aprovação (Médico)

```
[Login Page]
    ↓
"Registrar como Médico"
    ↓
[RegisterMedico Form]
    ↓
Fill form:
• nome, email, crm
• especialidade (dropdown)
• nomeUsuario, senha
• endereco
    ↓
Submit POST /api/medicos/requests
    ↓
[Backend]
1. Create MedicoRequest entity
2. Set approved = false
3. Save to database
4. Return success message
    ↓
[Frontend]
Show success screen
Request awaiting admin approval
    ↓
    
┌─ Admin Flow ──────────────────┐
│                                │
│ [Admin Dashboard]              │
│ GET /api/medicos/requests      │
│ (approved = false)             │
│      ↓                         │
│ Show requests in table         │
│      ↓                         │
│ Click [Aprovar]                │
│      ↓                         │
│ POST /api/medicos/requests/{id}/approve
│      ↓                         │
│ [Backend]                      │
│ 1. Find MedicoRequest by id    │
│ 2. Set approved = true         │
│ 3. Create Medico from request  │
│ 4. Add to InMemoryUserDetails  │
│ 5. Save both                   │
│      ↓                         │
│ Request removed from table     │
│ Médico now can login           │
│                                │
└────────────────────────────────┘
```

---

## 5. Estrutura de Pastas Frontend

```
frontend/
├── src/
│   ├── components/
│   │   ├── AdminDashboard/
│   │   │   ├── AdminDashboard.js       (loads pending medicos)
│   │   │   └── AdminDashboard.css
│   │   │
│   │   ├── Dashboard/
│   │   │   ├── Dashboard.js            (router by role)
│   │   │   └── Dashboard.css
│   │   │
│   │   ├── Login/
│   │   │   ├── Login.js                (✅ FIXED)
│   │   │   └── Login.css               (✨ NEW STYLES)
│   │   │
│   │   ├── MedicoDashboard/
│   │   │   ├── MedicoDashboard.js      (tabs interface)
│   │   │   └── MedicoDashboard.css
│   │   │
│   │   ├── Navbar/
│   │   │   ├── Navbar.js               (✅ FIXED)
│   │   │   └── Navbar.css
│   │   │
│   │   ├── PacienteDashboard/
│   │   │   ├── PacienteDashboard.js
│   │   │   └── PacienteDashboard.css
│   │   │
│   │   ├── RegisterMedico/
│   │   │   ├── RegisterMedico.js
│   │   │   └── RegisterMedico.css
│   │   │
│   │   └── RegisterPaciente/
│   │       ├── RegisterPaciente.js
│   │       └── Register.css
│   │
│   ├── config.js                      (API + auth utils)
│   ├── index.css                      (design system)
│   ├── index.js
│   ├── App.js                         (routing)
│   └── App.test.js
│
├── public/
│   ├── index.html
│   └── favicon.ico
│
├── package.json                       (dependencies)
├── .env.example                       (env vars)
├── Dockerfile                         (docker build)
├── .gitignore
└── README.md
```

---

## 6. Estrutura de Pastas Backend

```
src/
├── main/
│   ├── java/inf012/apiclinica/
│   │   ├── ApiclinicaApplication.java (entry point)
│   │   │
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── PacienteController.java
│   │   │   ├── MedicoController.java
│   │   │   ├── MedicoRegistrationController.java
│   │   │   └── ConsultaController.java
│   │   │
│   │   ├── service/
│   │   │   ├── PacienteService.java
│   │   │   ├── MedicoService.java
│   │   │   └── ConsultaService.java
│   │   │
│   │   ├── model/
│   │   │   ├── Paciente.java
│   │   │   ├── Medico.java
│   │   │   ├── MedicoRequest.java
│   │   │   ├── Consulta.java
│   │   │   ├── Endereco.java
│   │   │   ├── Especialidade.java
│   │   │   └── MotivoCancelamento.java
│   │   │
│   │   ├── repository/
│   │   │   ├── PacienteRepository.java
│   │   │   ├── MedicoRepository.java
│   │   │   ├── MedicoRequestRepository.java
│   │   │   └── ConsultaRepository.java
│   │   │
│   │   ├── dto/
│   │   │   ├── LoginDTO.java
│   │   │   ├── JwtAuthResponseDTO.java
│   │   │   ├── PacienteCreateDTO.java
│   │   │   ├── PacienteListDTO.java
│   │   │   ├── PacienteUpdateDTO.java
│   │   │   ├── MedicoCreateDTO.java
│   │   │   ├── MedicoListDTO.java
│   │   │   ├── MedicoUpdateDTO.java
│   │   │   └── ConsultaCancelamentoDTO.java
│   │   │
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── JwtTokenFilter.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── UserDetailsConfig.java
│   │   │   └── PasswordEncoderConfig.java
│   │   │
│   │   └── exception/
│   │       └── ResourceNotFoundException.java
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/.../ApiclinicaApplicationTests.java
```

---

## 7. Ciclo de Vida de uma Request Autenticada

```
┌─ FRONTEND ────────────────────────────────┐
│                                           │
│ GET /api/medicos/requests (Admin)         │
│ + Header: Authorization: Bearer {token}   │
│                                           │
└─ HTTP POST ───────────────────────────────┘
                    ↓
┌─ NETWORK ─────────────────────────────────┐
│ JSON over HTTP/HTTPS                     │
│ CORS headers validated                   │
└────────────────────────────────────────────┘
                    ↓
┌─ BACKEND: Spring Servlet Filter Chain ──┐
│                                          │
│ 1. JwtTokenFilter                        │
│    a. Extract token from Authorization   │
│    b. Call JwtTokenProvider.validate()   │
│    c. Extract username + authorities     │
│    d. Set SecurityContext                │
│    e. Pass to next filter                │
│                                          │
│ 2. SecurityConfig Authorization          │
│    a. Check if endpoint requires auth    │
│    b. Check if user has required roles   │
│    c. For admin endpoint: verify ADMIN   │
│    d. Continue or deny                   │
│                                          │
└────────────────────────────────────────────┘
                    ↓
┌─ CONTROLLER ───────────────────────────────┐
│                                            │
│ @GetMapping("/api/medicos/requests")       │
│ @PreAuthorize("hasRole('ADMIN')")          │
│ public List<MedicoRequest> listar()        │
│ {                                          │
│     // SecurityContext has current user   │
│     return repository.findAll();           │
│ }                                          │
│                                            │
└────────────────────────────────────────────┘
                    ↓
┌─ SERVICE LAYER ────────────────────────────┐
│ Business logic (if any)                   │
│ Typically delegates to repository         │
└────────────────────────────────────────────┘
                    ↓
┌─ REPOSITORY ────────────────────────────────┐
│                                             │
│ List<MedicoRequest> findAll()               │
│ ↓ Query database with JPA                   │
│ SELECT * FROM medico_request                │
│ ↓ Return List<MedicoRequest>                │
│                                             │
└─────────────────────────────────────────────┘
                    ↓
┌─ DATABASE ──────────────────────────────────┐
│ PostgreSQL query execution                  │
│ Return result set                           │
└─────────────────────────────────────────────┘
                    ↓
┌─ RESPONSE ──────────────────────────────────┐
│ List<MedicoRequest> → JSON serialization   │
│ HTTP 200 OK + JSON body                    │
│ CORS headers added                         │
└─────────────────────────────────────────────┘
                    ↓
┌─ FRONTEND ──────────────────────────────────┐
│ Receive JSON response                      │
│ Parse JSON                                 │
│ Update state                               │
│ Re-render UI                               │
│ Display medicos in table                   │
└─────────────────────────────────────────────┘
```

---

## 8. Matriz de Autorização

| Endpoint | Admin | Médico | Paciente | Public |
|----------|-------|--------|----------|--------|
| POST /auth/login | ✓ | ✓ | ✓ | ✓ |
| POST /pacientes | ✓ | ✗ | ✗ | ✓ |
| GET /pacientes | ✓ | ✓ | ✗ | ✗ |
| POST /medicos/requests | ✓ | ✗ | ✗ | ✓ |
| GET /medicos/requests | ✓ | ✗ | ✗ | ✗ |
| POST /medicos/{id}/approve | ✓ | ✗ | ✗ | ✗ |
| GET /medicos | ✓ | ✓ | ✓ | ✓ |
| POST /medicos | ✓ | ✗ | ✗ | ✗ |
| GET /consultas | ✓ | ✓ | ✗ | ✗ |
| POST /consultas | ✓ | ✓ | ✓ | ✗ |
| DELETE /consultas/{id} | ✓ | ✓ | (own) | ✗ |

---

## 9. Fluxo de Deploy (Docker)

```
docker-compose.yml
├── Service: postgres
│   ├── Image: postgres:16
│   ├── Ports: 5432:5432
│   ├── Volumes: postgres_data:/var/lib/postgresql/data
│   └── Environment: POSTGRES_PASSWORD, POSTGRES_DB
│
├── Service: api (Backend)
│   ├── Build: ./Dockerfile
│   ├── Ports: 8080:8080
│   ├── Depends on: postgres
│   ├── Environment: SPRING_DATASOURCE_URL, SPRING_PROFILES_ACTIVE
│   └── Volumes: app logs
│
└── Service: frontend (opcional)
    ├── Build: ./frontend/Dockerfile
    ├── Ports: 3000:3000
    └── Environment: REACT_APP_API_URL
```

---

## 10. Verificação de Status

```bash
# Verificar se tudo está rodando
docker-compose ps

# Output esperado:
# NAME            STATUS
# apiclinica-postgres-1    Up
# apiclinica-api-1         Up
# apiclinica-frontend-1    Up (se adicionado)

# Testar Backend
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"nomeUsuario":"admin","senha":"admin123"}'

# Esperado: { "token": "...", "nomeUsuario": "admin", "tipo": "ADMIN" }

# Testar Frontend
curl http://localhost:3000
# Esperado: Página HTML do React

# Testar BD
psql -h localhost -U postgres -d apiclinica_db
```

---

**Documentação de Arquitetura Completa** ✅

Para mais detalhes, veja:
- README_FINAL.md (Overview)
- SETUP.md (Setup instructions)
- TESTES.md (Test procedures)
- FRONTEND_COMPLETE.md (Frontend docs)
