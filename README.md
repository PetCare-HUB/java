# 🐾 PetCare Hub

Sistema de gerenciamento veterinário desenvolvido com Java e Spring Boot, 

---

# 📖 Visão Geral

O **PetCare Hub** é uma API REST desenvolvida para centralizar o gerenciamento de informações relacionadas ao ecossistema veterinário.

O sistema permite:

* gerenciamento de responsáveis pelos animais
* cadastro de pets
* gerenciamento de clínicas veterinárias
* registro de consultas veterinárias
* paginação e filtros de busca
* documentação automática da API

## 🎯 Objetivo

O projeto foi desenvolvido com foco em:

* aplicação prática de conceitos de Java Advanced
* modelagem relacional utilizando JPA/Hibernate
* construção de APIs RESTful com Spring Boot
* implementação de validações de domínio
* organização em arquitetura em camadas
* documentação profissional de APIs


---

# 🛠️ Tecnologias Utilizadas

## Backend

* Java 17
* Spring Boot 4
* Spring Web MVC
* Spring Data JPA
* Spring Validation
* Spring Cache
* Hibernate
* Lombok

## Banco de Dados

* Oracle Database

## Documentação

* Swagger/OpenAPI (springdoc-openapi)

## Build e Gerenciamento

* Maven

---

# 📁 Estrutura de Pastas

```text
src/main/java/fiap/com/br/petcarehub
│
├── config
├── controller
├── dto
├── entity
├── enums
├── projection
├── repository
├── service
└── validation
```

## 📂 Descrição das Pastas

| Pasta        | Responsabilidade                        |
| ------------ | --------------------------------------- |
| `config`     | Configurações da aplicação e Swagger    |
| `controller` | Endpoints REST                          |
| `dto`        | Objetos de transferência de dados       |
| `entity`     | Entidades JPA                           |
| `enums`      | Enumerações do domínio                  |
| `projection` | Projeções JPA para consultas otimizadas |
| `repository` | Persistência de dados                   |
| `service`    | Regras de negócio                       |
| `validation` | Validações customizadas                 |

---

# ⚙️ Funcionalidades

# ✅ Funcionalidades Implementadas

## Clínicas

* CRUD completo de clínicas
* paginação
* ordenação

## Responsáveis

* CRUD completo de responsáveis
* busca por nome
* busca por email
* busca por CPF
* paginação
* projections customizadas

## Pets

* CRUD completo de pets
* busca por nome
* busca por espécie
* busca por raça
* paginação
* projections customizadas
* validação customizada de idade

## Consultas

* CRUD completo de consultas
* DTO de resposta
* paginação
* relacionamento entre pet e clínica

## Infraestrutura

* documentação Swagger/OpenAPI
* validações Bean Validation
* queries customizadas JPQL
* projections JPA
* paginação
* ordenação



---


# 🌐 Endpoints da API

# 📍 Clínicas

| Método | Endpoint             | Descrição                |
| ------ | -------------------- | ------------------------ |
| GET    | `/clinicas`          | Lista todas as clínicas  |
| GET    | `/clinicas/paginado` | Lista clínicas paginadas |
| GET    | `/clinicas/{id}`     | Busca clínica por ID     |
| POST   | `/clinicas`          | Cria clínica             |
| PUT    | `/clinicas/{id}`     | Atualiza clínica         |
| DELETE | `/clinicas/{id}`     | Remove clínica           |

---

# 📍 Responsáveis

| Método | Endpoint                 | Descrição            |
| ------ | ------------------------ | -------------------- |
| GET    | `/responsaveis`          | Lista responsáveis   |
| GET    | `/responsaveis/paginado` | Lista paginada       |
| GET    | `/responsaveis/{id}`     | Busca por ID         |
| POST   | `/responsaveis`          | Cria responsável     |
| PUT    | `/responsaveis/{id}`     | Atualiza responsável |
| DELETE | `/responsaveis/{id}`     | Remove responsável   |
| GET    | `/responsaveis/nome`     | Busca por nome       |
| GET    | `/responsaveis/email`    | Busca por email      |
| GET    | `/responsaveis/cpf`      | Busca por CPF        |

