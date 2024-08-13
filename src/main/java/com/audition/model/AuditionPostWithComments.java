package com.audition.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuditionPostWithComments extends AuditionPost {

    private List<PostComment> comments;

    public AuditionPostWithComments(int userId, int id, String title, String body, List<PostComment> comments) {
        super(userId, id, title, body);
        this.comments = comments;
    }
}
