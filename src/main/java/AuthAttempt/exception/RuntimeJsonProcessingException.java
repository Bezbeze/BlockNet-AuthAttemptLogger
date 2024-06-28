package AuthAttempt.exception;

import lombok.Getter;

@SuppressWarnings("serial")

public class RuntimeJsonProcessingException extends RuntimeException {
	
	@Getter
	private Exception e;

	public RuntimeJsonProcessingException(String message, Exception e) {
		super(message);
		this.e = e;
	}
}
