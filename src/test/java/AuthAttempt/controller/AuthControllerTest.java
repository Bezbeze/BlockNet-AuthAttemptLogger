package AuthAttempt.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import AuthAttempt.dto.AuthResponse;
import AuthAttempt.dto.GeneralErrorResponse;
import AuthAttempt.exception.RuntimeTimeoutException;
import AuthAttempt.service.KafkaService;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@WebMvcTest(controllers = AuthController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AuthControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	KafkaService kafkaService;
	
	@Autowired
	ObjectMapper mapper;
	
	String cleanip = "171.186.146.129";
	String blockedIp = "156.104.188.87";
	AuthResponse authResponse;
	String url = "/check-ip/{ip}";
	RequestBuilder request;

	@BeforeEach
	void setUp() throws Exception {
		authResponse = new AuthResponse(UUID.randomUUID().toString(), cleanip, false);
		request = MockMvcRequestBuilders.get(url, cleanip)
				.accept(MediaType.APPLICATION_JSON);   //get answer like json
	}

	@Test
	@SneakyThrows
	@DisplayName("Check IP: Get answer about IP block status: ip is not in the block list")
	void checkPositiveIpTest() {
		
		//Arrange
		Mockito.when(kafkaService.waitForResponse(Mockito.anyString())).thenReturn(authResponse);
			
		//Act
		MvcResult result = mockMvc.perform(request)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		String body = result.getResponse().getContentAsString();
		AuthResponse actualResponseData = mapper.readValue(body, AuthResponse.class);
		
		// Assert
		assertEquals(authResponse, actualResponseData);
	}
	
	@Test
	@SneakyThrows
	@DisplayName("Check IP: Get answer about IP block status: ip is IN THE BLOCK LIST")
	void checkNegativeIpTest() {
		authResponse.setBlocked(true);
		authResponse.setCheckIp(blockedIp);
		Mockito.when(kafkaService.waitForResponse(Mockito.anyString())).thenReturn(authResponse);
		
		request = MockMvcRequestBuilders.get(url, blockedIp)
				.accept(MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(request)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		String body = result.getResponse().getContentAsString();
		AuthResponse actualResponseData = mapper.readValue(body, AuthResponse.class);
		
		assertEquals(authResponse, actualResponseData);
	}
	
	
	@Test
	@SneakyThrows
	@DisplayName("Failed to get response in time")
	void timeoutTest() {
		
		Mockito.when(kafkaService.waitForResponse(Mockito.anyString()))
		.thenThrow(new RuntimeTimeoutException("Failed to get response in time"));
		
		MvcResult result = mockMvc.perform(request)
				.andExpect(MockMvcResultMatchers.status().isRequestTimeout())
				.andReturn();
		
		String body = result.getResponse().getContentAsString();
		GeneralErrorResponse actualResponseData = mapper.readValue(body, GeneralErrorResponse.class);
		
		assertEquals("Failed to get response in time", actualResponseData.getMessage());		
	}
	
	@Test
	@SneakyThrows
	@DisplayName("any unexpected exception")
	void exceptionTest() {
		
		Mockito.when(kafkaService.waitForResponse(Mockito.anyString()))
				.thenThrow(new RuntimeException("something was wrong"));

		MvcResult result = mockMvc.perform(request)
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andReturn();
		
		String body = result.getResponse().getContentAsString();
		GeneralErrorResponse actualResponseData = mapper.readValue(body, GeneralErrorResponse.class);
		
		assertEquals("something was wrong", actualResponseData.getMessage());			
		
	}

}
