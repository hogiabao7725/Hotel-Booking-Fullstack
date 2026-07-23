package com.hogiabao7725.hotelbooking.validation.file;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedFileTypesValidator.class)
public @interface AllowedFileTypes {

    String[] value();

    String message() default "Allowed file types: {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
