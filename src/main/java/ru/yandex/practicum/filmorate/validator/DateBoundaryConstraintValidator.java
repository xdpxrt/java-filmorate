package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateBoundaryConstraintValidator implements ConstraintValidator<DateBoundary, LocalDate> {
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate annotationDate;

    @Override
    public void initialize(DateBoundary dateBoundary) {
        this.annotationDate = LocalDate.parse(dateBoundary.value(), dateTimeFormatter);
    }

    @Override
    public boolean isValid(LocalDate target, ConstraintValidatorContext cxt) {
        if (target == null) {
            return false;
        }
        if (target.isAfter(annotationDate)) {
            return true;
        } else throw new ValidationException("Дата выпуска должна быть позже 1895-12-28");
    }
}
