package com.project.hub.aop.badge;

import com.project.hub.entity.Projects;
import java.util.List;

public interface BadgeInterface {
  Long getId();
  List<Projects> getProjects();
}
