package com.example.syu_ctn_be.chat.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.syu_ctn_be.controller.ChatController;
import com.example.syu_ctn_be.controller.GlobalExceptionHandler;
import com.example.syu_ctn_be.service.ChatService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    private static final String LOGIN_ID = "alice";

    @Mock private ChatService chatService;
    @InjectMocks private ChatController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        UserDetails principal = User.withUsername(LOGIN_ID).password("x").roles("USER").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, "x", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class Start {

        @Test
        void returnsNewSessionId() throws Exception {
            when(chatService.startSession(LOGIN_ID)).thenReturn(42L);

            mockMvc.perform(post("/api/v1/chat/sessions/start"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sessionId").value(42));

            verify(chatService, times(1)).startSession(LOGIN_ID);
        }

        @Test
        void returns401WithoutPrincipal() throws Exception {
            SecurityContextHolder.clearContext();

            mockMvc.perform(post("/api/v1/chat/sessions/start"))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                    .andExpect(jsonPath("$.status").value(401));

            verify(chatService, never()).startSession(org.mockito.ArgumentMatchers.anyString());
        }
    }

    @Nested
    class Ask {

        @Test
        void returnsAnswerAndSessionId() throws Exception {
            when(chatService.ask(LOGIN_ID, 7L, "question")).thenReturn("answer");

            mockMvc.perform(post("/api/v1/chat/sessions/ask")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"sessionId\":7,\"question\":\"question\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sessionId").value(7))
                    .andExpect(jsonPath("$.answer").value("answer"));

            verify(chatService, times(1)).ask(LOGIN_ID, 7L, "question");
        }
    }

    @Nested
    class Discard {

        @Test
        void returns204AfterDiscard() throws Exception {
            mockMvc.perform(delete("/api/v1/chat/sessions/{id}", 99L))
                    .andExpect(status().isNoContent());

            verify(chatService, times(1)).discardSession(LOGIN_ID, 99L);
        }

        @Test
        void returns401WithoutPrincipal() throws Exception {
            SecurityContextHolder.clearContext();

            mockMvc.perform(delete("/api/v1/chat/sessions/{id}", 99L))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                    .andExpect(jsonPath("$.status").value(401));

            verify(chatService, never()).discardSession(eq(LOGIN_ID), org.mockito.ArgumentMatchers.anyLong());
        }
    }
}
