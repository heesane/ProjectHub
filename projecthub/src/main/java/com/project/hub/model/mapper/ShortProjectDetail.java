package com.project.hub.model.mapper;

import com.project.hub.entity.Projects;
import lombok.Getter;

@Getter
public class ShortProjectDetail {

  private final String title;
  private final String subject;

  public ShortProjectDetail(Projects projects) {
    this.title = projects.getTitle();
    this.subject = projects.getSubject();
  }
}
