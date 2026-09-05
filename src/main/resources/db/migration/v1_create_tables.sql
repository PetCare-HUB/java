SET SERVEROUTPUT ON;

------------------------------------------------------------
-- PETCARE HUB - CREATE TABLES
-- Challenge CLYVO VET
-- Banco base para Java + .NET
------------------------------------------------------------

------------------------------------------------------------
-- LIMPEZA OPCIONAL
-- Execute essa parte se quiser apagar e recriar as tabelas.
------------------------------------------------------------

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE ALERTA_SAUDE CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE SCORE_SAUDE CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE LEITURA_SENSOR CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE DISPOSITIVO_IOT CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE EVENTO_PREVENTIVO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE PROTOCOLO_PREVENTIVO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE CONSULTA CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE PET CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE CLINICA CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE RESPONSAVEL CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE LOG_ERROS CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

------------------------------------------------------------
-- 1. RESPONSAVEL
------------------------------------------------------------

CREATE TABLE RESPONSAVEL (
    id_responsavel  NUMBER(10)      NOT NULL,
    nome           VARCHAR2(100)   NOT NULL,
    email          VARCHAR2(120)   NOT NULL,
    telefone       VARCHAR2(20),
    cpf            VARCHAR2(11),
    data_cadastro  DATE            DEFAULT SYSDATE NOT NULL,
    ativo          CHAR(1)         DEFAULT 'S' NOT NULL,

    CONSTRAINT pk_responsavel PRIMARY KEY (id_responsavel),
    CONSTRAINT uk_responsavel_email UNIQUE (email),
    CONSTRAINT uk_responsavel_cpf UNIQUE (cpf),
    CONSTRAINT ck_responsavel_ativo CHECK (ativo IN ('S', 'N'))
);

------------------------------------------------------------
-- 2. CLINICA
------------------------------------------------------------

CREATE TABLE CLINICA (
    id_clinica  NUMBER(10)  DEFAULT seq_clinica.NEXTVAL  NOT NULL,
    nome           VARCHAR2(120)   NOT NULL,
    cnpj           VARCHAR2(14)    NOT NULL,
    email          VARCHAR2(120),
    telefone       VARCHAR2(20),
    endereco       VARCHAR2(200),
    ativo          CHAR(1)         DEFAULT 'S' NOT NULL,

    CONSTRAINT pk_clinica PRIMARY KEY (id_clinica),
    CONSTRAINT uk_clinica_cnpj UNIQUE (cnpj),
    CONSTRAINT ck_clinica_ativo CHECK (ativo IN ('S', 'N'))
);

------------------------------------------------------------
-- 3. PET
------------------------------------------------------------

CREATE TABLE PET (
    id_pet              NUMBER(10)      NOT NULL,
    id_responsavel      NUMBER(10)      NOT NULL,
    id_clinica          NUMBER(10)      NOT NULL,
    nome                VARCHAR2(80)    NOT NULL,
    especie             VARCHAR2(20)    NOT NULL,
    raca                VARCHAR2(80),
    data_nascimento     DATE,
    peso_kg             NUMBER(5,2)     NOT NULL,
    sexo                CHAR(1),
    condicoes_cronicas  VARCHAR2(300),
    data_cadastro       DATE            DEFAULT SYSDATE NOT NULL,
    ativo               CHAR(1)         DEFAULT 'S' NOT NULL,

    CONSTRAINT pk_pet PRIMARY KEY (id_pet),

    CONSTRAINT fk_pet_responsavel FOREIGN KEY (id_responsavel)
        REFERENCES RESPONSAVEL (id_responsavel),

    CONSTRAINT fk_pet_clinica FOREIGN KEY (id_clinica)
        REFERENCES CLINICA (id_clinica),

    CONSTRAINT ck_pet_especie CHECK (especie IN ('CAO', 'GATO', 'OUTRO')),
    CONSTRAINT ck_pet_peso CHECK (peso_kg > 0),
    CONSTRAINT ck_pet_sexo CHECK (sexo IN ('M', 'F')),
    CONSTRAINT ck_pet_ativo CHECK (ativo IN ('S', 'N'))
);

------------------------------------------------------------
-- 4. CONSULTA
------------------------------------------------------------

CREATE TABLE CONSULTA (
    id_consulta          NUMBER(10)      NOT NULL,
    id_pet               NUMBER(10)      NOT NULL,
    id_clinica           NUMBER(10)      NOT NULL,
    data_consulta        DATE            NOT NULL,
    tipo_consulta        VARCHAR2(30)    NOT NULL,
    descricao            VARCHAR2(500),
    diagnostico          VARCHAR2(500),
    valor                NUMBER(10,2),
    retorno_recomendado  CHAR(1)         DEFAULT 'N' NOT NULL,
    data_retorno         DATE,

    CONSTRAINT pk_consulta PRIMARY KEY (id_consulta),

    CONSTRAINT fk_consulta_pet FOREIGN KEY (id_pet)
        REFERENCES PET (id_pet),

    CONSTRAINT fk_consulta_clinica FOREIGN KEY (id_clinica)
        REFERENCES CLINICA (id_clinica),

    CONSTRAINT ck_consulta_tipo CHECK (
        tipo_consulta IN ('CHECKUP', 'VACINA', 'EMERGENCIA', 'RETORNO', 'EXAME')
    ),

    CONSTRAINT ck_consulta_retorno CHECK (retorno_recomendado IN ('S', 'N')),
    CONSTRAINT ck_consulta_valor CHECK (valor IS NULL OR valor >= 0)
);

