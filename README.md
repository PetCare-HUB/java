# PetCare Hub — API Java Advanced

API REST principal do **PetCare Hub**, desenvolvida em **Java 17 + Spring Boot** para o Challenge FIAP 2026 — CLYVO VET.

O objetivo da API é apoiar a continuidade do cuidado do pet, conectando responsáveis, pets, consultas, leituras IoT simuladas, alertas automáticos, score de saúde e plano preventivo.

> Observação de arquitetura: a API Java é responsável pelo domínio principal e processamento dos dados. A API .NET fica responsável pelo dashboard B2B das clínicas, consumindo dados processados pelo Java ou consultando a mesma base.

---

## Tecnologias

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA (com Criteria / Specifications para filtros dinâmicos)
- Bean Validation (incluindo validações customizadas)
- H2 Database para execução local
- Oracle via perfil `oracle`
- Swagger/OpenAPI (Springdoc)
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

- CRUD de responsáveis com busca por nome, email e CPF
- CRUD de clínicas com vínculo de domínio
- CRUD de pets com busca combinada via JPA Specifications
- CRUD de consultas com busca por pet
- DTOs de entrada e saída
- Bean Validation nos requests
- Validações customizadas (`@MaxAge` para idade máxima do pet)
- Paginação e ordenação com Pageable
- Busca combinada de pets com filtros dinâmicos (Criteria API)
- Tratamento global de exceções com `@RestControllerAdvice`
- Cache em protocolos preventivos
- Score de saúde do pet
- Alertas automáticos a partir de leituras IoT simuladas
- Timeline longitudinal do pet
- Plano preventivo
- Métricas, agenda e alertas IoT por clínica
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

### Tipo de alerta

```txt
BATERIA_COLEIRA_BAIXA
RACAO_BAIXA
BAIXA_ALIMENTACAO
AMBIENTE_RUIM
TEMPERATURA_FORA_DA_FAIXA
UMIDADE_FORA_DA_FAIXA
SCORE_CRITICO
CONSULTA_ATRASADA
```

### Nível do alerta

```txt
BAIXO
MEDIO
ALTO
CRITICO
```

### Tipo de evento preventivo

```txt
VACINA
CHECKUP
VERMIFUGO
RETORNO
MEDICAMENTO
```

---

## Endpoints

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
POST   /clinicas
PUT    /clinicas/{id}
DELETE /clinicas/{id}
GET    /clinicas/{id}/pets-em-risco?nivel=vermelho
GET    /clinicas/{id}/metricas?periodo=90d
GET    /clinicas/{id}/agenda/proximos-30-dias
GET    /clinicas/{id}/alertas-iot/hoje
```

> O parâmetro `periodo` aceita valores no formato `Nd` onde `N` é o número de dias. Exemplos: `7d`, `30d`, `90d` (padrão).

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

> O endpoint `/pets/busca` usa **JPA Specifications** para combinar filtros dinâmicos. Todos os parâmetros são opcionais e podem ser combinados livremente.

### Score de saúde do pet

```http
GET  /pets/{id}/score-saude
POST /pets/{id}/score-saude/calcular
GET  /pets/{id}/score-saude/historico
```

### Timeline e plano preventivo do pet

```http
GET /pets/{id}/timeline
GET /pets/{id}/plano-preventivo
```

### Consultas

```http
GET    /consultas
GET    /consultas/{id}
GET    /consultas/pet/{petId}
POST   /consultas
PUT    /consultas/{id}
DELETE /consultas/{id}
```

### Alertas

```http
GET    /alertas?petId=1&tipo=BATERIA_COLEIRA_BAIXA&resolvido=false
GET    /pets/{id}/alertas/ativos
POST   /alertas
PUT    /alertas/{id}/resolver
DELETE /alertas/{id}
```

> O endpoint `/alertas` usa **JPA Specifications** para filtros dinâmicos por `petId`, `tipo` e `resolvido`.

### Leituras IoT simuladas

```http
POST /leituras/coleira
POST /leituras/comedouro
POST /leituras/ambiente

