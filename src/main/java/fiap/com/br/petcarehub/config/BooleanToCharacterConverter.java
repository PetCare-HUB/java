package fiap.com.br.petcarehub.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanToCharacterConverter
        implements AttributeConverter<Boolean, Character> {

    @Override
    public Character convertToDatabaseColumn(Boolean value) {
        return Boolean.TRUE.equals(value) ? 'S' : 'N';
    }

    @Override
    public Boolean convertToEntityAttribute(Character value) {
        return value != null && (value == 'S' || value == 's');
    }
}