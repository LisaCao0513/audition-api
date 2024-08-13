package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostWithComments;
import com.audition.model.PostComment;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class AuditionServiceTest {

    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private AuditionService auditionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPosts() {
        List<AuditionPost> expectedPosts = Arrays.asList(new AuditionPost(), new AuditionPost());
        when(auditionIntegrationClient.getPosts("title")).thenReturn(expectedPosts);

        List<AuditionPost> posts = auditionService.getPosts("title");
        assertEquals(expectedPosts, posts);
        verify(auditionIntegrationClient).getPosts("title");
    }

    @Test
    void testGetPostById() {
        AuditionPost expectedPost = new AuditionPost();
        when(auditionIntegrationClient.getPostById("1")).thenReturn(expectedPost);

        AuditionPost post = auditionService.getPostById("1");
        assertEquals(expectedPost, post);
        verify(auditionIntegrationClient).getPostById("1");
    }

    @Test
    void testGetPostsByIdWithComments() {
        AuditionPostWithComments expectedPostWithComments = new AuditionPostWithComments(1, 1, "Title1", "Content1",
            List.of(new PostComment(1, 1, "name", "email", "body")));
        when(auditionIntegrationClient.getPostsByIdWithComments("1")).thenReturn(expectedPostWithComments);

        AuditionPostWithComments postWithComments = auditionService.getPostsByIdWithComments("1");
        assertEquals(expectedPostWithComments, postWithComments);
        verify(auditionIntegrationClient).getPostsByIdWithComments("1");
    }

    @Test
    void testGetCommentsByPostId() {
        List<PostComment> expectedComments = Arrays.asList(new PostComment(), new PostComment());
        when(auditionIntegrationClient.getCommentsByPostId("1")).thenReturn(expectedComments);

        List<PostComment> comments = auditionService.getCommentsByPostId("1");
        assertEquals(expectedComments, comments);
        verify(auditionIntegrationClient).getCommentsByPostId("1");
    }

    @Test
    void testValidPostIdValidId() {
        // No exception should be thrown
        assertDoesNotThrow(() -> auditionService.validPostId("123"));
    }

    @Test
    void testValidPostIdNullId() {
        SystemException thrown = assertThrows(SystemException.class, () -> auditionService.validPostId(null));
        assertEquals("postId is required", thrown.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }

    @Test
    void testValidPostIdEmptyId() {
        SystemException thrown = assertThrows(SystemException.class, () -> auditionService.validPostId(" "));
        assertEquals("postId is required", thrown.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }

    @Test
    void testValidPostIdInvalidInteger() {
        SystemException thrown = assertThrows(SystemException.class, () -> auditionService.validPostId("abc"));
        assertEquals("postId must be a valid integer", thrown.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }
}