# ⚽ Stake Metrics

> High-Frequency Sports Analytics & Automated Bet Execution Platform (SaaS)  
> *Architected with Kotlin, Spring Boot 3, Clean Architecture, Google Cloud Run & Tasks, and React.*

---

[![Status: Inactive](https://img.shields.io/badge/Status-Inactive%20%2F%20Archived-lightgrey.svg)](#-note-on-project-origin--discontinuation)
[![Active Period](https://img.shields.io/badge/Active%20Period-2024--06--29%20to%202025--07--08-blue.svg)](#-note-on-project-origin--discontinuation)
[![Initial Commit](https://img.shields.io/badge/Started-June%2029%2C%202024-informational.svg)](#-note-on-project-origin--discontinuation)
[![Shutdown Commit](https://img.shields.io/badge/Shutdown-July%208%2C%202025-orange.svg)](#-note-on-project-origin--discontinuation)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg?logo=kotlin)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.2-green.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg?logo=openjdk)](https://openjdk.org/)
[![React](https://img.shields.io/badge/React-18-61dafb.svg?logo=react)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-blue.svg?logo=typescript)](https://www.typescriptlang.org/)
[![GCP](https://img.shields.io/badge/GCP-Cloud_Run_%7C_Cloud_Tasks-4285F4.svg?logo=googlecloud)](https://cloud.google.com/)
[![AWS](https://img.shields.io/badge/AWS-RDS_MySQL-232F3E.svg?logo=amazon-aws)](https://aws.amazon.com/rds/)
[![License: Non-Commercial](https://img.shields.io/badge/License-Source--Available%20%2F%20Non--Commercial-blue.svg)](LICENSE)

---

## 📖 Note on Project Origin & Discontinuation

> Stake Metrics came out of two converging goals early in my software career. The first was an engineering milestone: I wanted to build and operate a complete full-stack product end to end as a solo developer, taking an idea from an empty repository through cloud architecture, automated subscriptions, production deployment, and real paying customers.
>
> The second was practical machine learning research. Like many people studying predictive modeling, I needed data-rich environments with clear, verifiable feedback loops. While financial market forecasting is the standard route, I chose to explore sports betting markets and odds movements instead. Sports data streams provided equally dense time-series data with lower regulatory barriers, accessible feeds, and measurable edges that allowed immediate validation of model predictions against bookmaker odds. The motivation was never sports gambling itself; it was finding a sandbox where data was abundant, hypothesis testing was cheap, and feedback cycles were fast.
>
> Bringing those two efforts together turned a technical experiment into an operational SaaS platform. The software performed well under load, scaled as a multi-module Kotlin and Cloud Tasks system, and attracted customers, peaking at nearly 100 active users.
>
> The decision to shut it down came down to how the project evolved. As commercial betting platforms expanded rapidly across Brazil, the social harm and friction tied to gambling became hard to ignore. What started as an individual study in predictive models and a solo engineering exercise had turned into an active commercial betting operation with real retail customers.
>
> I decided I no longer wanted to participate in that market or profit from it. On July 8, 2025, I permanently shut down production operations.
>
> The codebase remains here as an engineering portfolio piece: an entire system built from scratch to a production-ready, distributed architecture capable of high-throughput data processing for real users.

---

## 🛠️ What the Platform Does

Stake Metrics operated as a cloud-native SaaS that tracked live and pre-match sports events, identified statistically backed market opportunities, and executed user strategies autonomously.

### Core Capabilities

- **Real-Time Market Ingestion**: Polls and ingests match feeds, live odds fluctuations, and game statistics across global football and FIFA eSports matches.
- **Rules Engine & Strategy Evaluation**: Evaluates live odds against user-configured mathematical models (e.g., minimum expected value thresholds, volume triggers, match timing, bankroll percentage allocations).
- **Asynchronous Execution Pipeline**: Distributes ingestion and execution tasks across managed cloud queues, ensuring resilience against upstream rate limits and avoiding traffic spikes.
- **Automated Bet Dispatch**: Sends formatted execution webhooks to external execution partners when predefined market criteria trigger.
- **Real-Time Alerts & Reporting**: Delivers instant bet execution alerts and match updates via Telegram bots, alongside performance dashboards tracking return on investment (ROI), strike rate, and bankroll curves.
- **Subscription Management**: Integrates Stripe for self-service subscription plans, customer checkout sessions, and automated billing lifecycles.

---

## 🏗️ Architecture & Modules

The backend is built as a Gradle multi-module project enforcing Clean Architecture principles, strict boundary isolation, and dependency inversion:

```mermaid
graph TD
    HTTP["http<br/>(REST Controllers, Auth & Queue Endpoints)"] --> APP["application<br/>(Core Domain, Entities, Use Cases & Services)"]
    INT["integration<br/>(Stripe, Brevo, Telegram, BetsAPI, AutoBettor)"] --> APP
    PERS["persistence<br/>(JPA Entities, Repositories, Flyway Migrations)"] --> APP
    ROOT["back-end:root<br/>(Spring Boot Entrypoint & Config)"] --> HTTP
    ROOT --> INT
    ROOT --> PERS
    FRONT["front-end<br/>(React, TypeScript, Chakra UI)"] -.->|REST API| HTTP
```

### Module Breakdown

| Module | Purpose | Key Technologies |
| :--- | :--- | :--- |
| **`application`** | Core business logic, domain entities, rules engine, strategy matching, and interface contracts (`IAutoBettorService`, `IEmailService`, `ITelegramService`). Decoupled from framework dependencies. | Kotlin, Pure Domain |
| **`integration`** | Outbound adapters implementing application contracts: Stripe billing, transactional emails, Telegram alert bots, odds providers, and generic webhook auto-bettors. | OkHttp, Stripe Java SDK, Cloud Tasks Client |
| **`persistence`** | Database models, Spring Data JPA repositories, query optimizations, and Flyway database migrations. | Hibernate, JPA, Flyway, MySQL |
| **`http`** | Inbound HTTP adapters: REST API controllers, security filters, JWT authentication, and Google Cloud Tasks queue receivers. | Spring MVC, Spring Security |
| **`front-end`** | Single Page Application (SPA) offering operational dashboards, strategy configuration, analytics charts, and integration settings. | React 18, TypeScript, Chakra UI |

---

## ⚡ Asynchronous Ingestion & Execution Pipeline

To manage continuous polling and parallel strategy evaluations without blocking web threads or exceeding external rate limits, the platform relies on Google Cloud Tasks and Google Cloud Scheduler:

```mermaid
sequenceDiagram
    autonumber
    actor Cron as Cloud Scheduler
    participant Queue as Cloud Tasks
    participant API as Queue Controller
    participant Engine as Strategy Engine
    participant DB as AWS RDS MySQL
    participant Partner as Webhook AutoBettor
    participant User as Telegram Bot

    Cron->>API: Trigger scheduled match and odds sync
    API->>Queue: Enqueue tasks per match or league
    Queue->>API: Deliver task with backoff and retry policy
    API->>Engine: Evaluate odds against active strategies
    Engine->>DB: Check historical trends and persist snapshot
    alt Strategy Conditions Met
        Engine->>Partner: Dispatch automated bet via Webhook
        Engine->>User: Send notification to user channel
        Engine->>DB: Record bet result and bankroll update
    end
```

---

## 🧰 Tech Stack & Cloud Infrastructure

- **Languages & Frameworks**: Kotlin 1.9, Java 17, Spring Boot 3.3, React 18, TypeScript.
- **Compute & Serverless**: Google Cloud Run (Containerized Microservices).
- **Queuing & Scheduling**: Google Cloud Tasks (Distributed task processing with exponential backoff), Google Cloud Scheduler.
- **Database & Storage**: AWS RDS (MySQL 8), Flyway database migrations.
- **Security & Identity**: Stateless JWT, role-based authorization, rate-limiting headers.
- **External Services**: Stripe (Subscriptions & Webhooks), Brevo (Transactional Email), Telegram Bot API.

---

## 💻 Local Development Setup

### Prerequisites

- JDK 17+
- Node.js 18+ & npm
- Docker & Docker Compose (for local MySQL if needed)

### 1. Backend Setup

```bash
cd back-end

# 1. Prepare configuration
cp src/main/resources/application.properties.example src/main/resources/application-dev.properties

# 2. Run test suite
./gradlew test

# 3. Build artifact
./gradlew build
```

### 2. Frontend Setup

```bash
cd front-end

# 1. Prepare environment variables
cp .env.example .env.development

# 2. Install dependencies and start development server
npm install
npm start
```

---

## 📄 License & Intellectual Property

Copyright (c) 2024-2026 Tiago Ornelas. All rights reserved.

This repository is licensed under a **Source-Available & Non-Commercial Personal Use License**. See the full terms in the [LICENSE](LICENSE) file.

### Summary of Terms

- **Permitted**:
  - **Local Execution & Testing**: Running, testing, and using the software locally for private personal or educational purposes.
  - **Personal Modifications**: Modifying, adapting, and building upon the codebase strictly for personal, private, non-commercial use on your own environment.
  - **Inspection & Analysis**: Viewing and evaluating the source code and architecture for educational and technical research.
- **Strictly Prohibited**:
  - **Commercial Exploitation**: Using, copying, distributing, sublicensing, selling, renting, or monetizing this software (or modified versions), in whole or in part, as a commercial product, SaaS, or hosted service.
  - **Public Redistribution**: Publishing, mirroring, or distributing this source code (original or modified) on third-party repositories or public package registries without prior written authorization.

### ⚠️ Disclaimer on Real-Money Gambling

While local personal and educational use is permitted, the author does not recommend, encourage, or endorse using this software, its strategies, or its algorithms for real-money gambling or financial speculation. Gambling carries substantial financial risk, including total loss of capital. The author assumes no responsibility or liability for any financial losses, damages, or consequences resulting from the use or misuse of this code.
