------------------------------------------------------------
-- OBSERVACAO DO SCORE DE SAUDE
--
-- O ScoreSaudeService ja montava, a cada calculo, um texto
-- explicando cada penalidade aplicada (sedentarismo, bateria,
-- racao, ambiente, consulta, alertas), mas esse texto era
-- descartado na hora de gravar. Sem ele, o tutor via a nota
-- cair e nao sabia o porque.
--
-- A coluna guarda esse texto junto do score que ele explica:
--
--   - A observacao e gerada por REGRA DETERMINISTICA em Java
--     (cada if de penalidade acrescenta a sua frase). Nao e
--     texto de IA: o mesmo conjunto de leituras sempre produz
--     a mesma explicacao.
--
--   - Nullable: scores calculados antes desta migration nao
--     tem observacao e nao ha como reconstitui-la depois.
--
--   - VARCHAR2(1000): o service trunca em 1000 caracteres
--     antes de gravar, entao o texto nunca estoura a coluna.
------------------------------------------------------------

ALTER TABLE SCORE_SAUDE ADD (observacao VARCHAR2(1000));

------------------------------------------------------------
-- TESTE: confirmar coluna criada e nullable
------------------------------------------------------------
SELECT column_name, data_type, data_length, nullable
  FROM user_tab_columns
 WHERE table_name = 'SCORE_SAUDE' AND column_name = 'OBSERVACAO';
