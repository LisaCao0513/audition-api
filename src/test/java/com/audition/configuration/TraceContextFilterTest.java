package com.audition.configuration;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class TraceContextFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResponseHeaderInjector responseHeaderInjector;

    @Test
    @WithMockUser(username = "lisa")
    public void testTraceAndSpanIdHeaders() throws Exception {
        Mockito.doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(0);
            response.setHeader("X-Trace-Id", "test-trace-id");
            response.setHeader("X-Span-Id", "test-span-id");
            return null;
        }).when(responseHeaderInjector).injectTraceContext(Mockito.any(HttpServletResponse.class));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts"))
            .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
            .andExpect(MockMvcResultMatchers.header().string("X-Trace-Id", "test-trace-id"))
            .andExpect(MockMvcResultMatchers.header().string("X-Span-Id", "test-span-id"));
    }
}