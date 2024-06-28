package AuthAttempt.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import AuthAttempt.dto.AuthRequest;
import AuthAttempt.dto.AuthResponse;
import AuthAttempt.exception.RuntimeTimeoutException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaService implements IService {
	
	@Getter
	 private static ConcurrentMap<String, AuthResponse> responseMap = new ConcurrentHashMap<>();

	 StreamBridge streamBridge;
	 int timeout;

	public KafkaService(@Autowired StreamBridge streamBridge, @Value("${custom.timeout:30000}") int timeout) {
		this.streamBridge = streamBridge;
		this.timeout = timeout;
	}

	private void sendSensorData(AuthRequest authRequest) {
		streamBridge.send("sensor-data-out", authRequest);
	}

	@Override
	public AuthResponse waitForResponse(String requestId) {
		long curentTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - curentTime < timeout) {
			AuthResponse response = responseMap.get(requestId);
			if(response != null) {
				responseMap.remove(requestId);
				return response;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				 throw new RuntimeException("Thread was interrupted", e);
			}
		}
		throw new RuntimeTimeoutException("Failed to get response in time");
	}

	@Override
	public void sendRequest(String requestId, String ip, HttpServletRequest request) {
		sendSensorData(new AuthRequest(requestId, ip, request.getRemoteAddr()));	
	}


}
