package com.audition.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostWithComments;
import com.audition.model.PostComment;
import com.audition.service.AuditionService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AuditionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditionService auditionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "lisa")
    void testGetAllPostsWithFilter() throws Exception {
        AuditionPost post1 = new AuditionPost(1, 1, "Title1", "Content1");
        AuditionPost post2 = new AuditionPost(2, 2, "Title2", "Content2");

        when(auditionService.getPosts("Title1")).thenReturn(List.of(post1));

        mockMvc.perform(get("/posts")
                .param("title", "Title1")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Title1"))
            .andDo(MockMvcResultHandlers.print());

        verify(auditionService, times(1)).getPosts("Title1");
    }

    @Test
    void testGetAllPostsWithFilter_withoutAuthentication() throws Exception {
        AuditionPost post1 = new AuditionPost(1, 1, "Title1", "Content1");
        AuditionPost post2 = new AuditionPost(2, 2, "Title2", "Content2");

        when(auditionService.getPosts("Title1")).thenReturn(List.of(post1));

        mockMvc.perform(get("/posts")
                .param("title", "Title1")
                .contentType("application/json"))
            .andExpect(status().isUnauthorized())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(username = "lisa")
    void testGetPostById() throws Exception {
        AuditionPost post = new AuditionPost(1, 1, "Title1", "Content1");

        when(auditionService.getPostById("1")).thenReturn(post);
        // when(auditionService.validPostId("1")).thenReturn(true);

        mockMvc.perform(get("/posts/{id}", "1")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Title1"))
            .andDo(MockMvcResultHandlers.print());

        verify(auditionService, times(1)).getPostById("1");
        verify(auditionService, times(1)).validPostId("1");
    }

    @Test
    @WithMockUser(username = "lisa")
    void testGetPostsWithComments() throws Exception {
        AuditionPostWithComments post = new AuditionPostWithComments(1, 1, "Title1", "Content1",
            List.of(new PostComment(1, 1, "name", "email", "body")));

        when(auditionService.getPostsByIdWithComments("1")).thenReturn(post);
        // when(auditionService.validPostId("1")).thenReturn(true);

        mockMvc.perform(get("/posts/{id}/comments", "1")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Title1"))
            .andDo(MockMvcResultHandlers.print());

        verify(auditionService, times(1)).getPostsByIdWithComments("1");
        verify(auditionService, times(1)).validPostId("1");
    }

    @Test
    @WithMockUser(username = "lisa")
    void testGetCommentsForPost() throws Exception {
        PostComment comment1 = new PostComment(1, 1, "name1", "email1", "Comment1");
        PostComment comment2 = new PostComment(2, 2, "name2", "email2", "Comment2");
        List<PostComment> comments = Arrays.asList(comment1, comment2);

        when(auditionService.getCommentsByPostId("1")).thenReturn(comments);
        // when(auditionService.validPostId("1")).thenReturn(true);

        mockMvc.perform(get("/comments")
                .param("postId", "1")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].body").value("Comment1"))
            .andDo(MockMvcResultHandlers.print());

        verify(auditionService, times(1)).getCommentsByPostId("1");
        verify(auditionService, times(1)).validPostId("1");
    }

    @Test
    @WithMockUser(username = "lisa")
    void testGetAllPostsWithInvalidFilter() throws Exception {
        // Simulate a failure in the service
        when(auditionService.getPosts(anyString())).thenThrow(new SystemException("Invalid title",
            HttpStatus.BAD_REQUEST));

        mockMvc.perform(get("/posts")
                .param("title", "InvalidTitle")
                .contentType("application/json"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Invalid title"))
            .andDo(MockMvcResultHandlers.print());

        verify(auditionService, times(1)).getPosts("InvalidTitle");
    }

    @Test
    @WithMockUser(username = "lisa")
    void testGetPostById_withInvalidId() throws Exception {
        // Simulate the service throwing an exception due to invalid post ID
        doThrow(new SystemException("Invalid post ID", HttpStatus.BAD_REQUEST)).when(auditionService)
            .validPostId("invalidId");

        mockMvc.perform(get("/posts/{id}", "invalidId")
                .contentType("application/json"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Invalid post ID"))
            .andDo(MockMvcResultHandlers.print());

        verify(auditionService, times(1)).validPostId("invalidId");
        verify(auditionService, never()).getPostById("invalidId");
    }

    @Test
    @WithMockUser(username = "lisa")
    void testGetPostById_whenPostNotFound() throws Exception {
        // Simulate the service throwing an exception when the post is not found
        when(auditionService.getPostById("1")).thenThrow(
            new SystemException("Post not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/posts/{id}", "1")
                .contentType("application/json"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("Post not found"))
            .andDo(MockMvcResultHandlers.print());

        verify(auditionService, times(1)).getPostById("1");
        verify(auditionService, times(1)).validPostId("1");
    }

    @Test
    @WithMockUser(username = "lisa")
    void testGetPostsWithComments_whenPostNotFound() throws Exception {
        // Simulate the service throwing an exception when the post with comments is not found
        when(auditionService.getPostsByIdWithComments("1")).thenThrow(
            new SystemException("Post with comments not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/posts/{id}/comments", "1")
                .contentType("application/json"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("Post with comments not found"))
            .andDo(MockMvcResultHandlers.print());

        verify(auditionService, times(1)).getPostsByIdWithComments("1");
        verify(auditionService, times(1)).validPostId("1");
    }

    @Test
    @WithMockUser(username = "lisa")
    void testGetCommentsForPost_whenPostIdInvalid() throws Exception {
        // Simulate the service throwing an exception due to invalid post ID
        doThrow(new SystemException("Invalid post ID", HttpStatus.BAD_REQUEST)).when(auditionService)
            .validPostId("invalidPostId");

        mockMvc.perform(get("/comments")
                .param("postId", "invalidPostId")
                .contentType("application/json"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Invalid post ID"))
            .andDo(MockMvcResultHandlers.print());

        verify(auditionService, times(1)).validPostId("invalidPostId");
        verify(auditionService, never()).getCommentsByPostId("invalidPostId");
    }
}
