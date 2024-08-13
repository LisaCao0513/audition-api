package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostWithComments;
import com.audition.model.PostComment;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    @Autowired
    private RestTemplate restTemplate;

    public static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private static final Logger logger = LoggerFactory.getLogger(AuditionIntegrationClient.class);

    public List<AuditionPost> getPosts(@Nullable String title) {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts
        try {
            AuditionPost[] postsArray = restTemplate.getForObject(BASE_URL + "/posts", AuditionPost[].class);
            if (postsArray == null) {
                return new ArrayList<>();
            }
            if (StringUtils.isNotBlank(title)) {
                // Filter posts by title if the query parameter is provided
                return Arrays.stream(postsArray)
                    .filter(post -> post.getTitle() != null && post.getTitle().contains(title))
                    .collect(Collectors.toList());
            } else {
                return Arrays.asList(postsArray);
            }
        } catch (final HttpClientErrorException ex) {
            logger.error("An unexpected error occurred in getPosts: ", ex);
            throw new SystemException(ex.getResponseBodyAsString(), ex.getStatusCode());
        } catch (Exception exception) {
            logger.error("An unexpected error occurred in getPosts: ", exception);
            throw new SystemException(exception.getMessage(), exception);
        }
    }

    public AuditionPost getPostById(final String id) {
        // TODO get post by post ID call from https://jsonplaceholder.typicode.com/posts/
        try {
            return restTemplate.getForObject(BASE_URL + "/posts/" + id, AuditionPost.class);
        } catch (final HttpClientErrorException ex) {
            logger.error("API exception occurred in : getPostById", ex);
            // TODO Find a better way to handle the exception so that the original error message is not lost. Feel free to change this function.
            throw new SystemException(ex.getMessage(), ex.getStatusCode());
        }
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.
    public AuditionPostWithComments getPostsByIdWithComments(String id) {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts
        try {
            AuditionPost auditionPost = getPostById(id);
            PostComment[] commentArray = restTemplate.getForObject(BASE_URL + "/posts/" + id + "/comments",
                PostComment[].class);
            List<PostComment> postCommentList = commentArray != null ? Arrays.asList(commentArray) : new ArrayList<>();
            return new AuditionPostWithComments(auditionPost.getUserId(), auditionPost.getId(),
                auditionPost.getTitle(), auditionPost.getBody(), postCommentList);
        } catch (final HttpClientErrorException ex) {
            logger.error("An unexpected error occurred in : getPostsByIdWithComments", ex);
            throw new SystemException(ex.getResponseBodyAsString());
        }
    }

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.
    public List<PostComment> getCommentsByPostId(String id) {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts
        try {
            PostComment[] commentArray = restTemplate.getForObject(BASE_URL + "/comments?postId=" + id,
                PostComment[].class);
            return commentArray != null ? Arrays.asList(commentArray) : new ArrayList<>();
        } catch (final HttpClientErrorException ex) {
            logger.error("An unexpected error occurred in getCommentsByPostId: ", ex);
            throw new SystemException(ex.getResponseBodyAsString());
        }
    }
}
