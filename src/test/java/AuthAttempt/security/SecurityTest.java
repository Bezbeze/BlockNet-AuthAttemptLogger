package AuthAttempt.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import AuthAttempt.repo.ClientsRepo;
import AuthAttempt.service.KafkaService;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;


@WebMvcTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class SecurityTest {
	
	@MockBean
	ClientsRepo clientsRepo;
	
	@MockBean
	KafkaService kafkaService;
	
	@Autowired
	MockMvc mockMvc;

	String url="/check-ip/255.231.123.12";

	@Test
	@SneakyThrows
	@DisplayName("Client doesn't exists in db")
	void clientDoesnotHaveAccessTest() {
		String clientIp = "123.123.123.33";
		
		Mockito.when(clientsRepo.existsById(eq(clientIp))).thenReturn(false);	
		
	      var request = MockMvcRequestBuilders.get(url)
	                .accept(MediaType.APPLICATION_JSON)
	                .remoteAddress(clientIp);
		MvcResult result = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isUnauthorized()).andReturn();
		
		assertTrue(result.getResponse().getContentAsString().isBlank());
		assertEquals("Unauthorized IP" ,result.getResponse().getErrorMessage());

	}
	
	@Test
	@SneakyThrows
	@DisplayName("Client exists in db")
	void clientHasAccessTest() {
		String clientIp = "123.123.123.34";
		
		Mockito.when(clientsRepo.existsById(eq(clientIp))).thenReturn(true);	
		
	      var request = MockMvcRequestBuilders.get(url)
	                .accept(MediaType.APPLICATION_JSON)
	                .remoteAddress(clientIp);
		mockMvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();

	}

}
