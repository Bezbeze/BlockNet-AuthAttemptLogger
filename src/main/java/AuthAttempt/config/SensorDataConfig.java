package AuthAttempt.config;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import AuthAttempt.dto.AuthResponse;
import AuthAttempt.exception.RuntimeJsonProcessingException;
import AuthAttempt.service.KafkaService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensorDataConfig {
	ObjectMapper mapper = new ObjectMapper();
	
	@Bean
	Consumer<String> receiveSensorData(){
		return sensorData -> {
			try {
				AuthResponse authResponse = mapper.readValue(sensorData, AuthResponse.class);
				KafkaService.getResponseMap().put(authResponse.getRequestId(), authResponse);
			} catch (JsonProcessingException e) {
				throw new RuntimeJsonProcessingException("can't read value authResponse", e);
			}			
		};
	}

}
