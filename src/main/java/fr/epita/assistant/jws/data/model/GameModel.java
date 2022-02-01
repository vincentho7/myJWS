package fr.epita.assistant.jws.presentation.data.model;

import javax.enterprise.inject.Model;
import java.sql.Timestamp;

@Model
public class GameModel {
    public Timestamp startTime;
    public int id;
    public int player;
    public String state;

}
