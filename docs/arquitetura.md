# Arquitetura da API Java — PetCare Hub

## Papel da API Java

A API Java é o núcleo da solução PetCare Hub. Ela gerencia os dados principais do domínio e processa eventos de saúde do pet.

Responsabilidades:

- Cadastro de responsáveis e pets;
- Registro de consultas;
- Recebimento de leituras IoT simuladas;
- Geração automática de alertas;
- Cálculo do Score de Saúde;
- Organização de protocolos e eventos preventivos;
- Exposição de dados para App Mobile e API .NET.

## Separação com a API .NET

A API .NET é responsável pelo dashboard B2B da clínica. A Java mantém os dados processados e disponibiliza consultas como pets em risco, score e alertas.

## Fluxo macro

```txt
IoT / Wokwi / MQTT futuro
        ↓
POST /leituras/*
        ↓
API Java Spring Boot
        ↓
Validação + persistência + alertas + score
        ↓
Banco H2 local ou Oracle
        ↓
App Mobile / API .NET Dashboard
```

## Camadas

```txt
Controller → Service → Repository → Entity → Banco
       DTO Request/Response ↔ validação ↔ exception handler
```
