package com.project.hub.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.hub.model.type.Skills;
import com.project.hub.model.type.Tools;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE projects SET deleted_at = now() WHERE id = ?")
@Table(name = "projects")
public class Projects extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 프로젝트 제목
  @Column(name = "title", nullable = false)
  private String title;

  // 프로젝트 주제(요약)
  @Column(name = "subject", nullable = false)
  private String subject;

  // 프로젝트 기능
  @Column(name = "feature", nullable = false)
  @Lob // MarkUp Language를 저장하기 위해 Lob 사용
  private String feature;

  @Column(name = "contents", nullable = false)
  @Lob // MarkUp Language를 저장하기 위해 Lob 사용
  private String contents;

  @ElementCollection(targetClass = Skills.class)
  @CollectionTable(name = "project_skills", joinColumns = @JoinColumn(name = "project_id"))
  @Enumerated(EnumType.STRING)
  @BatchSize(size = 10)
  @Column(name = "skills")
  private List<Skills> skills;

  @ElementCollection(targetClass = Tools.class)
  @CollectionTable(name = "project_tools", joinColumns = @JoinColumn(name = "tools_id"))
  @Enumerated(EnumType.STRING)
  @BatchSize(size = 10)
  @Column(name = "tools")
  private List<Tools> tools;

  @Column(name = "system_architecture_url", nullable = true)
  private String systemArchitectureUrl;

  @Column(name = "hash_system_architecture", nullable = true)
  private String hashSystemArchitecture;

  @Column(name = "erd_url", nullable = true)
  private String erdUrl;

  @Column(name = "hash_erd", nullable = true)
  private String hashErd;

  @Column(name = "github_url", nullable = true)
  private String githubUrl;

  @Column(name = "visible")
  private boolean visible;

  // 등록한 User
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User user;

  public void updateSystemArchitecture(String newUrl, String newHash) {
    this.systemArchitectureUrl = newUrl;
    this.hashSystemArchitecture = newHash;
  }

  public void updateErd(String newUrl, String newHash) {
    this.erdUrl = newUrl;
    this.hashErd = newHash;
  }

  public void update(String title, String subject, String feature, String contents,
      List<Skills> skills, List<Tools> tools, String githubUrl, boolean visible) {
    this.title = title;
    this.subject = subject;
    this.feature = feature;
    this.contents = contents;
    this.skills = skills;
    this.tools = tools;
    this.githubUrl = githubUrl;
    this.visible = visible;
  }

  public void updateVisible(boolean visible) {
    this.visible = visible;
  }
}
