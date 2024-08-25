package com.project.hub.entity;

import com.project.hub.model.type.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "member")
public class User extends BaseTimeEntity {

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
  @OneToMany(mappedBy = "user")
  private List<Projects> projects;
}
