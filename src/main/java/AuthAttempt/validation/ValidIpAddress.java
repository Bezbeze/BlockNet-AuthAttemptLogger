package AuthAttempt.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import AuthAttempt.validation.processor.IpAddressValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = IpAddressValidator.class)
public @interface ValidIpAddress {

	String message() default "Invalid IP address";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
