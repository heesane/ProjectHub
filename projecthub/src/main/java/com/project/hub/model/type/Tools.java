package com.project.hub.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Tools {
  SLACK("Slack"),
  NOTION("Notion"),
  JIRA("Jira"),
  TRELLO("Trello"),
  ZOOM("Zoom"),
  GOOGLE_MEET("Google Meet"),
  MICROSOFT_TEAMS("Microsoft Teams"),
  DISCORD("Discord"),
  ;

  private final String name;
}
