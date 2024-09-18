package com.project.hub.model.dto.response.projects;

import com.project.hub.model.mapper.ShortProjectDetail;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ListShortProjectDetail {
  private final List<ShortProjectDetail> projectDetails;
}
