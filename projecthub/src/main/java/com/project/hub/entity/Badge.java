package com.project.hub.entity;

import com.project.hub.model.dto.request.badge.UpdateBadgeRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "badge")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Badge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "required_project_count", nullable = false)
  private Long requiredProjectCount;

  @Column(name = "required_comment_count", nullable = false)
  private Long requiredCommentCount;

  public void update(UpdateBadgeRequest request) {
    this.name = request.getName();
    this.description = request.getDescription();
    this.requiredProjectCount = request.getRequiredProjects();
    this.requiredCommentCount = request.getRequiredComments();
  }
}