------------------------------------------------------------
-- 5. PROTOCOLO_PREVENTIVO
------------------------------------------------------------

CREATE TABLE PROTOCOLO_PREVENTIVO (
    id_protocolo              NUMBER(10)      NOT NULL,
    especie                   VARCHAR2(20)    NOT NULL,
    raca                      VARCHAR2(80),
    tipo_evento               VARCHAR2(30)    NOT NULL,
    descricao                 VARCHAR2(300)   NOT NULL,
    idade_meses_recomendada   NUMBER(3),
    intervalo_dias            NUMBER(5),
    ativo                     CHAR(1)         DEFAULT 'S' NOT NULL,

    CONSTRAINT pk_protocolo_preventivo PRIMARY KEY (id_protocolo),

    CONSTRAINT ck_protocolo_especie CHECK (especie IN ('CAO', 'GATO', 'OUTRO')),

    CONSTRAINT ck_protocolo_tipo CHECK (
        tipo_evento IN ('VACINA', 'CHECKUP', 'VERMIFUGO', 'RETORNO')
    ),

    CONSTRAINT ck_protocolo_idade CHECK (
        idade_meses_recomendada IS NULL OR idade_meses_recomendada >= 0
    ),

    CONSTRAINT ck_protocolo_intervalo CHECK (
        intervalo_dias IS NULL OR intervalo_dias > 0
    ),

    CONSTRAINT ck_protocolo_ativo CHECK (ativo IN ('S', 'N'))
);

------------------------------------------------------------
-- 6. EVENTO_PREVENTIVO
------------------------------------------------------------

CREATE TABLE EVENTO_PREVENTIVO (
    id_evento         NUMBER(10)      NOT NULL,
    id_pet            NUMBER(10)      NOT NULL,
    id_protocolo      NUMBER(10),
    tipo_evento       VARCHAR2(30)    NOT NULL,
    descricao         VARCHAR2(300)   NOT NULL,
    data_prevista     DATE            NOT NULL,
    data_realizacao   DATE,
    status            VARCHAR2(20)    DEFAULT 'PENDENTE' NOT NULL,

    CONSTRAINT pk_evento_preventivo PRIMARY KEY (id_evento),

    CONSTRAINT fk_evento_pet FOREIGN KEY (id_pet)
        REFERENCES PET (id_pet),

    CONSTRAINT fk_evento_protocolo FOREIGN KEY (id_protocolo)
        REFERENCES PROTOCOLO_PREVENTIVO (id_protocolo),

    CONSTRAINT ck_evento_tipo CHECK (
        tipo_evento IN ('VACINA', 'CHECKUP', 'VERMIFUGO', 'RETORNO')
    ),

    CONSTRAINT ck_evento_status CHECK (
        status IN ('PENDENTE', 'REALIZADO', 'ATRASADO', 'CANCELADO')
    )
);

------------------------------------------------------------
-- 7. DISPOSITIVO_IOT
------------------------------------------------------------

CREATE TABLE DISPOSITIVO_IOT (
    id_dispositivo    NUMBER(10)      NOT NULL,
    id_pet            NUMBER(10)      NOT NULL,
    tipo_dispositivo  VARCHAR2(30)    NOT NULL,
    codigo_serie      VARCHAR2(80)    NOT NULL,
    data_ativacao     DATE            DEFAULT SYSDATE NOT NULL,
    ativo             CHAR(1)         DEFAULT 'S' NOT NULL,

    CONSTRAINT pk_dispositivo_iot PRIMARY KEY (id_dispositivo),

    CONSTRAINT fk_dispositivo_pet FOREIGN KEY (id_pet)
        REFERENCES PET (id_pet),

    CONSTRAINT uk_dispositivo_codigo UNIQUE (codigo_serie),

    CONSTRAINT ck_dispositivo_tipo CHECK (
        tipo_dispositivo IN ('COLEIRA', 'COMEDOURO', 'AMBIENTE')
    ),

    CONSTRAINT ck_dispositivo_ativo CHECK (ativo IN ('S', 'N'))
);

------------------------------------------------------------
-- 8. LEITURA_SENSOR
------------------------------------------------------------

