package com.project.hub.entity;

import com.project.hub.aop.badge.BadgeInterface;
import com.project.hub.model.type.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE member SET deleted_at = now() WHERE id = ?")
@Table(name = "member")
public class User extends BaseTimeEntity implements BadgeInterface {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "password", nullable = true)
  private String password;

  @Column(name = "nickname", nullable = true)
  private String nickname;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  @Column(name = "provider", nullable = true)
  private String provider;

  @Column(name = "provider_id", nullable = true)
  private String providerId;

  @Column(name = "refresh_token", nullable = true)
  private String refreshToken;

  // 유저가 등록한 프로젝트의 리스트
  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
  private List<Projects> projects;

  @OneToOne
  @JoinColumn(name="badge_id")
  private Badge badge;

  public void updateBadge(Badge badge){
    this.badge = badge;
  }
}
