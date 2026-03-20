# 🏦 Bank Microservices

Sistema bancário construído com Spring Boot e arquitetura de microsserviços, com gerenciamento de contas, conversão de moedas, histórico de transações e autenticação via Keycloak.

---

## ⚠️ Atenção: a aplicação está em estágio de desenvolvimento e não representa a versão final

---

## 🧩 Serviços

### Gateway Service — `porta 8080`
Spring Cloud Gateway (WebFlux) que atua como ponto de entrada único para todas as requisições dos clientes. Valida tokens JWT, configura CORS e roteia o tráfego para os serviços downstream via Eureka.

### Discovery Service — `porta 8761`
Servidor Netflix Eureka para registro e descoberta de serviços. Todos os microsserviços se registram aqui e o utilizam para se localizar mutuamente.

### Account Service — `porta 8081`
Serviço bancário principal. Gerencia contas, saldos, depósitos, saques e transferências. Publica eventos de transação no Kafka e se comunica com os serviços de Conversão de Moeda e de Usuário via Feign clients.

**Endpoints:**

| Método | Caminho | Descrição |
|--------|---------|-----------|
| POST | `/api/v1/account/create` | Criar uma nova conta |
| PUT | `/api/v1/account/update` | Atualizar moeda da conta |
| GET | `/api/v1/account/find-all` | Listar todas as contas (somente ADMIN) |
| DELETE | `/api/v1/account/delete` | Deletar conta |
| GET | `/api/v1/account/balance` | Consultar saldo atual |
| POST | `/api/v1/account/deposit` | Realizar depósito |
| POST | `/api/v1/account/withdraw` | Realizar saque |
| POST | `/api/v1/account/transfer` | Transferir para outro usuário |
| GET | `/api/v1/account/find-by-user-id` | Buscar ID da conta pelo usuário |

### Transaction Service — `porta 8082`
Consome eventos Kafka publicados pelo Account Service e persiste os registros de transações. Suporta consulta do histórico completo de transações de um usuário, incluindo a direção (ENVIADO/RECEBIDO).

**Endpoints:**

| Método | Caminho | Descrição |
|--------|---------|-----------|
| GET | `/api/v1/transaction/me` | Listar todas as transações do usuário autenticado |

### Currency Converter Service — `porta 8084`
Realiza conversão de moedas utilizando APIs externas. Suporta dois provedores, configuráveis via properties: **FxRates API** (padrão) e **Invertexto API**.

**Endpoints:**

| Método | Caminho | Descrição |
|--------|---------|-----------|
| GET | `/api/v1/currency-converter/{from}/{to}?amount=` | Converter um valor entre duas moedas |

### User Service — `porta 8085`
Integra-se com a API Admin do Keycloak para buscar usuários por e-mail. Utilizado pelo Account Service durante transferências para resolver a conta do destinatário.

**Endpoints:**

| Método | Caminho | Descrição |
|--------|---------|-----------|
| GET | `/api/v1/user?email=` | Buscar usuário por e-mail |

---

## 🔐 Segurança

A autenticação é gerenciada pelo **Keycloak** (`bank-realm`). Todos os serviços são configurados como OAuth2 Resource Servers, validando tokens JWT.

- O Gateway valida os tokens antes de encaminhar as requisições.
- O Transaction Service repassa o JWT do usuário ao Account Service via interceptor Feign.
- Controle de acesso por papel: o endpoint `find-all` de contas exige a role `ADMIN` do realm.

---

## 📨 Mensageria

**Apache Kafka** é utilizado para comunicação assíncrona entre o Account Service (produtor) e o Transaction Service (consumidor).

| Tópico | Produtor | Consumidor |
|--------|----------|------------|
| `transaction-created` | Account Service | Transaction Service |

**Tipos de transação:** `DEPOSIT`, `WITHDRAW`, `TRANSFER`, `EXCHANGE`

---

## 🗄️ Bancos de Dados

| Serviço | Banco de Dados |
|---------|----------------|
| Account Service | `accounts_db` (MySQL) |
| Transaction Service | `transaction_db` (MySQL) |
| Keycloak | `keycloak_database` (PostgreSQL) |

---

## 🐳 Executando com Docker

Inicie todas as dependências de infraestrutura com o Docker Compose:

```bash
docker compose up -d
```

Isso sobe:
- **MySQL** na porta `3306`
- **Keycloak** na porta `8181`
- **PostgreSQL** (banco do Keycloak) na porta `5432`
- **Zookeeper** na porta `2181`
- **Kafka** na porta `9092`

> **Atenção:** Após o primeiro start, é necessário configurar o `bank-realm` no Keycloak e criar o client `user-ms-client` com o client secret correspondente.

---

## ⚙️ Variáveis de Ambiente

Defina as seguintes variáveis antes de executar o Currency Converter Service:

```bash
TOKEN_API_FXRATESAPI=seu_token_fxrates
TOKEN_API_INVERTEXTO=seu_token_invertexto
```

Para trocar o provedor de câmbio, altere no `application.properties`:

```properties
# Opções: fxRates (padrão) | invertexto
currency.provider=fxRates
```

O User Service requer o client secret do Keycloak:

```properties
app.config.keycloak.client-secret=seu_client_secret
```

---

## 🚀 Ordem de Inicialização

Inicie os serviços nesta ordem para evitar falhas de dependência:

1. Infraestrutura via Docker Compose
2. Discovery Service
3. Account Service, Transaction Service, Currency Converter Service, User Service
4. Gateway Service

---

## 📖 Documentação da API

O Swagger UI está disponível pelo Gateway em:

```
http://localhost:8080/swagger-ui.html
```

A documentação de todos os serviços é agregada e acessível via seletor de dropdown.

---

## 🧪 Testes

Testes unitários estão disponíveis para o Account Service utilizando JUnit 5 e Mockito. Execute com:

```bash
./mvnw test
```

A cobertura inclui criação de contas, atualização de moeda, consulta de saldo, paginação e exclusão — com cenários de sucesso e de erro.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21** / Spring Boot 3
- **Spring Cloud Gateway** (WebFlux)
- **Spring Cloud Netflix Eureka**
- **Spring Security** (OAuth2 Resource Server)
- **OpenFeign** (comunicação entre serviços)
- **Apache Kafka**
- **MySQL** / **PostgreSQL**
- **Keycloak 26**
- **Docker** / Docker Compose
- **SpringDoc OpenAPI** (Swagger UI)
- **JUnit 5** / Mockito
