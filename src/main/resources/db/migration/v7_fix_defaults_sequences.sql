SET SERVEROUTPUT ON;

ALTER TABLE TUTOR                MODIFY id_tutor        DEFAULT seq_tutor.NEXTVAL;
ALTER TABLE CLINICA              MODIFY id_clinica      DEFAULT seq_clinica.NEXTVAL;
ALTER TABLE PET                  MODIFY id_pet          DEFAULT seq_pet.NEXTVAL;
ALTER TABLE CONSULTA             MODIFY id_consulta     DEFAULT seq_consulta.NEXTVAL;
ALTER TABLE PROTOCOLO_PREVENTIVO MODIFY id_protocolo    DEFAULT seq_protocolo_preventivo.NEXTVAL;
ALTER TABLE EVENTO_PREVENTIVO    MODIFY id_evento       DEFAULT seq_evento_preventivo.NEXTVAL;
ALTER TABLE DISPOSITIVO_IOT      MODIFY id_dispositivo  DEFAULT seq_dispositivo_iot.NEXTVAL;
ALTER TABLE LEITURA_SENSOR       MODIFY id_leitura      DEFAULT seq_leitura_sensor.NEXTVAL;
ALTER TABLE ALERTA_SAUDE         MODIFY id_alerta       DEFAULT seq_alerta_saude.NEXTVAL;
ALTER TABLE SCORE_SAUDE          MODIFY id_score        DEFAULT seq_score_saude.NEXTVAL;
ALTER TABLE LOG_ERROS            MODIFY id_log          DEFAULT seq_log_erros.NEXTVAL;

SELECT table_name, column_name, data_default
FROM user_tab_columns
WHERE column_name LIKE 'ID\_%' ESCAPE '\'
  AND table_name IN (
    'TUTOR', 'CLINICA', 'PET', 'CONSULTA',
    'PROTOCOLO_PREVENTIVO', 'EVENTO_PREVENTIVO',
    'DISPOSITIVO_IOT', 'LEITURA_SENSOR',
    'ALERTA_SAUDE', 'SCORE_SAUDE', 'LOG_ERROS'
  )
ORDER BY table_name;
