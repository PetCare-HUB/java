SET SERVEROUTPUT ON;

------------------------------------------------------------
-- PETCARE HUB - SEPARA LEITURA_SENSOR EM 3 TABELAS
-- Alinha o schema com as 3 entidades Java (LeituraColeira,
-- LeituraComedouro, LeituraAmbiente) em vez da tabela
-- genérica LEITURA_SENSOR (tipo_leitura/valor/unidade).
------------------------------------------------------------

------------------------------------------------------------
-- 1. Efeito colateral: ALERTA_SAUDE tinha FK obrigatória
-- para LEITURA_SENSOR. O Java nunca mapeou esse campo,
-- então o alerta passa a existir sem apontar para uma
-- leitura específica.
------------------------------------------------------------

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE ALERTA_SAUDE DROP CONSTRAINT fk_alerta_leitura';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_alerta_leitura';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE ALERTA_SAUDE DROP COLUMN id_leitura';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

------------------------------------------------------------
-- 2. Remove a tabela genérica antiga
------------------------------------------------------------

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE LEITURA_SENSOR CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_leitura_sensor';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

------------------------------------------------------------
-- 3. LEITURA_COLEIRA
------------------------------------------------------------

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE LEITURA_COLEIRA CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

CREATE TABLE LEITURA_COLEIRA (
    id_leitura_coleira  NUMBER(10)   NOT NULL,
    id_pet              NUMBER(10)   NOT NULL,
    status_atividade    VARCHAR2(30) NOT NULL,
    nivel_bateria       NUMBER(3)    NOT NULL,
    timestamp_leitura   TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,

    CONSTRAINT pk_leitura_coleira PRIMARY KEY (id_leitura_coleira),
    CONSTRAINT fk_leitura_coleira_pet FOREIGN KEY (id_pet) REFERENCES PET (id_pet),
    CONSTRAINT ck_leitura_coleira_status CHECK (
        status_atividade IN ('ATIVO', 'MODERADO', 'SEDENTARIO')
    ),
    CONSTRAINT ck_leitura_coleira_bateria CHECK (nivel_bateria BETWEEN 0 AND 100)
);

CREATE SEQUENCE seq_leitura_coleira START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
ALTER TABLE LEITURA_COLEIRA MODIFY id_leitura_coleira DEFAULT seq_leitura_coleira.NEXTVAL;

CREATE INDEX idx_leitura_coleira_pet ON LEITURA_COLEIRA (id_pet);

------------------------------------------------------------
-- 4. LEITURA_COMEDOURO
------------------------------------------------------------

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE LEITURA_COMEDOURO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

CREATE TABLE LEITURA_COMEDOURO (
    id_leitura_comedouro  NUMBER(10)    NOT NULL,
    id_pet                NUMBER(10)    NOT NULL,
    nivel_racao_pct       NUMBER(3)     NOT NULL,
    peso_consumido_g      NUMBER(8,2)   NOT NULL,
    timestamp_leitura     TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,

    CONSTRAINT pk_leitura_comedouro PRIMARY KEY (id_leitura_comedouro),
    CONSTRAINT fk_leitura_comedouro_pet FOREIGN KEY (id_pet) REFERENCES PET (id_pet),
    CONSTRAINT ck_leitura_comedouro_racao CHECK (nivel_racao_pct BETWEEN 0 AND 100),
    CONSTRAINT ck_leitura_comedouro_peso CHECK (peso_consumido_g >= 0)
);

CREATE SEQUENCE seq_leitura_comedouro START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
ALTER TABLE LEITURA_COMEDOURO MODIFY id_leitura_comedouro DEFAULT seq_leitura_comedouro.NEXTVAL;

CREATE INDEX idx_leitura_comedouro_pet ON LEITURA_COMEDOURO (id_pet);

------------------------------------------------------------
-- 5. LEITURA_AMBIENTE
------------------------------------------------------------

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE LEITURA_AMBIENTE CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

CREATE TABLE LEITURA_AMBIENTE (
    id_leitura_ambiente   NUMBER(10)   NOT NULL,
    id_pet                NUMBER(10)   NOT NULL,
    temperatura_ambiente  NUMBER(5,2)  NOT NULL,
    umidade_pct           NUMBER(3)    NOT NULL,
    qualidade_ar_ppm      NUMBER(6)    NOT NULL,
    pet_presente          NUMBER(1)    NOT NULL,
    timestamp_leitura     TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,

    CONSTRAINT pk_leitura_ambiente PRIMARY KEY (id_leitura_ambiente),
    CONSTRAINT fk_leitura_ambiente_pet FOREIGN KEY (id_pet) REFERENCES PET (id_pet),
    CONSTRAINT ck_leitura_ambiente_umidade CHECK (umidade_pct BETWEEN 0 AND 100),
    CONSTRAINT ck_leitura_ambiente_ar CHECK (qualidade_ar_ppm >= 0),
    CONSTRAINT ck_leitura_ambiente_presente CHECK (pet_presente IN (0,1))
);

CREATE SEQUENCE seq_leitura_ambiente START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
ALTER TABLE LEITURA_AMBIENTE MODIFY id_leitura_ambiente DEFAULT seq_leitura_ambiente.NEXTVAL;

CREATE INDEX idx_leitura_ambiente_pet ON LEITURA_AMBIENTE (id_pet);

------------------------------------------------------------
-- TESTE FINAL: confirma que as 3 tabelas novas existem
-- e que LEITURA_SENSOR sumiu
------------------------------------------------------------

SELECT table_name
FROM user_tables
WHERE table_name IN ('LEITURA_COLEIRA', 'LEITURA_COMEDOURO', 'LEITURA_AMBIENTE', 'LEITURA_SENSOR')
ORDER BY table_name;
