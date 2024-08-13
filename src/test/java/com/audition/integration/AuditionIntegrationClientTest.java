package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostWithComments;
import com.audition.model.PostComment;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class AuditionIntegrationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPostsWithTitle() {
        // Mock response
        AuditionPost post1 = new AuditionPost();
        post1.setTitle("Title 1");
        AuditionPost post2 = new AuditionPost();
        post2.setTitle("Title 2");
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts", AuditionPost[].class))
            .thenReturn(new AuditionPost[]{post1, post2});

        // Call method
        List<AuditionPost> posts = auditionIntegrationClient.getPosts("Title 1");

        // Verify results
        assertEquals(1, posts.size());
        assertEquals("Title 1", posts.get(0).getTitle());
    }

    @Test
    void testGetPostsWithoutTitle() {
        // Mock response
        AuditionPost post1 = new AuditionPost();
        post1.setTitle("Title 1");
        AuditionPost post2 = new AuditionPost();
        post2.setTitle("Title 2");
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts", AuditionPost[].class))
            .thenReturn(new AuditionPost[]{post1, post2});

        // Call method
        List<AuditionPost> posts = auditionIntegrationClient.getPosts(null);

        // Verify results
        assertEquals(2, posts.size());
    }

    @Test
    void testGetPostById() {
        // Mock response
        AuditionPost post = new AuditionPost();
        post.setId(1);
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", AuditionPost.class))
            .thenReturn(post);

        // Call method
        AuditionPost result = auditionIntegrationClient.getPostById("1");

        // Verify results
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testGetPostByIdException() {
        // Mock exception
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Call method and verify exception
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById("1"));
    }

    @Test
    void testGetPostsByIdWithComments() {
        // Mock responses
        AuditionPost post = new AuditionPost();
        post.setId(1);
        PostComment comment = new PostComment();
        comment.setPostId(1);
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", AuditionPost.class))
            .thenReturn(post);
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1/comments", PostComment[].class))
            .thenReturn(new PostComment[]{comment});

        // Call method
        AuditionPostWithComments result = auditionIntegrationClient.getPostsByIdWithComments("1");

        // Verify results
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1, result.getComments().size());
        assertEquals(1, result.getComments().get(0).getPostId());
    }

    @Test
    void testGetCommentsByPostId() {
        // Mock response
        PostComment comment1 = new PostComment();
        comment1.setPostId(1);
        PostComment comment2 = new PostComment();
        comment2.setPostId(1);
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/comments?postId=1", PostComment[].class))
            .thenReturn(new PostComment[]{comment1, comment2});

        // Call method
        List<PostComment> comments = auditionIntegrationClient.getCommentsByPostId("1");

        // Verify results
        assertEquals(2, comments.size());
    }

    @Test
    void testGetCommentsByPostIdException() {
        // Mock exception
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/comments?postId=1", PostComment[].class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Call method and verify exception
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getCommentsByPostId("1"));
    }

    @Test
    void testGetPostsHandlesHttpClientErrorException() {
        // Mock exception
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts", AuditionPost[].class))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Call method and verify exception
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts(null));
    }

    @Test
    void testGetPostByIdHandlesHttpClientErrorException() {
        // Mock exception
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Call method and verify exception
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById("1"));
    }

    @Test
    void testGetPostsByIdWithCommentsHandlesHttpClientErrorException() {
        // Mock response for post
        AuditionPost post = new AuditionPost();
        post.setId(1);

        // Mock exceptions
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", AuditionPost.class))
            .thenReturn(post);
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1/comments", PostComment[].class))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Call method and verify exception
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostsByIdWithComments("1"));
    }

    @Test
    void testGetCommentsByPostIdHandlesHttpClientErrorException() {
        // Mock exception
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/comments?postId=1", PostComment[].class))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Call method and verify exception
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getCommentsByPostId("1"));
    }

    @Test
    void testGetPostsHandlesUnexpectedException() {
        // Mock unexpected exception
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts", AuditionPost[].class))
            .thenThrow(new RuntimeException("Unexpected error"));

        // Call method and verify exception
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts(null));
    }

    @Test
    void testGetPostByIdHandlesUnexpectedException() {
        // Mock unexpected exception
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", AuditionPost.class))
            .thenThrow(new SystemException("Unexpected error"));

        // Call method and verify exception
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById("1"));
    }

    @Test
    void testGetPostsByIdWithCommentsHandlesUnexpectedException() {
        // Mock response for post
        AuditionPost post = new AuditionPost();
        post.setId(1);

        // Mock unexpected exceptions
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", AuditionPost.class))
            .thenReturn(post);
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1/comments", PostComment[].class))
            .thenThrow(new SystemException("Unexpected error"));

        // Call method and verify exception
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostsByIdWithComments("1"));
    }

    @Test
    void testGetCommentsByPostIdHandlesUnexpectedException() {
        // Mock unexpected exception
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/comments?postId=1", PostComment[].class))
            .thenThrow(new SystemException("Unexpected error"));

        // Call method and verify exception
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getCommentsByPostId("1"));
    }
}
