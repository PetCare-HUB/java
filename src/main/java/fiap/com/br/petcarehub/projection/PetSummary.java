package fiap.com.br.petcarehub.projection;

import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;

import java.math.BigDecimal;

public interface PetSummary {

    Long getId();

    String getNome();

    EspeciePet getEspecie();

    String getRaca();

    BigDecimal getPesoKg();

    SexoPet getSexo();

    Character getAtivo();

}