GET /pets/{id}/leituras/coleira
GET /pets/{id}/leituras/comedouro
GET /pets/{id}/leituras/ambiente
```

### Protocolos preventivos (com cache Caffeine)

```http
GET  /protocolos-preventivos
GET  /protocolos-preventivos/por-especie?especie=CAO
POST /protocolos-preventivos
```

### Eventos preventivos

```http
POST /eventos-preventivos
PUT  /eventos-preventivos/{id}/realizar
```

---

## Validações Bean Validation

Os DTOs de request usam Bean Validation padrão (`@NotBlank`, `@NotNull`, `@Size`, `@Positive`, `@Min`, `@Max`, `@Email`, `@Past`, `@PastOrPresent`, `@FutureOrPresent`).

### Validação customizada `@MaxAge`

Criamos a validação customizada `@MaxAge` para limitar a idade máxima de um pet a partir da data de nascimento. Está aplicada em `PetRequest.dataNascimento`:

```java
@PastOrPresent
@MaxAge(25)
LocalDate dataNascimento
```

Implementação em `validation/MaxAge.java` + `validation/MaxAgeValidator.java`. Isso demonstra o uso de `ConstraintValidator` customizado, complementando as validações padrão.

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

### Criar clínica

```json
{
  "nome": "Clyvo Vet Paulista",
  "cnpj": "12.345.678/0001-90",
  "endereco": "Av. Paulista, 1000",
  "telefone": "1133334444"
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

> Pets com `dataNascimento` há mais de 25 anos retornam `400 Bad Request` (validação `@MaxAge(25)`).

### Criar consulta

```json
{
  "petId": 1,
  "clinicaId": 1,
  "dataConsulta": "2026-12-10T10:30:00",
  "tipo": "CHECKUP",
  "observacoes": "Consulta preventiva de rotina",
  "valor": 180.0
}
```

> O campo `dataConsulta` precisa ser presente ou futuro (`@FutureOrPresent`).

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

### Criar alerta manual

```json
{
  "petId": 1,
  "tipo": "CONSULTA_ATRASADA",
  "nivel": "ALTO",
  "mensagem": "Pet precisa de consulta preventiva."
}
```

### Criar protocolo preventivo

```json
{
  "especie": "CAO",
  "tipo": "VACINA",
  "nome": "V10",
  "descricao": "Vacina polivalente anual para cães",
  "idadeMesesAplicacao": 2,
  "intervaloReforcoDias": 365
}
```

### Criar evento preventivo

```json
{
  "petId": 1,
  "tipo": "CHECKUP",
  "descricao": "Retorno preventivo pós-alerta",
  "dataPrevista": "2026-12-20",
  "realizado": false
}
```

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

Quando o score cai abaixo de 50, a API cria automaticamente um alerta do tipo `SCORE_CRITICO`.

---

## Organização do projeto

```txt
src/main/java/fiap/com/br/petcarehub
├── config              # DataLoader (seed) + SwaggerConfig
├── controller          # REST controllers
├── dto
│   ├── request         # DTOs de entrada (com validação)
│   └── response        # DTOs de saída
├── entity              # Entidades JPA
├── enums               # Enums do domínio
├── exception           # GlobalExceptionHandler + ErroResponse
├── repository          # Spring Data JPA repositories
├── service             # Serviços de domínio
├── specification       # JPA Specifications (filtros dinâmicos)
└── validation          # @MaxAge customizado
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

Arquivo principal na pasta `docs/`:

```txt
docs/petcarehub_insomnia_collection.json
```

A collection cobre todos os endpoints da API e inclui casos de teste para validação Bean Validation (ex: rejeição de pet com idade superior a 25 anos).

Para importar no Insomnia:

1. `Application` → `Preferences` → `Data` → `Import Data` → `From File`
2. Selecione o arquivo `petcarehub_insomnia_collection.json`
3. As requisições virão organizadas por entidade

---

## Integração com o Challenge

A API Java cobre o núcleo da solução:

- continuidade do cuidado do pet;
- histórico longitudinal;
- geração de alertas preventivos;
- cálculo de score de saúde;
- dados estruturados para app mobile e dashboard clínico;
- base para integração com IoT via MQTT nas próximas sprints.

## 👥 Integrantes da Equipe

| Nome | RM | Turma | GitHub | LinkedIn |
|---|---|---|---|---|
| Alexander Dennis Isidro Mamani | 565554 | 2TDSPG | [alex-isidro](https://github.com/alex-isidro) | [LinkedIn](https://www.linkedin.com/in/alexander-dennis-a3b48824b/) |
| Kelson Zhang | 563748 | 2TDSPG | [KelsonZh0](https://github.com/KelsonZh0) | [LinkedIn](https://www.linkedin.com/in/kelson-zhang-211456323/) |

---