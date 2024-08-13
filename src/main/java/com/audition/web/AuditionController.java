package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import com.audition.service.AuditionService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditionController {

    private final AuditionService auditionService;

    public AuditionController(AuditionService auditionService) {
        this.auditionService = auditionService;
    }

    // TODO Add a query param that allows data filtering. The intent of the filter is at developers discretion.
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getAllPosts(@RequestParam(value = "title", required = false) String title) {
        // TODO Add logic that filters response data based on the query param
        return auditionService.getPosts(title);
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostById(@PathVariable("id") final String postId) {
        // Validate input
        auditionService.validPostId(postId);
        return auditionService.getPostById(postId);
    }

    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/
    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostsWithComments(@PathVariable("id") final String postId) {
        // Validate input
        auditionService.validPostId(postId);
        return auditionService.getPostsByIdWithComments(postId);
    }

    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<PostComment> getCommentsForPost(@RequestParam("postId") final String postId) {
        auditionService.validPostId(postId);
        return auditionService.getCommentsByPostId(postId);
    }
}
