package com.ldavidsantiago.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldavidsantiago.common.BookingRequest;
import com.ldavidsantiago.common.BookingResponse;
import com.ldavidsantiago.entity.BookOrder;
import com.ldavidsantiago.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Reserva exitosa")
    void reservaExitos() throws Exception {
        BookOrder bookOrder = new BookOrder(1, "Prueba", 2, 100.0);
        BookingRequest request = new BookingRequest(bookOrder);
        BookingResponse response = new BookingResponse(200.0, "Prueba", bookOrder, 1, 200.0, "TX123", "Payment processed Successful");
        when(restTemplate.getForObject(anyString(), any())).thenReturn("{\"nombreProducto\":\"Prueba\",\"precio\":100}");
        when(restTemplate.getForObject(Mockito.contains("validateToken"), any())).thenReturn("OK");
        when(bookingService.bookOder(any(BookingRequest.class), anyString())).thenReturn(response);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/book/bookOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer tokenvalido")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookname").value("Prueba"))
                .andExpect(jsonPath("$.response").value("Payment processed Successful"));
    }

    @Test
    @DisplayName("Producto no encontrado")
    void productoNoEncontrado() throws Exception {
        when(restTemplate.getForObject(anyString(), any())).thenReturn("error");
        String jsonRequest = "{\"bookOrder\":{\"id\":99,\"name\":\"Inexistente\",\"quantity\":1,\"price\":100.0}}";
        mockMvc.perform(post("/book/bookOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer tokenvalido")
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Token ausente o inválido")
    void tokenAusenteOInvalido() throws Exception {
        BookOrder bookOrder = new BookOrder(1, "prueba", 1, 100.0);
        BookingRequest request = new BookingRequest(bookOrder);
        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/book/bookOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Token expirado o inválido (respuesta de auth)")
    void tokenExpiradoOInvalido() throws Exception {
        when(restTemplate.getForObject(anyString(), any())).thenReturn("{\"nombreProducto\":\"prueba\",\"precio\":100}");
        when(restTemplate.getForObject(Mockito.contains("validateToken"), any())).thenThrow(new RuntimeException("Token vencido o invalido"));
        BookOrder bookOrder = new BookOrder(1, "Libro", 1, 100.0);
        BookingRequest request = new BookingRequest(bookOrder);
        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/book/bookOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer tokeninvalido")
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Fallo en el pago")
    void falloEnElPago() throws Exception {
        BookOrder bookOrder = new BookOrder(2, "Fallido", 1, 100.0);
        BookingRequest request = new BookingRequest(bookOrder);
        BookingResponse response = new BookingResponse(100.0, "Fallido", bookOrder, 1, 100.0, "TX999", "Payment Failure");
        when(restTemplate.getForObject(anyString(), any())).thenReturn("{\"nombreProducto\":\"Fallido\",\"precio\":100}");
        when(restTemplate.getForObject(Mockito.contains("validateToken"), any())).thenReturn("OK");
        when(bookingService.bookOder(any(BookingRequest.class), anyString())).thenReturn(response);
        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/book/bookOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer tokenvalido")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Payment Failure"));
    }

    @Test
    @DisplayName("Validación de cálculo de precio")
    void validacionCalculoPrecio() throws Exception {
        BookOrder bookOrder = new BookOrder(3, "Precio", 3, 50.0);
        BookingRequest request = new BookingRequest(bookOrder);
        BookingResponse response = new BookingResponse(150.0, "Precio", bookOrder, 1, 150.0, "TX321", "Payment processed Successful");
        when(restTemplate.getForObject(anyString(), any())).thenReturn("{\"nombreProducto\":\"Precio\",\"precio\":50}");
        when(restTemplate.getForObject(Mockito.contains("validateToken"), any())).thenReturn("OK");
        when(bookingService.bookOder(any(BookingRequest.class), anyString())).thenReturn(response);
        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/book/bookOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer tokenvalido")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookPrice").value(150.0));
    }
} 