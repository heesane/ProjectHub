package com.project.hub.model.dto.request.projects;

import com.project.hub.model.type.Skills;
import com.project.hub.model.type.Tools;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ProjectUpdateRequest extends BaseProjectRequest {

  private final Long userId;
  private final Long projectId;

  public ProjectUpdateRequest(
      @Min(1)
      Long userId,
      @Min(1)
      Long projectId,
      @NotBlank
      String title,
      @NotBlank
      String subject,
      @NotBlank
      String feature,
      @NotBlank
      String contents,
      @NotEmpty
      List<Skills> skills,
      @NotEmpty
      List<Tools> tools,
      MultipartFile systemArchitecturePicture,
      MultipartFile erdPicture,
      String githubUrl,
      boolean visible
  ) {
    super(title, subject, feature, contents, skills, tools, systemArchitecturePicture, erdPicture,
        githubUrl, visible);
    this.userId = userId;
    this.projectId = projectId;
  }
}
