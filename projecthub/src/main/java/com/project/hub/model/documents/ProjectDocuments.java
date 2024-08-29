package com.project.hub.model.documents;

import com.project.hub.model.dto.response.comments.Comment;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "projects")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProjectDocuments {

  @Id
  @Field(name = "id", type = FieldType.Keyword)
  private Long id;

  @Field(name = "title", type = FieldType.Keyword)
  private String title;

  @Field(name = "subject", type = FieldType.Keyword)
  private String subject;

  @Field(name = "feature", type = FieldType.Keyword)
  private String feature;

  @Field(name = "contents", type = FieldType.Keyword)
  private String contents;

  @Field(name = "skills", type = FieldType.Keyword)
  private List<String> skills;

  @Field(name = "tools", type = FieldType.Keyword)
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
}
