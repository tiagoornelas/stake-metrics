![Novo Projeto (2)](https://github.com/user-attachments/assets/9fe96670-10e2-4716-89fd-71dd420f4ac1)

### Deploy Infra
- Front-end: Google Cloud Run 🏃
- Back-end: Google Cloud Run 🏃
- Database: AWS RDS 🗂️
- Queues: Google Cloud Tasks ➡️
- Scheduler: Google Cloud Scheduler ⏱️

### Application Properties (Backend Env)
application-properties:
- spring.application.name=back-end
- spring.profiles.active={{dev | prod}} // dev ou prod a depender do env

application-{{dev | prod}}.properties:
- app.frontend.base.url=http://localhost:3000
- app.backend.queue.base.url=http://172.17.0.1:8080 (Linux) http://host.docker.internal:8080 (Windows or Mac)
- app.gcp.project.id=stakemetrics
- app.gcp.location.id=us-central1
- spring.datasource.url=jdbc:mysql://localhost:3306/stake-metrics
- spring.datasource.username=root
- spring.jpa.hibernate.ddl-auto=update
- spring.datasource.password=dev_secret_token_placeholder
- api.security.token.secret=dev_secret_token_placeholder
- stripe.pricing.table=prctbl_placeholder_1
- stripe.dark.mode.pricing.table=prctbl_placeholder_2
- stripe.api.key=sk_test_placeholder
- stripe.public.key=pk_test_placeholder
- mailersend.domain.email=noreply@stakemetrics.net
- mailersend.api.key=mlsn.placeholder
- telegram.api.id=21770625
- telegram.api.hash=dummy_telegram_api_hash
- telegram.bot.token=dummy_telegram_bot_token
- bets.api.token=dummy_bets_api_token
- autobettor.webhook.token=dummy_autobettor_token
- logging.level.org.springframework.security=DEBUG

### .env (Frontend Env)
Variáveis:
- REACT_APP_BASE_URL=http://localhost:8080
- REACT_APP_DISABLE_NEW_USERS=true (para desativar novos usuários)

## Demais instalações (Local Env)
- Para identificar o código que é sensível ao ambiente, procure pela anotação @EnvironmentSensitive
- Para ativar as alterações do ambiente local, insira spring.profiles.active=dev no application.properties

### Cloud-Task-Emulator
- Clone o repositório [cloud-task-emulator](https://github.com/aertje/cloud-tasks-emulator)
- No repositório, faça o build do docker com o comando docker build ./ -t tasks_emulator
- Rode o container com docker run -p 8123:8123 tasks_emulator -host 0.0.0.0 -port 8123 -queue projects/stakemetrics-project/locations/us-central1/queues/run-strategy-against-odds -queue projects/stakemetrics-project/locations/us-central1/queues/run-trend-analysis -queue projects/stakemetrics-project/locations/us-central1/queues/message-queue -queue projects/stakemetrics-project/locations/us-central1/queues/save-match-result -queue projects/stakemetrics-project/locations/us-central1/queues/bet-queue -queue projects/stakemetrics-project/locations/us-central1/queues/close-bet -queue projects/stakemetrics-project/locations/us-central1/queues/close-odd-snapshot -queue projects/stakemetrics-project/locations/us-central1/queues/bet-report-queue -queue projects/stakemetrics-project/locations/us-central1/queues/discard-hanging-bet queue projects/stakemetrics-project/locations/us-central1/queues/auto-bet-queue
- Verifique os nomes das filas padrões criadas para identificar se precisam ser criadas novas filas.
