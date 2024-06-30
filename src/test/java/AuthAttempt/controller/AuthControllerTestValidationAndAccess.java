package AuthAttempt.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import AuthAttempt.dto.AuthResponse;
import AuthAttempt.service.KafkaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@WebMvcTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class AuthControllerTestValidationAndAccess {

    @MockBean
    KafkaService kafkaService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    String url = "/check-ip/";

    static Stream<String> validIpsProvider() {
        return Stream.of("20.167.212.166", "146.41.203.136", "197.95.253.234", "47.152.17.155");
    }

    static Stream<String> invalidIpsProvider() {
        return Stream.of("256.167.212.166", "146.203.136", "197.95.-253.234", null);
    }

    @ParameterizedTest
    @MethodSource("validIpsProvider")
    @DisplayName("Check valid parameter {ip}")
    @SneakyThrows
    void testValidation(String ip) {
        String uuid = Integer.toString(ip.hashCode());
        AuthResponse authResponse = new AuthResponse(uuid, url, false);
        Mockito.when(kafkaService.sendRequest(eq(ip), Mockito.any(HttpServletRequest.class))).thenReturn(uuid);
        Mockito.when(kafkaService.waitForResponse(uuid)).thenReturn(authResponse);

        var request = MockMvcRequestBuilders.get(url + ip).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String body = result.getResponse().getContentAsString();
        AuthResponse actualResponseData = mapper.readValue(body, AuthResponse.class);
        assertEquals(actualResponseData, authResponse);
    }

    @ParameterizedTest
    @MethodSource("invalidIpsProvider")
    @DisplayName("Check INVALID parameter in URL {ip}")
    @SneakyThrows
    void testValidationInvalidParam(String ip) {
        var request = MockMvcRequestBuilders.get(url + ip).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("message\":\"400 BAD_REQUEST "));
    }
}
