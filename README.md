# saas-monorepo-template
A template for a Saas with Kotlin on the backend and React on the frontend

### Application Properties (Backend Env)
Propriedades:
- app.frontend.base.url=http://localhost:3000
- spring.datasource.url=jdbc:mysql://localhost:3306/backend
- spring.datasource.username=root
- spring.jpa.hibernate.ddl-auto=update
- spring.datasource.password={{senha}}
- api.security.token.secret={{senha}}
- stripe.api.key=
- api.security.token.secret=UIBmIuZ_BvwA
- stripe.pricing.tabe={{pricing_table_id}}
- stripe.api.key={{api_key}}
- stripe.public.key={{public_key}}

### .env (Frontend Env)
Variáveis:
- REACT_APP_BASE_URL=http://localhost:8080

### Demais instalações
- Para testar a integração com Stripe para pagamentos, é preciso [instalar a CLI do Stripe](https://github.com/stripe/stripe-cli/releases/tag/v1.21.0) para redirecionar o webhook para o endpoint local.
- Na CLI, rode o seguinte comando: stripe login (para fazer login na sua conta Stripe)
- Após logado, rode o comando: stripe listen --forward-to localhost:8080/subscription/notify-events --skip-verify
- Limpe os clientes e assinaturas no Stripe para evitar problemas de sync
