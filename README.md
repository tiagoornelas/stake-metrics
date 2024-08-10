![Novo Projeto (1)](https://github.com/user-attachments/assets/412379bd-0fc1-4945-af09-623c7d75d46d)

### Application Properties (Backend Env)
application-properties:
- spring.application.name=back-end
- spring.profiles.active={{dev | prod}} // dev ou prod a depender do env

application-{{dev | prod}}.properties:
- app.frontend.base.url=http://localhost:3000
- app.backend.queue.base.url=http://host.docker.internal:8080
- app.gcp.project.id=stakemetrics
- app.gcp.location.id=us-central1
- spring.datasource.url=jdbc:mysql://localhost:3306/stake-metrics
- spring.datasource.username=root
- spring.jpa.hibernate.ddl-auto=update
- spring.datasource.password=UIBmIuZ_BvwA
- api.security.token.secret=UIBmIuZ_BvwA
- stripe.pricing.table=prctbl_1PWjTiEvrQ7zPy18hlKtUjPW
- stripe.dark.mode.pricing.table=prctbl_1PjX2nEvrQ7zPy180Gh6wYLz
- stripe.api.key=sk_test_51PVffeEvrQ7zPy18P0Y5XYNFChQjZxe7iIz5kXSvl4ni8OMNNk95KMb7EmR9WE17pH6Z6aOxPgrGFkPnThiDPcW700q4GU4Jnm
- stripe.public.key=pk_test_51PVffeEvrQ7zPy18W30s4uVxV8VtlxQgW9aASPu0vNt7BdNdsHnzaoIcIf28FEuHGP4dLkqOj4Lmb3SKAhIYUZpr00BxGWItEs
- mailersend.domain.email=MS_BCyJGD@trial-k68zxl28en5lj905.mlsender.net
- mailersend.api.key=mlsn.3b6c3b8576ffbd80bb9ff4b7fcccb5a1bcd5dfa3ed53f2be9cd623babd4bc859
- telegram.api.id=21770625
- telegram.api.hash=5d6c78524953731cd978db89c7eb6230
- telegram.bot.token=6875098813:AAEmMj7BWA8gfEjXPpSvflcWpYXoiEO5RSE
- logging.level.org.springframework.security=DEBUG

### .env (Frontend Env)
Variáveis:
- REACT_APP_BASE_URL=http://localhost:8080

## Demais instalações (Local Env)
- Para identificar o código que é sensível ao ambiente, procure pela anotação @EnvironmentSensitive
- Para ativar as alterações do ambiente local, insira spring.profiles.active=dev no application.properties

### Stripe
- Para testar a integração com Stripe para pagamentos, é preciso [instalar a CLI do Stripe](https://github.com/stripe/stripe-cli/releases/tag/v1.21.0) para redirecionar o webhook para o endpoint local.
- Na CLI, rode o seguinte comando: stripe login (para fazer login na sua conta Stripe)
- Após logado, rode o comando: stripe listen --forward-to localhost:8080/subscription/notify-events --skip-verify
- Limpe os clientes e assinaturas no Stripe para evitar problemas de sync

### Cloud-Task-Emulator
- Clone o repositório [cloud-task-emulator](https://github.com/aertje/cloud-tasks-emulator)
- No repositório, faça o build do docker com o comando docker build ./ -t tasks_emulator
- Rode o container com docker run -p 8123:8123 tasks_emulator -host 0.0.0.0 -port 8123 -queue projects/stakemetrics/locations/us-central1/queues/run-strategy-against-odds -queue projects/stakemetrics/locations/us-central1/queues/send-message -queue projects/stakemetrics/locations/us-central1/queues/save-match-result
- Verifique os nomes das filas padrões criadas para identificar se precisam ser criadas novas filas.
