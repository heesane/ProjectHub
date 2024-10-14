package com.project.hub.model.documents;

import com.project.hub.aop.lock.DistributedLockInterface;
import com.project.hub.entity.Projects;
import com.project.hub.model.dto.response.comments.Comment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@SQLDelete(sql = "UPDATE projects SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Document(indexName = "projects")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProjectDocuments implements DistributedLockInterface {

  @Id
  @Field(name = "id", type = FieldType.Keyword)
  private Long id;

  @Field(name = "title", type = FieldType.Text)
  private String title;

  @Field(name = "subject", type = FieldType.Text)
  private String subject;

  @Field(name = "feature", type = FieldType.Text)
  private String feature;

  @Field(name = "contents", type = FieldType.Text)
  private String contents;

  @Field(name = "skills", type = FieldType.Text)
  private List<String> skills;

  @Field(name = "tools", type = FieldType.Text)
  private List<String> tools;

  @Field(name = "system_architecture", type = FieldType.Text)
  private String systemArchitecture;

  @Field(name = "erd", type = FieldType.Text)
  private String erd;

  @Field(name = "github_link", type = FieldType.Keyword)
  private String githubLink;

  @Field(name = "authorName", type = FieldType.Keyword)
  private String authorName;

  @Field(name = "comments", type = FieldType.Nested)
  private List<Comment> comments;

  @Field(name = "commentsCount", type = FieldType.Keyword)
  private Long commentsCount;

  @Field(name = "likeCount", type = FieldType.Keyword)
  private Long likeCount;

  @Field(name = "deleted_at", type = FieldType.Keyword)
  private LocalDateTime deletedAt;

  public void update(Projects projects) {
    this.of(projects);
  }

  public ProjectDocuments of(Projects projects) {
    this.title = projects.getTitle();
    this.subject = projects.getSubject();
    this.feature = projects.getFeature();
    this.contents = projects.getContents();
    this.skills = projects.getSkills().stream().map(Enum::name).collect(Collectors.toList());
    this.tools = projects.getTools().stream().map(Enum::name).collect(Collectors.toList());
    this.systemArchitecture = projects.getSystemArchitectureUrl();
    this.erd = projects.getErdUrl();
    this.githubLink = projects.getGithubUrl();
    this.authorName = projects.getUser().getNickname();
    this.comments =
        projects.getComments() != null ? projects.getComments().stream().map(Comment::of).toList()
            : null;
    this.commentsCount = projects.getComments() != null ? (long) projects.getComments().size() : 0L;
    this.likeCount = projects.getLikeCounts();
    return this;
  }

  @Override
  public String getEntityType() {
    return "projects";
  }
}
