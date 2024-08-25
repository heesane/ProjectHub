package com.project.hub.model.dto.request.projects;

import com.project.hub.model.type.Skills;
import com.project.hub.model.type.Tools;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class BaseProjectRequest {

  @NotBlank
  private final String title;

  @NotBlank
  private final String subject;

  @NotBlank
  private final String feature;

  @NotBlank
  private final String contents;

  @NotBlank
  private final List<Skills> skills;

  @NotBlank
  private final List<Tools> tools;

  private final MultipartFile systemArchitecturePicture;

  private final MultipartFile erdPicture;

  private final String githubUrl;

  private final boolean visible;

}
