# 🧪 Guia de Testes - API Clínica

## Início Rápido (5 minutos)

### Terminal 1: Backend
```bash
# Na raiz do projeto
docker-compose up -d

# Ou, se preferir local:
mvn spring-boot:run
```

### Terminal 2: Frontend
```bash
cd frontend
npm install  # (primeira vez apenas)
npm start
```

### Abrir Navegador
```
http://localhost:3000
```

---

## 🔐 Teste 1: Login como Admin

### Etapas
1. Acesse http://localhost:3000
2. Veja a página de login com dois novos botões
3. Digite credenciais:
   - Usuário: `admin`
   - Senha: `admin123`
4. Clique em "Entrar"

### Resultados Esperados
✅ Token salvo no localStorage  
✅ Redirecionamento para `/dashboard`  
✅ Painel Admin carregado com solicitações de médicos  
✅ Navbar mostra "admin" como usuário logado  
✅ Botão "Sair" funciona  

---

## 👤 Teste 2: Login como Paciente

### Etapas
1. Retorne à página de login
2. Digite credenciais:
   - Usuário: `paciente1`
   - Senha: `pac1`
3. Clique em "Entrar"

### Resultados Esperados
✅ Dashboard de Paciente carregado  
✅ Mostra consultas do paciente  
✅ Cards com informações de saúde  
✅ Tipo de usuário correto em localStorage  

---

## 📝 Teste 3: Registrar Novo Paciente

### Etapas
1. Na página de login, clique em "Registrar como Paciente"
2. Preencha o formulário:
   - **Nome**: João Silva
   - **Email**: joao@email.com
   - **Telefone**: (11) 99999-8888
   - **CPF**: 123.456.789-00
   - **Nome de Usuário**: joao_silva
   - **Senha**: senha123
   - **Endereço**: Rua Teste, 123
   - **Bairro**: Centro
   - **Cidade**: São Paulo
   - **UF**: SP
   - **CEP**: 01234-567

3. Clique em "Registrar"

### Resultados Esperados
✅ Formulário validado  
✅ Requisição POST enviada para `/api/pacientes`  
✅ Redirecionamento para login (se sucesso)  
✅ Novo paciente pode fazer login  

**Teste de Login**: Agora faça login com `joao_silva` / `senha123`

---

## 👨‍⚕️ Teste 4: Registrar Novo Médico

### Etapas
1. Na página de login, clique em "Registrar como Médico"
2. Preencha o formulário:
   - **Nome**: Dr. Carlos Santos
   - **Email**: carlos@email.com
   - **Telefone**: (11) 88888-7777
   - **CRM**: 123456/SP
   - **Especialidade**: Cardiologia
   - **Nome de Usuário**: dr_carlos
   - **Senha**: senha456
   - **Endereço**: Av. Paulista, 500
   - **Cidade**: São Paulo
   - **UF**: SP
   - **CEP**: 01311-100

3. Clique em "Registrar"

### Resultados Esperados
✅ Requisição enviada para `/api/medicos/requests`  
✅ Tela de sucesso exibida  
✅ Solicitação aguarda aprovação  
✅ NOT pode fazer login ainda  

---

## ✅ Teste 5: Admin Aprova Médico

### Etapas
1. Faça login como admin (`admin` / `admin123`)
2. Vá para Painel de Administrador
3. Veja a solicitação do novo médico na tabela
4. Clique em "Aprovar"

### Resultados Esperados
✅ Solicitação removida da tabela  
✅ Médico now pode fazer login  
✅ Contador de solicitações pendentes reduzido  

**Teste de Login do Médico**: Agora faça login com `dr_carlos` / `senha456`

---

## 🔄 Teste 6: Logout Funciona

### Etapas
1. Estando em qualquer página autenticada
2. Procure o botão "Sair" (logout) na navbar
3. Clique nele

### Resultados Esperados
✅ Token removido do localStorage  
✅ Redirecionamento para página de login  
✅ Não consegue mais acessar `/dashboard`  
✅ Pode fazer novo login  

---

## 🧭 Teste 7: Navegação de Registro

### Etapas
1. Na página de login, note os dois novos botões
2. Clique em "Registrar como Paciente"
3. Volte (browser back ou link)
4. Clique em "Registrar como Médico"

### Resultados Esperados
✅ Links navegam corretamente  
✅ Formulários carregam sem erros  
✅ CSS responsivo em mobile  

---

## 📱 Teste 8: Responsividade

### Etapas
1. Abra DevTools (F12)
2. Clique em "Toggle Device Toolbar" (Ctrl+Shift+M)
3. Teste em:
   - iPhone 12 (390x844)
   - iPad (768x1024)
   - Desktop (1920x1080)

### Resultados Esperados
✅ Layout adapta-se ao tamanho  
✅ Botões são clickáveis em mobile  
✅ Texto legível em todos os tamanhos  
✅ Sem horizontal scrolling desnecessário  

---

## 🐛 Teste 9: Tratamento de Erros

### Cenário 1: Credenciais Inválidas
1. Digite usuário que não existe
2. Clique "Entrar"

**Esperado**: Mensagem de erro "Credenciais inválidas"

### Cenário 2: API Offline
1. Pare o backend: `docker-compose down`
2. Tente fazer login

**Esperado**: Mensagem de erro "Erro ao fazer login"

### Cenário 3: Sem Token
1. No DevTools (Console), execute:
```javascript
localStorage.removeItem('authToken');
```
2. Tente acessar `/dashboard`

**Esperado**: Redirecionamento automático para `/`

---

## 🎯 Teste 10: Validação de Importações

### No Console do Browser
```javascript
// Deve retornar os dados do usuário
console.log(JSON.parse(localStorage.getItem('authUser')));

// Deve retornar o token
console.log(localStorage.getItem('authToken'));

// Deve retornar o tipo de usuário
console.log(localStorage.getItem('userType'));
```

**Esperado**: Todos retornam valores válidos após login

---

## 📋 Checklist de Testes

- [ ] Login com admin funciona
- [ ] Login com paciente funciona
- [ ] Novo paciente pode registrar
- [ ] Novo médico pode registrar
- [ ] Admin pode aprovar médico
- [ ] Logout funciona
- [ ] Navegação de registro funciona
- [ ] Mobile responsivo
- [ ] Erros tratados corretamente
- [ ] Tokens persistem em refresh de página
- [ ] Logout limpa tokens
- [ ] Redireciona para login se sem token

---

## 🚨 Problemas Comuns

### "Cannot connect to API"
```bash
# Verifique se backend está rodando
docker-compose ps
# ou
curl http://localhost:8080/api/auth/login
```

### "Module not found: ../config"
✅ **CORRIGIDO**: Caminhos de importação atualizados para `../../config`

### "Port 3000 already in use"
```bash
# Use porta diferente
PORT=3001 npm start
```

### "Blank page after login"
- Verifique DevTools → Console para erros
- Verifique DevTools → Network para falhas HTTP
- Verifique se token está em localStorage

---

## 📊 Métricas de Sucesso

Todos os 10 testes devem passar para considerar o frontend pronto para produção.

**Status**: ✅ Pronto para Testes

---

**Boa sorte nos testes! 🚀**
