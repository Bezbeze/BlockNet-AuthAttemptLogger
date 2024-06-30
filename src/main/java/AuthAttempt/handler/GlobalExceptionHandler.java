package AuthAttempt.handler;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import AuthAttempt.dto.GeneralErrorResponse;
import AuthAttempt.exception.RuntimeTimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = { RuntimeTimeoutException.class })
	@ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT) // 408
	GeneralErrorResponse runtimeTimeoutExceptionHandler(RuntimeTimeoutException e) {
		return new GeneralErrorResponse(UUID.randomUUID().toString(), e.getMessage());

	}

	@ExceptionHandler(value = { Exception.class })
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR) // 500
	GeneralErrorResponse exceptionHandler(Exception e) {
		return new GeneralErrorResponse(UUID.randomUUID().toString(), e.getMessage());
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	public GeneralErrorResponse handlerMethodValidationException(HandlerMethodValidationException ex) {
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

}
