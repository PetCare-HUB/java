SET SERVEROUTPUT ON;

------------------------------------------------------------
-- PETCARE HUB - CREATE INDEXES
-- Índices auxiliares para melhorar buscas por FK e filtros
------------------------------------------------------------

------------------------------------------------------------
-- LIMPEZA OPCIONAL
-- Remove os índices caso já existam
------------------------------------------------------------

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_pet_responsavel';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_pet_clinica';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_consulta_pet';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_consulta_clinica';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_evento_pet';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_evento_protocolo';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_dispositivo_pet';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_leitura_pet';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_leitura_dispositivo';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_alerta_pet';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_alerta_leitura';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_score_pet';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_alerta_resolvido';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_evento_status';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP INDEX idx_score_categoria';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

------------------------------------------------------------
-- ÍNDICES DE RELACIONAMENTO
------------------------------------------------------------

CREATE INDEX idx_pet_responsavel
ON PET (id_responsavel);

CREATE INDEX idx_pet_clinica
ON PET (id_clinica);

CREATE INDEX idx_consulta_pet
ON CONSULTA (id_pet);

CREATE INDEX idx_consulta_clinica
ON CONSULTA (id_clinica);

CREATE INDEX idx_evento_pet
ON EVENTO_PREVENTIVO (id_pet);

CREATE INDEX idx_evento_protocolo
ON EVENTO_PREVENTIVO (id_protocolo);

CREATE INDEX idx_dispositivo_pet
ON DISPOSITIVO_IOT (id_pet);

CREATE INDEX idx_leitura_pet
ON LEITURA_SENSOR (id_pet);

CREATE INDEX idx_leitura_dispositivo
ON LEITURA_SENSOR (id_dispositivo);

CREATE INDEX idx_alerta_pet
ON ALERTA_SAUDE (id_pet);

CREATE INDEX idx_alerta_leitura
ON ALERTA_SAUDE (id_leitura);

CREATE INDEX idx_score_pet
ON SCORE_SAUDE (id_pet);

------------------------------------------------------------
-- ÍNDICES PARA FILTROS FREQUENTES
------------------------------------------------------------

CREATE INDEX idx_alerta_resolvido
ON ALERTA_SAUDE (resolvido);

CREATE INDEX idx_evento_status
ON EVENTO_PREVENTIVO (status);

CREATE INDEX idx_score_categoria
ON SCORE_SAUDE (categoria);

------------------------------------------------------------
-- TESTE FINAL: LISTAR ÍNDICES CRIADOS
------------------------------------------------------------

SELECT index_name, table_name
FROM user_indexes
WHERE index_name IN (
    'IDX_PET_RESPONSAVEL',
    'IDX_PET_CLINICA',
    'IDX_CONSULTA_PET',
    'IDX_CONSULTA_CLINICA',
    'IDX_EVENTO_PET',
    'IDX_EVENTO_PROTOCOLO',
    'IDX_DISPOSITIVO_PET',
    'IDX_LEITURA_PET',
    'IDX_LEITURA_DISPOSITIVO',
    'IDX_ALERTA_PET',
    'IDX_ALERTA_LEITURA',
    'IDX_SCORE_PET',
    'IDX_ALERTA_RESOLVIDO',
    'IDX_EVENTO_STATUS',
    'IDX_SCORE_CATEGORIA'
)
ORDER BY table_name, index_name;