---

# 📍 Pets

| Método | Endpoint         | Descrição              |
| ------ | ---------------- | ---------------------- |
| GET    | `/pets`          | Lista pets             |
| GET    | `/pets/paginado` | Lista paginada         |
| GET    | `/pets/{id}`     | Busca por ID           |
| POST   | `/pets`          | Cria pet               |
| PUT    | `/pets/{id}`     | Atualiza pet           |
| DELETE | `/pets/{id}`     | Remove pet             |
| GET    | `/pets/nome`     | Busca pets por nome    |
| GET    | `/pets/especie`  | Busca pets por espécie |
| GET    | `/pets/raca`     | Busca pets por raça    |

---

# 📍 Consultas

| Método | Endpoint              | Descrição             |
| ------ | --------------------- | --------------------- |
| GET    | `/consultas`          | Lista consultas       |
| GET    | `/consultas/paginado` | Lista paginada        |
| GET    | `/consultas/{id}`     | Busca consulta por ID |
| POST   | `/consultas`          | Cria consulta         |
| PUT    | `/consultas/{id}`     | Atualiza consulta     |
| DELETE | `/consultas/{id}`     | Remove consulta       |

---

# 📌 Parâmetros Aceitos

| Parâmetro | Descrição               |
| --------- | ----------------------- |
| `page`    | Número da página        |
| `size`    | Quantidade de registros |
| `sort`    | Campo de ordenação      |

## Exemplo

```http
GET /pets/paginado?page=0&size=5&sort=nome,asc
```

---

# 📚 Documentação Swagger

O projeto possui integração com Swagger/OpenAPI.


## Acesso

Após iniciar a aplicação:

```text
http://localhost:8080/swagger-ui.html
```

ou

```text
http://localhost:8080/swagger-ui/index.html
```

---

# ▶️ Como Executar o Projeto

## Pré-requisitos

* Java 17+
* Maven
* Oracle Database

---

# 📥 Clonar Repositório

```bash
git clone <URL_DO_REPOSITORIO>
```

```bash
cd PetCarehub
```

---

# ⚙️ Configurar Variáveis de Ambiente

## application.properties

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

###  Variáveis de Ambiente

| Variável      | Descrição             |
| ------------- | --------------------- |
| `DB_URL`      | URL de conexão Oracle |
| `DB_USERNAME` | Usuário do banco      |
| `DB_PASSWORD` | Senha do banco        |

---

#  Executar Aplicação

```bash
mvn spring-boot:run
```

##  Porta Padrão

```text
http://localhost:8080
```

---


#  Exemplos de Requests e Responses

# Criar Responsável

## Request

```json
{
  "nome": "Carlos Silva",
  "email": "carlos@email.com",
  "telefone": "11999999999",
  "cpf": "12345678900",
  "ativo": "S"
}
```

---

#  Criar Pet

## Request

```json
{
  "nome": "Rex",
  "especie": "CACHORRO",
  "raca": "Golden Retriever",
  "pesoKg": 28.5,
  "sexo": "M",
  "ativo": "S",
  "responsavel": {
    "id": 1
  },
  "clinica": {
    "id": 1
  }
}
```

---

#  Criar Consulta

## Request

```json
{
  "pet": {
    "id": 1
  },
  "clinica": {
    "id": 1
  },
  "dataConsulta": "2026-05-17",
  "tipo": "ROTINA",
  "descricao": "Consulta de rotina",
  "diagnostico": "Saudável",
  "valor": 120.00,
  "retornoRecomendado": "N"
}
```

---

# Exemplo de Response

```json
{
  "id": 1,
  "petNome": "Rex",
  "clinicaNome": "PetCare Center",
  "dataConsulta": "2026-05-17",
  "tipo": "ROTINA",
  "descricao": "Consulta de rotina",
  "diagnostico": "Saudável",
  "valor": 120.00,
  "retornoRecomendado": "N",
  "dataRetorno": null
}
```
