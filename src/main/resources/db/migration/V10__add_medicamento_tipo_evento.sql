------------------------------------------------------------
-- O enum TipoEventoPreventivo (Java) inclui MEDICAMENTO desde sempre,
-- mas as constraints de CHECK criadas na V1 nunca foram atualizadas -
-- um INSERT com tipo_evento = 'MEDICAMENTO' quebrava com ORA-02290
-- tanto em EVENTO_PREVENTIVO quanto em PROTOCOLO_PREVENTIVO.
------------------------------------------------------------

ALTER TABLE EVENTO_PREVENTIVO DROP CONSTRAINT ck_evento_tipo;

ALTER TABLE EVENTO_PREVENTIVO ADD CONSTRAINT ck_evento_tipo CHECK (
    tipo_evento IN ('VACINA', 'CHECKUP', 'VERMIFUGO', 'RETORNO', 'MEDICAMENTO')
);

ALTER TABLE PROTOCOLO_PREVENTIVO DROP CONSTRAINT ck_protocolo_tipo;

ALTER TABLE PROTOCOLO_PREVENTIVO ADD CONSTRAINT ck_protocolo_tipo CHECK (
    tipo_evento IN ('VACINA', 'CHECKUP', 'VERMIFUGO', 'RETORNO', 'MEDICAMENTO')
);

------------------------------------------------------------
-- TESTE: confirmar que as constraints foram recriadas
------------------------------------------------------------
SELECT constraint_name, search_condition
FROM user_constraints
WHERE constraint_name IN ('CK_EVENTO_TIPO', 'CK_PROTOCOLO_TIPO');
