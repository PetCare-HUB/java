package fiap.com.br.petcarehub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class MaxAgeValidator implements ConstraintValidator<MaxAge, LocalDate> {

    private int maxAge;

    @Override
    public void initialize(MaxAge constraintAnnotation) {
        this.maxAge = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        LocalDate now = LocalDate.now();
        if (value.isAfter(now)) {
            return false;
        }
        int age = Period.between(value, now).getYears();
        return age <= maxAge;
    }
}

