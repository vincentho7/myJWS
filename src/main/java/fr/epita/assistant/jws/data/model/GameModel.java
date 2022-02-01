package fr.epita.assistant.jws.data.model;


import javax.persistence.*;
import java.sql.Timestamp;

@Entity @Table(name = "GameModel")
public class GameModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public @Column(name="starttime") Timestamp startTime;
    public @Column(name="state") String state;

}
