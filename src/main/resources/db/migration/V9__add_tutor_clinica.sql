SET SERVEROUTPUT ON;

------------------------------------------------------------
-- TUTOR passa a guardar a clinica que fez o pre-cadastro dele.
-- Sem isso, um tutor recem-ativado que ainda nao tem nenhum PET
-- nao tem como saber (nem a API responder) qual e a clinica dele.
-- Nullable porque tutores ja existentes nao tem esse dado
-- retroativo.
------------------------------------------------------------

ALTER TABLE TUTOR ADD (id_clinica NUMBER(10));

ALTER TABLE TUTOR ADD CONSTRAINT fk_tutor_clinica
    FOREIGN KEY (id_clinica) REFERENCES CLINICA (id_clinica);

------------------------------------------------------------
-- TESTE: confirmar que a coluna e a FK foram criadas
------------------------------------------------------------
SELECT column_name FROM user_tab_columns WHERE table_name = 'TUTOR' AND column_name = 'ID_CLINICA';
SELECT constraint_name FROM user_constraints WHERE table_name = 'TUTOR' AND constraint_name = 'FK_TUTOR_CLINICA';
