package com.project.hub.repository.jpa;

import com.project.hub.entity.Comments;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {

  @Query("select c.id from Comments c")
  List<Long> findAllIdWithDetail();

  Long countByUserId(Long userId);
}
