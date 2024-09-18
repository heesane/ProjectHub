package com.project.hub.model.dto.response.user;

import com.project.hub.model.dto.response.projects.ListShortProjectDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Profile {

  private final String email;

  private final String nickname;

  private final Long commentCounts;

  private final Long projectCounts;

  private final ListShortProjectDetail projects;

  private final ListShortProjectDetail likedProjects;

  private final String badge;

}
