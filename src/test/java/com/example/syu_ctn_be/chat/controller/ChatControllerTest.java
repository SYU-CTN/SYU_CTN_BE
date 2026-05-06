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

import com.example.syu_ctn_be.chat.service.ChatService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

/**
 * ChatController 슬라이스 테스트. Spring 컨텍스트를 띄우지 않고 standalone MockMvc 로 검증한다.
 *  - 인증 주체는 SecurityContextHolder 에 직접 채워 @AuthenticationPrincipal 주입을 흉내낸다.
 *  - ChatService 는 모킹하여 컨트롤러 ↔ 서비스 위임만 확인한다.
 */
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
                .setHandlerExceptionResolvers(new ResponseStatusExceptionResolver())
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
    @DisplayName("POST /api/v1/chat/sessions/start")
    class Start {

        @Test
        void 새_세션_id_를_JSON_으로_반환한다() throws Exception {
            when(chatService.startSession(LOGIN_ID)).thenReturn(42L);

            mockMvc.perform(post("/api/v1/chat/sessions/start"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sessionId").value(42));

            verify(chatService, times(1)).startSession(LOGIN_ID);
        }

        @Test
        void 인증_주체가_없으면_401() throws Exception {
            SecurityContextHolder.clearContext();

            mockMvc.perform(post("/api/v1/chat/sessions/start"))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

            verify(chatService, never()).startSession(org.mockito.ArgumentMatchers.anyString());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/chat/sessions/ask")
    class Ask {

        @Test
        void 답변과_세션id_를_반환한다() throws Exception {
            when(chatService.ask(LOGIN_ID, 7L, "질문?")).thenReturn("응답입니다");

            mockMvc.perform(post("/api/v1/chat/sessions/ask")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"sessionId\":7,\"question\":\"질문?\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sessionId").value(7))
                    .andExpect(jsonPath("$.answer").value("응답입니다"));

            verify(chatService, times(1)).ask(LOGIN_ID, 7L, "질문?");
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/chat/sessions/{sessionId}")
    class Discard {

        @Test
        void 세션을_폐기하고_204_를_반환한다() throws Exception {
            mockMvc.perform(delete("/api/v1/chat/sessions/{id}", 99L))
                    .andExpect(status().isNoContent());

            verify(chatService, times(1)).discardSession(LOGIN_ID, 99L);
        }

        @Test
        void 인증_주체가_없으면_401_이고_서비스를_호출하지_않는다() throws Exception {
            SecurityContextHolder.clearContext();

            mockMvc.perform(delete("/api/v1/chat/sessions/{id}", 99L))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

            verify(chatService, never()).discardSession(eq(LOGIN_ID), org.mockito.ArgumentMatchers.anyLong());
        }
    }
}
