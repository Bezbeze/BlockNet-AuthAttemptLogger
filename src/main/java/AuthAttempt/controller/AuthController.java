package AuthAttempt.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import AuthAttempt.dto.AuthResponse;
import AuthAttempt.service.KafkaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {
	
	KafkaService kafkaService;
	
	//Everything is done in a single connection(all microservices);
	//RuntimeTimeoutException("Failed to get response in time")
	@GetMapping("/check-ip/{ip}")
	public AuthResponse putMethodName(@PathVariable String ip, HttpServletRequest request) {
		String uuid= UUID.randomUUID().toString(); //unique identifier for each client response 
		kafkaService.sendRequest(uuid, ip, request);
		return kafkaService.waitForResponse(uuid);  
	}

}
