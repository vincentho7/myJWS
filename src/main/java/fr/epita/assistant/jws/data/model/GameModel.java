package fr.epita.assistant.jws.data.model;
import fr.epita.assistant.jws.utils.GameState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "game")
@AllArgsConstructor @NoArgsConstructor @With

public class GameModel {
    public LocalDateTime startTime;
    public GameState state;
    public @OneToMany(cascade = CascadeType.ALL)  List<PlayerModel> players;
    @ElementCollection public List<String> map;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public long id;
}
