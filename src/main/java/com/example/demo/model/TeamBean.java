package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamBean implements Serializable {
    private int teamId;
    private String win;

    public boolean getWin() {
        return win.equals("Win");
    }
}
