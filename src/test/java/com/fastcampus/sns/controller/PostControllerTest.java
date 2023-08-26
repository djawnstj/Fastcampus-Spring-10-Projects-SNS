package com.fastcampus.sns.controller;

import com.fastcampus.sns.controller.request.PostCreateRequest;
import com.fastcampus.sns.controller.request.PostModifyRequest;
import com.fastcampus.sns.controller.request.UserJoinRequest;
import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.fixture.PostEntityFixture;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    @WithMockUser
    public void 포스트_작성() throws Exception {
        // given
        String title = "title";
        String body = "body";

        // when
        final ResultActions result = mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body))))
                .andDo(print());

        //then
        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void 포스트_작성_시_로그인_하지_않은_경우() throws Exception {
        // given
        String title = "title";
        String body = "body";

        // when
        final ResultActions result = mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body))))
                .andDo(print());

        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 포스트_수정() throws Exception {
        // given
        String title = "title";
        String body = "body";

        // when
        when(postService.modify(eq(title), eq(body), any(), any()))
                .thenReturn(Post.fromEntity(PostEntityFixture.get("userName", 1, 1)));

        final ResultActions result = mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body))))
                .andDo(print());

        //then
        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void 포스트_수정_시_로그인_하지_않은_경우() throws Exception {
        // given
        String title = "title";
        String body = "body";

        // when
        final ResultActions result = mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body))))
                .andDo(print());

        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 포스트_수정_시_본인이_작성한_글이_아니라면_에러_발생() throws Exception {
        // given
        String title = "title";
        String body = "body";

        // when
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(eq(title), eq(body), any(), eq(1));

        final ResultActions result = mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body))))
                .andDo(print());

        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 포스트_수정_시_수정하려는_글이_없는_경우_에러_발생() throws Exception {
        // given
        String title = "title";
        String body = "body";

        // when
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(eq(title), eq(body), any(), eq(1));

        final ResultActions result = mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body))))
                .andDo(print());

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void 포스트_삭제() throws Exception {
        final ResultActions result = mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void 포스트_삭제_시_로그인_하지_않은_경우() throws Exception {
        final ResultActions result = mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        result.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 포스트_삭제_시_작성자와_삭제_요청자_다른_경우() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), any()) ;
        final ResultActions result = mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        result.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 포스트_삭제_시_삭제하려는_포스트가_존재하지_않을_경우() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(), any()) ;
        final ResultActions result = mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        result.andExpect(status().isNotFound() );
    }

    @Test
    @WithMockUser
    public void 피드_목록() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        final ResultActions result = mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void 피드_목록_요청_시_로그인_하지_않은_경우() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        final ResultActions result = mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        result.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 내_피드_목록() throws Exception {
        when(postService.myList(any(), any())).thenReturn(Page.empty());

        final ResultActions result = mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void 내_피드_목록_요청_시_로그인_하지_않은_경우() throws Exception {
        when(postService.myList(any(), any())).thenReturn(Page.empty());

        final ResultActions result = mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        result.andExpect(status().isUnauthorized());
    }

}
