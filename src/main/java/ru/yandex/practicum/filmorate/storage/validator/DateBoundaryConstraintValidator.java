package ru.yandex.practicum.filmorate.storage.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateBoundaryConstraintValidator implements ConstraintValidator<DateBoundary, LocalDate> {
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");

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
        return target.isAfter(annotationDate);
    }

}
