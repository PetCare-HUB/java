------------------------------------------------------------
-- AJUSTE DA BASE DE CONHECIMENTO E HISTORICO DA IA
--
-- A V13 criou DOCUMENTO_CONHECIMENTO na forma da primeira versao da
-- IA (hash_fonte do arquivo de origem, sem identificacao do modelo
-- de embedding). A versao atual do RAG precisa de:
--   - doc_id: id do trecho gerado pelo Spring AI (Document.getId),
--     usado no upsert e devolvido na busca;
--   - modelo_embedding e dimensoes: identificam com que modelo o
--     vetor foi gerado. Vetores de modelos (ou dimensoes) diferentes
--     nao sao comparaveis, e a aplicacao ignora trechos que nao
--     batem com o modelo configurado;
--   - hash_fonte deixa de existir: a reindexacao substitui a base
--     inteira, entao nao ha mais pulo por arquivo inalterado.
--
-- Os trechos ja gravados sao apagados de proposito: sao dados
-- DERIVADOS dos .md de resources/conhecimento e foram gerados por
-- outro modelo de embedding. A base e recriada por
-- POST /admin/ia/reindexar. Nenhuma outra tabela e tocada.
--
-- Cria tambem ANALISE_IA: cada analise, pergunta ou explicacao de
-- alerta gerada pela IA, com o score e o alerta vigentes no momento
-- e as fontes da base usadas - e o que permite auditar a IA depois.
--
-- E, de quebra, troca SCORE_SAUDE.observacao para semantica CHAR
-- (ver bloco abaixo), sem alterar nenhum dado.
------------------------------------------------------------

------------------------------------------------------------
-- SCORE_SAUDE.observacao: semantica de CARACTERES, nao de bytes.
-- A V12 criou VARCHAR2(1000), que no Oracle e 1000 BYTES por padrao.
-- O Java limita a observacao em 1000 caracteres e o texto tem acentos
-- (ate 3 bytes cada em AL32UTF8): uma observacao longa estouraria com
-- ORA-12899. MODIFY para CHAR nao altera nenhum dado existente.
------------------------------------------------------------

ALTER TABLE SCORE_SAUDE MODIFY (observacao VARCHAR2(1000 CHAR));

DELETE FROM DOCUMENTO_CONHECIMENTO;

ALTER TABLE DOCUMENTO_CONHECIMENTO DROP COLUMN hash_fonte;

ALTER TABLE DOCUMENTO_CONHECIMENTO ADD (
    doc_id            VARCHAR2(64)   NOT NULL,
    modelo_embedding  VARCHAR2(100)  NOT NULL,
    dimensoes         NUMBER(5)      NOT NULL
);

ALTER TABLE DOCUMENTO_CONHECIMENTO MODIFY (titulo VARCHAR2(200));

ALTER TABLE DOCUMENTO_CONHECIMENTO
    ADD CONSTRAINT uk_documento_conhecimento_doc_id UNIQUE (doc_id);

------------------------------------------------------------
-- HISTORICO DAS RESPOSTAS DA IA
------------------------------------------------------------

CREATE SEQUENCE seq_analise_ia START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE ANALISE_IA (
    id_analise    NUMBER(10)           DEFAULT seq_analise_ia.NEXTVAL NOT NULL,
    id_pet        NUMBER(10)           NOT NULL,
    id_score      NUMBER(10),
    id_alerta     NUMBER(10),
    tipo_analise  VARCHAR2(30)         NOT NULL,
    pergunta      VARCHAR2(1000 CHAR),
    resposta      CLOB                 NOT NULL,
    modelo        VARCHAR2(100),
    fontes        VARCHAR2(1000 CHAR),
    data_analise  TIMESTAMP            DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_analise_ia PRIMARY KEY (id_analise),
    CONSTRAINT fk_analise_ia_pet FOREIGN KEY (id_pet) REFERENCES PET (id_pet),
    CONSTRAINT fk_analise_ia_score FOREIGN KEY (id_score) REFERENCES SCORE_SAUDE (id_score),
    CONSTRAINT fk_analise_ia_alerta FOREIGN KEY (id_alerta) REFERENCES ALERTA_SAUDE (id_alerta),
    CONSTRAINT ck_analise_ia_tipo CHECK (tipo_analise IN ('ANALISE_SAUDE', 'PERGUNTA', 'EXPLICACAO_ALERTA'))
);

CREATE INDEX idx_analise_ia_pet ON ANALISE_IA (id_pet);

------------------------------------------------------------
-- TESTE: confirmar colunas novas, tabela, sequence e indice
------------------------------------------------------------
SELECT column_name, data_type, data_length, nullable
  FROM user_tab_columns
 WHERE table_name = 'DOCUMENTO_CONHECIMENTO'
 ORDER BY column_id;
SELECT table_name FROM user_tables WHERE table_name = 'ANALISE_IA';
SELECT sequence_name FROM user_sequences WHERE sequence_name = 'SEQ_ANALISE_IA';
SELECT index_name FROM user_indexes WHERE index_name = 'IDX_ANALISE_IA_PET';
