# PetCare Hub — API Java Advanced

API REST principal do **PetCare Hub**, desenvolvida em **Java 17 + Spring Boot** para o Challenge FIAP 2026 — CLYVO VET.

O objetivo da API é apoiar a continuidade do cuidado do pet, conectando responsáveis, pets, consultas, leituras IoT simuladas, alertas automáticos, score de saúde e plano preventivo.

> Observação de arquitetura: a API Java é responsável pelo domínio principal e processamento dos dados. A API .NET fica responsável pelo dashboard B2B das clínicas, consumindo dados processados pelo Java ou consultando a mesma base.

---

## Tecnologias

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 Database para execução local
- Oracle via perfil `oracle`
- Swagger/OpenAPI
- Spring Cache + Caffeine
- Lombok

---

## Como executar localmente

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

A API sobe em:

```txt
http://localhost:8080
```

Swagger:

```txt
http://localhost:8080/swagger-ui.html
```

H2 Console:

```txt
http://localhost:8080/h2-console
```

Dados do H2:

```txt
JDBC URL: jdbc:h2:mem:petcarehub
User: sa
Password: vazio
```

---

## Como executar com Oracle

Configure as variáveis de ambiente:

```bash
DB_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1
DB_USERNAME=PETCARE
DB_PASSWORD=petcare123
```

Execute com o perfil Oracle:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=oracle
```

No perfil Oracle, o projeto usa:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Ou seja, espera que as tabelas já existam conforme o script/modelagem da disciplina de Database.

---

## Principais recursos implementados

- CRUD de responsáveis
- CRUD de clínicas para vínculo de domínio
- CRUD de pets
- CRUD de consultas
- DTOs de entrada e saída
- Bean Validation nos requests
- Paginação e ordenação com Pageable
- Busca por parâmetros
- Busca combinada de pets
- Tratamento global de exceções com `@RestControllerAdvice`
- Cache em protocolos preventivos
- Score de saúde do pet
- Alertas automáticos a partir de leituras IoT simuladas
- Timeline longitudinal do pet
- Plano preventivo
- Swagger documentando os endpoints
- Collection Insomnia exportada

---

## Enums importantes para teste

Use exatamente estes valores no JSON:

### Espécie

```txt
CAO
GATO
OUTRO
```

### Sexo

```txt
M
F
```

### Tipo de consulta

```txt
CHECKUP
VACINA
EMERGENCIA
RETORNO
EXAME
```

### Status de atividade da coleira

```txt
DORMINDO
ATIVO
BRINCANDO
```

---

## Endpoints principais

### Responsáveis

```http
GET    /responsaveis
GET    /responsaveis/{id}
GET    /responsaveis/nome?nome=Kelson
GET    /responsaveis/email?email=petcare
GET    /responsaveis/cpf?cpf=123
POST   /responsaveis
PUT    /responsaveis/{id}
DELETE /responsaveis/{id}
```

### Clínicas

```http
GET    /clinicas
GET    /clinicas/{id}
GET    /clinicas/nome?nome=Clyvo
GET    /clinicas/{id}/pets-em-risco?nivel=vermelho
POST   /clinicas
PUT    /clinicas/{id}
DELETE /clinicas/{id}
```

### Pets

```http
GET    /pets
GET    /pets/{id}
GET    /pets/nome?nome=Rex
GET    /pets/busca?especie=CAO&raca=Golden&clinicaId=1&scoreMin=0&scoreMax=100
POST   /pets
PUT    /pets/{id}
DELETE /pets/{id}
```

### Score de saúde

```http
GET  /pets/{id}/score-saude
POST /pets/{id}/score-saude/calcular
GET  /pets/{id}/score-saude/historico
```

### Alertas

```http
GET    /alertas?petId=1&resolvido=false
GET    /pets/{id}/alertas/ativos
POST   /alertas
PUT    /alertas/{id}/resolver
DELETE /alertas/{id}
```

### Leituras IoT simuladas

```http
POST /leituras/coleira
POST /leituras/comedouro
POST /leituras/ambiente

GET /pets/{id}/leituras/coleira
GET /pets/{id}/leituras/comedouro
GET /pets/{id}/leituras/ambiente
```

### Timeline e plano preventivo

```http
GET /pets/{id}/timeline
GET /pets/{id}/plano-preventivo
```

### Protocolos preventivos com cache

```http
GET  /protocolos-preventivos
GET  /protocolos-preventivos/por-especie?especie=CAO
POST /protocolos-preventivos
```

---

## Exemplos de JSON

### Criar responsável

```json
{
  "nome": "Kelson Silva",
  "email": "kelson.petcare@example.com",
  "telefone": "11999990000",
  "cpf": "12345678901"
}
```

### Criar pet

```json
{
  "nome": "Rex",
  "especie": "CAO",
  "raca": "Golden Retriever",
  "dataNascimento": "2021-05-10",
  "pesoKg": 28.5,
  "sexo": "M",
  "condicoesCronicas": "Tendência a obesidade",
  "ativo": true,
  "responsavelId": 1,
  "clinicaId": 1
}
```

### Registrar leitura da coleira

```json
{
  "petId": 1,
  "statusAtividade": "ATIVO",
  "nivelBateria": 18
}
```

Ao registrar bateria menor que 20%, a API cria automaticamente um alerta de bateria baixa e recalcula o score.

### Registrar leitura do comedouro

```json
{
  "petId": 1,
  "nivelRacaoPct": 15,
  "pesoConsumidoG": 25
}
```

Ao registrar ração abaixo de 20% ou consumo muito baixo, a API cria alertas automáticos.

### Registrar leitura ambiente

```json
{
  "petId": 1,
  "temperaturaAmbiente": 34,
  "umidadePct": 80,
  "qualidadeArPpm": 1200,
  "petPresente": true
}
```

Ao registrar ambiente ruim, temperatura fora da faixa ou umidade inadequada, a API cria alertas automáticos.

---

## Regra do Score de Saúde

A API começa com 100 pontos e subtrai pontos conforme os riscos:

| Condição | Penalidade |
|---|---:|
| Bateria da coleira abaixo de 20% | -20 |
| Ração abaixo de 20% | -10 |
| Consumo alimentar muito baixo | -20 |
| Temperatura fora da faixa | -15 |
| Umidade fora da faixa | -10 |
| Qualidade do ar ruim | -15 |
| Alerta grave ativo | -10 |

Categorias:

| Score | Categoria |
|---|---|
| 80 a 100 | VERDE |
| 50 a 79 | AMARELO |
| 0 a 49 | VERMELHO |

---

## Organização do projeto

```txt
src/main/java/fiap/com/br/petcarehub
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── enums
├── exception
├── repository
└── service
```

---

## Documentação complementar

A pasta `docs/` contém:

```txt
docs/
├── arquitetura.md
├── arquitetura.png
├── classes-dominio.md
├── cronograma.md
├── der.png
├── diagrama-classes.png
└── petcarehub_insomnia_collection.json
```

---

## Collection Insomnia

Arquivo principal:

```txt
petcarehub_insomnia_collection.json
```

Cópia dentro de docs:

```txt
docs/petcarehub_insomnia_collection.json
```

---

## Integração com o Challenge

A API Java cobre o núcleo da solução:

- continuidade do cuidado do pet;
- histórico longitudinal;
- geração de alertas preventivos;
- cálculo de score de saúde;
- dados estruturados para app mobile e dashboard clínico;
- base para integração com IoT via MQTT nas próximas sprints.

