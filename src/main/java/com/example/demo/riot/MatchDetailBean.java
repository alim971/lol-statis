package com.example.demo.riot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchDetailBean implements Serializable {
    private String gameId;
    private TeamBean[] teams;
    private ParticipantBean[] participantIdentities;
}
