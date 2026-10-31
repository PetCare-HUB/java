------------------------------------------------------------
-- BASE DE CONHECIMENTO DO RAG (IA)
--
-- O assistente de IA responde com base em documentos de cuidados
-- com pets (arquivos .md em resources/conhecimento). Cada arquivo
-- e quebrado em trechos e cada trecho ganha um embedding (vetor).
--
-- Por que uma tabela comum e nao o tipo VECTOR do Oracle 23ai:
--   - o Oracle da FIAP nao tem o tipo VECTOR;
--   - o Render nao tem disco persistente, entao um arquivo (como o
--     SimpleVectorStore) seria perdido a cada deploy.
-- O embedding vai como JSON (array de floats) num CLOB e a
-- similaridade de cosseno e calculada na aplicacao. A base e
-- pequena (centenas de trechos), entao isso e suficiente.
--
-- hash_fonte guarda o SHA-256 do arquivo inteiro de origem: se o
-- arquivo nao mudou, a ingestao e pulada e nao gasta embeddings.
------------------------------------------------------------

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_documento_conhecimento';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

CREATE SEQUENCE seq_documento_conhecimento START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE DOCUMENTO_CONHECIMENTO (
    id_documento    NUMBER(10)      DEFAULT seq_documento_conhecimento.NEXTVAL NOT NULL,
    fonte           VARCHAR2(200)   NOT NULL,
    titulo          VARCHAR2(300),
    conteudo        CLOB            NOT NULL,
    metadados       CLOB,
    embedding       CLOB            NOT NULL,
    hash_fonte      VARCHAR2(64)    NOT NULL,
    data_indexacao  TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,

    CONSTRAINT pk_documento_conhecimento PRIMARY KEY (id_documento)
);

CREATE INDEX idx_doc_conhecimento_fonte ON DOCUMENTO_CONHECIMENTO (fonte);

------------------------------------------------------------
-- TESTE: confirmar tabela e indice criados
------------------------------------------------------------
SELECT table_name FROM user_tables WHERE table_name = 'DOCUMENTO_CONHECIMENTO';
SELECT index_name FROM user_indexes WHERE index_name = 'IDX_DOC_CONHECIMENTO_FONTE';
