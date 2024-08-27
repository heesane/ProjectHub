package com.project.hub.model.dto.response.comments;

import com.project.hub.entity.Comments;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class CommentsList {

  private final Long count;

  private final List<Comment> comments;

  public CommentsList(List<Comments> comments) {
    this.comments = comments.stream()
        .map(Comment::of)
        .collect(Collectors.toList());
    this.count = (long) this.comments.size();
  }
}
