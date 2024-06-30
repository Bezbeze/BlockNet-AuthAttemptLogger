package AuthAttempt.service;

import AuthAttempt.dto.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IService {
	String sendRequest(String ip, HttpServletRequest request);
	AuthResponse waitForResponse(String requestId);
	

}
