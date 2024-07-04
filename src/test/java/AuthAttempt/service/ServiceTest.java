package AuthAttempt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.mock.web.MockHttpServletRequest;

import AuthAttempt.dto.AuthRequest;
import AuthAttempt.dto.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
class ServiceTest {

	@MockBean
	private StreamBridge streamBridge;

	@Autowired
	private KafkaService kafkaService;

	@Test
	@DisplayName("Send Request")
	void testSendRequest() {
		// Arrange
		String ip = "192.168.1.1";
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();
		ArgumentCaptor<AuthRequest> argumentCaptor = ArgumentCaptor.forClass(AuthRequest.class);

		// Act
		String requestId = kafkaService.sendRequest(ip, httpServletRequest);

		// Assert
		assertEquals(36, requestId.length());
		verify(streamBridge).send(eq("sensor-data-out"), argumentCaptor.capture());
		AuthRequest capturedRequest = argumentCaptor.getValue();
		assertEquals(requestId, capturedRequest.getRequestId());
		assertEquals(ip, capturedRequest.getCheckIp());
		assertEquals("127.0.0.1", capturedRequest.getClientUrl());
	}
	
    @Test
    @DisplayName("Failed to get response in time")
    void testWaitForResponseTimeout() {  //!!! wait timeout to finish!!!
 
        String requestId = UUID.randomUUID().toString();

        try {
        	kafkaService.waitForResponse(requestId);
        	fail();
		} catch (Exception e) {
			assertEquals("Failed to get response in time", e.getMessage());
		}

    }

	@Test
	@DisplayName("get response with delay")
    void testWaitForResponseSuccess() {   //!!! wait timeout to finish!!!
      
        String requestId = UUID.randomUUID().toString();
        AuthResponse expectedResponse = new AuthResponse(requestId, "125.125.125.55", false);
       
        // Schedule the response to be put in the map after 10 seconds
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            KafkaService.getResponseMap().put(requestId, expectedResponse);
        }, 10, TimeUnit.SECONDS);
        
        AuthResponse actualResponse = kafkaService.waitForResponse(requestId);
          
        assertEquals(expectedResponse, actualResponse);
    }


}