CREATE TABLE LEITURA_SENSOR (
    id_leitura       NUMBER(10)      NOT NULL,
    id_pet           NUMBER(10)      NOT NULL,
    id_dispositivo   NUMBER(10)      NOT NULL,
    tipo_leitura     VARCHAR2(40)    NOT NULL,
    valor            NUMBER(10,2)    NOT NULL,
    unidade          VARCHAR2(20)    NOT NULL,
    data_leitura     TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    status_leitura   VARCHAR2(20)    DEFAULT 'NORMAL' NOT NULL,

    CONSTRAINT pk_leitura_sensor PRIMARY KEY (id_leitura),

    CONSTRAINT fk_leitura_pet FOREIGN KEY (id_pet)
        REFERENCES PET (id_pet),

    CONSTRAINT fk_leitura_dispositivo FOREIGN KEY (id_dispositivo)
        REFERENCES DISPOSITIVO_IOT (id_dispositivo),

    CONSTRAINT ck_leitura_tipo CHECK (
        tipo_leitura IN (
            'ATIVIDADE',
            'NIVEL_RACAO',
            'PESO_CONSUMIDO',
            'TEMPERATURA_AMBIENTE',
            'UMIDADE',
            'QUALIDADE_AR'
        )
    ),

    CONSTRAINT ck_leitura_status CHECK (
        status_leitura IN ('NORMAL', 'ATENCAO', 'CRITICO')
    )
);

------------------------------------------------------------
-- 9. ALERTA_SAUDE
------------------------------------------------------------

CREATE TABLE ALERTA_SAUDE (
    id_alerta          NUMBER(10)      NOT NULL,
    id_pet             NUMBER(10)      NOT NULL,
    id_leitura         NUMBER(10),
    tipo_alerta        VARCHAR2(40)    NOT NULL,
    nivel_alerta       VARCHAR2(20)    NOT NULL,
    mensagem           VARCHAR2(300)   NOT NULL,
    valor_detectado    NUMBER(10,2),
    limite_referencia  NUMBER(10,2),
    resolvido          CHAR(1)         DEFAULT 'N' NOT NULL,
    data_alerta        TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    data_resolucao     TIMESTAMP,

    CONSTRAINT pk_alerta_saude PRIMARY KEY (id_alerta),

    CONSTRAINT fk_alerta_pet FOREIGN KEY (id_pet)
        REFERENCES PET (id_pet),

    CONSTRAINT fk_alerta_leitura FOREIGN KEY (id_leitura)
        REFERENCES LEITURA_SENSOR (id_leitura),

    CONSTRAINT ck_alerta_nivel CHECK (
        nivel_alerta IN ('BAIXO', 'MEDIO', 'ALTO', 'CRITICO')
    ),

    CONSTRAINT ck_alerta_resolvido CHECK (resolvido IN ('S', 'N'))
);

------------------------------------------------------------
-- 10. SCORE_SAUDE
------------------------------------------------------------

CREATE TABLE SCORE_SAUDE (
    id_score            NUMBER(10)    NOT NULL,
    id_pet              NUMBER(10)    NOT NULL,
    score_total         NUMBER(3)     NOT NULL,
    score_atividade     NUMBER(3)     NOT NULL,
    score_alimentacao   NUMBER(3)     NOT NULL,
    score_ambiente      NUMBER(3)     NOT NULL,
    score_consulta      NUMBER(3)     NOT NULL,
    score_preventivo    NUMBER(3)     NOT NULL,
    categoria           VARCHAR2(20)  NOT NULL,
    data_calculo        TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,

    CONSTRAINT pk_score_saude PRIMARY KEY (id_score),

    CONSTRAINT fk_score_pet FOREIGN KEY (id_pet)
        REFERENCES PET (id_pet),

    CONSTRAINT ck_score_total CHECK (score_total BETWEEN 0 AND 100),
    CONSTRAINT ck_score_atividade CHECK (score_atividade BETWEEN 0 AND 100),
    CONSTRAINT ck_score_alimentacao CHECK (score_alimentacao BETWEEN 0 AND 100),
    CONSTRAINT ck_score_ambiente CHECK (score_ambiente BETWEEN 0 AND 100),
    CONSTRAINT ck_score_consulta CHECK (score_consulta BETWEEN 0 AND 100),
    CONSTRAINT ck_score_preventivo CHECK (score_preventivo BETWEEN 0 AND 100),

    CONSTRAINT ck_score_categoria CHECK (
        categoria IN ('VERDE', 'AMARELO', 'VERMELHO')
    )
);

------------------------------------------------------------
-- 11. LOG_ERROS
------------------------------------------------------------

CREATE TABLE LOG_ERROS (
    id_log           NUMBER(10)      NOT NULL,
    nome_procedure   VARCHAR2(100)   NOT NULL,
    nome_usuario     VARCHAR2(100)   NOT NULL,
    data_ocorrencia  TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    codigo_erro      NUMBER          NOT NULL,
    mensagem_erro    VARCHAR2(1000)  NOT NULL,

    CONSTRAINT pk_log_erros PRIMARY KEY (id_log)
);

------------------------------------------------------------
-- TESTE FINAL: LISTAR TABELAS CRIADAS
------------------------------------------------------------

SELECT table_name
FROM user_tables
WHERE table_name IN (
    'RESPONSAVEL',
    'CLINICA',
    'PET',
    'CONSULTA',
    'PROTOCOLO_PREVENTIVO',
    'EVENTO_PREVENTIVO',
    'DISPOSITIVO_IOT',
    'LEITURA_SENSOR',
    'ALERTA_SAUDE',
    'SCORE_SAUDE',
    'LOG_ERROS'
)
ORDER BY table_name;