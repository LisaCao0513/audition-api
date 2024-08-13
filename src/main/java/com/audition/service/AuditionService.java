package com.audition.service;

import com.audition.common.exception.SystemException;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostWithComments;
import com.audition.model.PostComment;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class AuditionService {

    @Autowired
    private AuditionIntegrationClient auditionIntegrationClient;


    public List<AuditionPost> getPosts(String title) {
        return auditionIntegrationClient.getPosts(title);
    }

    public AuditionPost getPostById(final String postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    public AuditionPostWithComments getPostsByIdWithComments(final String postId) {
        return auditionIntegrationClient.getPostsByIdWithComments(postId);
    }

    public List<PostComment> getCommentsByPostId(final String postId) {
        return auditionIntegrationClient.getCommentsByPostId(postId);
    }

    public void validPostId(@PathVariable("id") String postId) {
        if (postId == null || postId.trim().isEmpty()) {
            throw new SystemException("postId is required", HttpStatus.BAD_REQUEST);
        }
        try {
            int postIdInt = Integer.parseInt(postId);
        } catch (NumberFormatException e) {
            throw new SystemException("postId must be a valid integer", HttpStatus.BAD_REQUEST);
        }
    }
}
