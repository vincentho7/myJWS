package fr.epita.assistant.jws.data.model;



import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Player")
public class PlayerModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public @Column(name = "lastbomb") Timestamp lastbomb;
    public @Column(name = "lastmovement") Timestamp lastmovement;
    public @Column(name = "lives") int lives;
    public @Column(name = "name") String name;
    public @Column(name = "posx") int posx;
    public @Column(name = "posy") int posy;
    public @Column(name = "position") int position;
}
