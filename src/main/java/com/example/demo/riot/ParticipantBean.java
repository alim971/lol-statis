package com.example.demo.riot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantBean implements Serializable {
    private int participantId;
    private UserBean player;
}
