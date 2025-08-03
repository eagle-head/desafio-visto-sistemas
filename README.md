# Sistema de Gerenciamento de Produtos

Sistema completo de gerenciamento de produtos desenvolvido com **Spring Boot** no backend e **Angular** no frontend, utilizando banco de dados **H2** em memória.

## 🚀 Tecnologias Utilizadas

### Backend

- **Java 21**
- **Spring Boot 3.5.4**
- **Spring Data JPA**
- **H2 Database** (em memória)
- **Maven**
- **Swagger/OpenAPI** para documentação da API
- **JUnit 5** e **Mockito** para testes

### Frontend

- **Angular 20**
- **Angular Material**
- **TypeScript**
- **RxJS**
- **Zod** para validação
- **Jasmine/Karma** para testes

### Containerização

- **Docker**
- **Docker Compose**
- **Nginx** (para servir o frontend em produção)

## 📋 Funcionalidades

- ✅ **CRUD completo** de produtos (Criar, Listar, Atualizar, Excluir)
- ✅ **Paginação** e **filtros** na listagem
- ✅ **Validação** de dados no frontend e backend
- ✅ **Tratamento de erros** centralizado
- ✅ **Interface responsiva** com Angular Material
- ✅ **Documentação** da API com Swagger
- ✅ **Notificações** para feedback do usuário
- ✅ **Confirmação** antes de excluir produtos

## 🛠️ Como Executar Localmente

### Pré-requisitos

- **Java 21**
- **Node.js 18+**
- **Maven 3.6+**
- **Angular CLI**

### Backend (Spring Boot)

```bash
# Navegar para o diretório do backend
cd backend

# Executar com Maven
./mvnw spring-boot:run

# Ou compilar e executar o JAR
./mvnw clean package
java -jar target/product-management-system.jar
```

O backend estará disponível em: http://localhost:8080

**Endpoints importantes:**

- API: http://localhost:8080/api/products
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (vazio)

### Frontend (Angular)

```bash
# Navegar para o diretório do frontend
cd frontend

# Instalar dependências
npm install

# Executar em modo de desenvolvimento
npm start
# ou
ng serve
```

O frontend estará disponível em: http://localhost:4200

## 🐳 Como Executar com Docker

### Usando Docker Compose (Recomendado)

```bash
# Na raiz do projeto
docker-compose up --build

# Para executar em background
docker-compose up -d --build

# Para parar os containers
docker-compose down
```

**Serviços disponíveis:**

- **Frontend**: http://localhost:80
- **Backend**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

### Executando containers individualmente

```bash
# Backend
cd backend
docker build -t product-management-backend .
docker run -p 8080:8080 product-management-backend

# Frontend
cd frontend
docker build -t product-management-frontend .
docker run -p 80:80 product-management-frontend
```

## 🧪 Como Executar os Testes

### Backend (Spring Boot)

```bash
cd backend

# Executar todos os testes
./mvnw test

# Executar testes com relatório de cobertura
./mvnw clean test jacoco:report

# Verificar cobertura mínima (80%)
./mvnw jacoco:check

# Ver relatório de cobertura
open target/site/jacoco/index.html
```

**Testes implementados:**

- ✅ **Testes unitários** para todas as camadas (Controller, Service, Repository)
- ✅ **Testes de integração** com banco H2
- ✅ **Testes de validação** e tratamento de exceções
- ✅ **Cobertura de código** > 80%

### Frontend (Angular)

```bash
cd frontend

# Executar testes unitários
npm test
# ou
ng test

# Executar testes em modo single-run
ng test --watch=false --browsers=ChromeHeadless

# Executar linting
npm run lint

# Verificar formatação
npm run format:check
```

> **⚠️ Nota sobre testes do frontend**: Por limitação de tempo, os testes do frontend não foram elaborados de forma mais robusta. Existem apenas os testes básicos gerados pelo Angular CLI. Em um ambiente de produção, seria recomendado implementar testes mais abrangentes incluindo testes de integração e E2E.

## 📁 Estrutura do Projeto

```
desafio-visto-sistemas/
├── backend/                    # API Spring Boot
│   ├── src/main/java/         # Código fonte
│   ├── src/test/java/         # Testes unitários
│   ├── Dockerfile             # Container do backend
│   └── pom.xml               # Dependências Maven
├── frontend/                  # Aplicação Angular
│   ├── src/app/              # Código fonte
│   ├── Dockerfile            # Container do frontend
│   └── package.json          # Dependências NPM
├── docker-compose.yml        # Orquestração dos containers
└── README.md                 # Este arquivo
```

## 📚 Documentação Adicional

- **[Desafio Original](desafio_visto_sistemas_1.md)** - Especificações completas do desafio
- **[Roteiro de Desenvolvimento](roteiro.md)** - Checklist e cronograma seguido
- **[Contexto para IA](CLAUDE.md)** - Informações técnicas para assistentes de IA

## 🚀 Deploy e Produção

O projeto está configurado para deploy usando Docker. Para ambiente de produção:

1. Configure variáveis de ambiente apropriadas
2. Use um banco de dados persistente (PostgreSQL, MySQL)
3. Configure HTTPS e certificados SSL
4. Implemente logging e monitoramento
5. Configure CI/CD pipeline

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto foi desenvolvido como desafio técnico para a Visto Sistemas.

---

**Desenvolvido com ❤️ usando Spring Boot + Angular**
