package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateBoundaryConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateBoundary {

    String value();

    String message() default "{DateBoundary}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}