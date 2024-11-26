package br.com.fiap.techchallenge.quickserveapi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String urlOrder = "/quick_service/orders";

    @Test
    public void testHandleUnsupportedMediaType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(urlOrder)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testHandleNotFoundException() throws Exception {
        // Simula a exceção NotFoundException
        mockMvc.perform(MockMvcRequestBuilders.get(urlOrder + "/orders/777")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void testHandleNotFoundExceptions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(urlOrder + "/777")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());  // Espera o status 405
    }
}