package com.project.hub.util;

import com.project.hub.aop.lock.DistributedLock;
import com.project.hub.entity.Comments;
import com.project.hub.entity.Projects;

public class UpdateManager {

  @DistributedLock
  public static void incrementProjectCommentCount(Projects projects) {
    projects.incrementCommentCounts();
  }

  @DistributedLock
  public static void updateProjectLikeCount(Projects project, Long likeCount) {
    project.updateLikeCounts(likeCount);
  }

  @DistributedLock
  public static void updateCommentLikeCount(Comments comments, Long likeCount) {
    comments.updateLikes(likeCount);
  }
}
