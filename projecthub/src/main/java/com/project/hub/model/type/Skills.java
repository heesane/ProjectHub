package com.project.hub.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Skills {

  // 백엔드 기술
  JAVA("Java"),
  SPRING("Spring"),
  SPRING_BOOT("Spring Boot"),
  JPA("JPA"),
  HIBERNATE("Hibernate"),
  MYSQL("MySQL"),
  POSTGRESQL("PostgreSQL"),
  MONGODB("MongoDB"),
  REDIS("Redis"),
  KAFKA("Kafka"),
  RABBITMQ("RabbitMQ"),
  DOCKER("Docker"),
  KUBERNETES("Kubernetes"),
  AWS("AWS"),
  GCP("GCP"),
  AZURE("Azure"),
  CI_CD("CI/CD"),
  GIT("Git"),
  REST_API("REST API"),
  GRAPHQL("GraphQL"),

  // 프론트엔드 기술
  JAVASCRIPT("JavaScript"),
  TYPESCRIPT("TypeScript"),
  REACT("React"),
  ANGULAR("Angular"),
  VUE("Vue.js"),
  HTML("HTML"),
  CSS("CSS"),
  SASS("SASS"),
  LESS("LESS"),
  BOOTSTRAP("Bootstrap"),
  TAILWIND("Tailwind CSS"),

  // 인프라 및 기타 기술
  LINUX("Linux"),
  NGINX("Nginx"),
  APACHE("Apache"),
  JENKINS("Jenkins"),
  ANSIBLE("Ansible"),
  TERRAFORM("Terraform"),
  PROMETHEUS("Prometheus"),
  GRAFANA("Grafana"),
  ELASTICSEARCH("Elasticsearch"),
  LOGSTASH("Logstash"),
  KIBANA("Kibana");

  private final String name;
}
