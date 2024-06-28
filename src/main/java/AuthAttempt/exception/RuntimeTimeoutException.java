package AuthAttempt.exception;

@SuppressWarnings("serial")
public class RuntimeTimeoutException extends RuntimeException {

	public RuntimeTimeoutException(String message) {
		super(message);
	}
}
