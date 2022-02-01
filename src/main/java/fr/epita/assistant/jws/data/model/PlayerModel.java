package fr.epita.assistant.jws.data.model;

import javax.enterprise.inject.Model;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Model
public class PlayerModel {
    public Timestamp lastbomb;
    public Timestamp lastmovement;
    public int lives;
    public String name;
    public int posx;
    public int posy;
    public int position;
}
