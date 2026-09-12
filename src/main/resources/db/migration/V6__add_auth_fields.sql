SET SERVEROUTPUT ON;

------------------------------------------------------------
-- CAMPOS DE AUTENTICAÇÃO
------------------------------------------------------------

ALTER TABLE TUTOR ADD (
    senha_hash     VARCHAR2(255),
    status_acesso  VARCHAR2(20) DEFAULT 'PRE_CADASTRADO' NOT NULL
);

ALTER TABLE TUTOR ADD CONSTRAINT ck_tutor_status_acesso
    CHECK (status_acesso IN ('PRE_CADASTRADO', 'ATIVO', 'BLOQUEADO', 'INATIVO'));

ALTER TABLE CLINICA ADD (
    senha_hash VARCHAR2(255)
);

------------------------------------------------------------
-- TESTE
------------------------------------------------------------
SELECT column_name, data_type, nullable
FROM user_tab_columns
WHERE table_name = 'TUTOR' AND column_name IN ('SENHA_HASH', 'STATUS_ACESSO');