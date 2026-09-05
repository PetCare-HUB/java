SET SERVEROUTPUT ON;

------------------------------------------------------------
-- RENOMEIA RESPONSAVEL -> TUTOR (tabela, PK, constraints, FK, sequence)
------------------------------------------------------------

ALTER TABLE RESPONSAVEL RENAME TO TUTOR;

ALTER TABLE TUTOR RENAME COLUMN id_responsavel TO id_tutor;

ALTER TABLE TUTOR RENAME CONSTRAINT pk_responsavel TO pk_tutor;
ALTER TABLE TUTOR RENAME CONSTRAINT uk_responsavel_email TO uk_tutor_email;
ALTER TABLE TUTOR RENAME CONSTRAINT uk_responsavel_cpf TO uk_tutor_cpf;
ALTER TABLE TUTOR RENAME CONSTRAINT ck_responsavel_ativo TO ck_tutor_ativo;

-- PET referencia RESPONSAVEL via id_responsavel — só ela tem essa FK
ALTER TABLE PET RENAME COLUMN id_responsavel TO id_tutor;
ALTER TABLE PET RENAME CONSTRAINT fk_pet_responsavel TO fk_pet_tutor;

-- Sequence
RENAME seq_responsavel TO seq_tutor;

------------------------------------------------------------
-- TESTE: confirmar que ficou tudo certo
------------------------------------------------------------
SELECT table_name FROM user_tables WHERE table_name = 'TUTOR';
SELECT column_name FROM user_tab_columns WHERE table_name = 'PET' AND column_name = 'ID_TUTOR';
SELECT sequence_name FROM user_sequences WHERE sequence_name = 'SEQ_TUTOR';