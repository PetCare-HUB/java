package fiap.com.br.petcarehub.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;


@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {MaxAgeValidator.class})
@Documented
public @interface MaxAge {
    String message() default "Idade maior que o máximo permitido ({value} anos)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int value() default 20;
}


