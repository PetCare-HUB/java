------------------------------------------------------------
-- CREDENCIAMENTO DE CLINICAS
--
-- A clinica passa a se auto-cadastrar (com senha) e so consegue
-- logar depois que um admin da PetCare Hub aprova. Isso exige:
--
--   1. CLINICA.status_acesso  - espelha o que o TUTOR ja tem (V6).
--      PRE_CADASTRADO = existe mas nao loga; ATIVO = aprovada.
--      O campo 'ativo' continua sendo a flag operacional usada
--      pelo .NET; status_acesso e so sobre poder autenticar.
--
--   2. UNIQUE no email da clinica - o login busca por findByEmail,
--      entao dois registros com o mesmo email deixariam a
--      autenticacao ambigua (retorna um dos dois, arbitrario).
--
--   3. Tabela ADMIN_CLYVO - o dono da decisao de quem entra na
--      rede. Nao e uma clinica nem um tutor, entao tem tabela
--      propria em vez de uma flag em CLINICA.
------------------------------------------------------------

------------------------------------------------------------
-- 1. STATUS DE ACESSO DA CLINICA
------------------------------------------------------------

ALTER TABLE CLINICA ADD (status_acesso VARCHAR2(20) DEFAULT 'PRE_CADASTRADO' NOT NULL);

-- Backfill: clinicas que ja existiam e ja conseguiam logar (tem
-- senha e estao ativas) continuam logando. O DEFAULT acima teria
-- deixado todas como PRE_CADASTRADO e derrubado o acesso de quem
-- ja usava o sistema.
UPDATE CLINICA
   SET status_acesso = 'ATIVO'
 WHERE senha_hash IS NOT NULL
   AND ativo = 'S';

ALTER TABLE CLINICA ADD CONSTRAINT ck_clinica_status_acesso
    CHECK (status_acesso IN ('PRE_CADASTRADO', 'ATIVO', 'BLOQUEADO', 'INATIVO'));

------------------------------------------------------------
-- 2. EMAIL DA CLINICA: alinhar tamanho e tornar unico
--
-- A coluna nasceu VARCHAR2(120) na V1, mas a entidade sempre
-- declarou length = 150. ddl-auto=validate nao compara tamanho,
-- entao a divergencia passou batido ate agora.
--
-- Oracle permite varios NULL num UNIQUE, entao clinicas antigas
-- sem email continuam validas.
------------------------------------------------------------

ALTER TABLE CLINICA MODIFY (email VARCHAR2(150));

ALTER TABLE CLINICA ADD CONSTRAINT uk_clinica_email UNIQUE (email);

------------------------------------------------------------
-- 3. ADMIN DA PETCARE HUB
------------------------------------------------------------

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_admin_clyvo';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

CREATE SEQUENCE seq_admin_clyvo START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE ADMIN_CLYVO (
    id_admin    NUMBER(10)      DEFAULT seq_admin_clyvo.NEXTVAL NOT NULL,
    nome        VARCHAR2(120)   NOT NULL,
    email       VARCHAR2(150)   NOT NULL,
    senha_hash  VARCHAR2(255)   NOT NULL,
    ativo       CHAR(1)         DEFAULT 'S' NOT NULL,

    CONSTRAINT pk_admin_clyvo PRIMARY KEY (id_admin),
    CONSTRAINT uk_admin_clyvo_email UNIQUE (email),
    CONSTRAINT ck_admin_clyvo_ativo CHECK (ativo IN ('S', 'N'))
);

------------------------------------------------------------
-- TESTE: confirmar coluna, constraints e tabela criadas
------------------------------------------------------------
SELECT column_name FROM user_tab_columns WHERE table_name = 'CLINICA' AND column_name = 'STATUS_ACESSO';
SELECT constraint_name FROM user_constraints WHERE constraint_name IN ('CK_CLINICA_STATUS_ACESSO', 'UK_CLINICA_EMAIL');
SELECT table_name FROM user_tables WHERE table_name = 'ADMIN_CLYVO';
