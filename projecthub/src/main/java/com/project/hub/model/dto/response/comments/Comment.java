package com.project.hub.model.dto.response.comments;

import com.project.hub.entity.Comments;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

  public Long id;

  public Long parentId;

  public String contents;

  public String userNickname;

  public static Comment of(Comments comments) {
    return Comment.builder()
        .id(comments.getId())
        .parentId(comments.getParentComment() == null ? null : comments.getParentComment().getId())
        .contents(comments.getContents())
        .userNickname(comments.getUser().getNickname())
        .build();
  }
}
