package fr.epita.assistant.jws.domain.entity;

import fr.epita.assistant.jws.utils.GameState;

import java.time.LocalDateTime;
import java.util.List;

public class  GameEntity {
    public static LocalDateTime startTime;
    public GameState state;
    public List<PlayerEntity> players;
    public List<String> map;
    public long id;

    public GameEntity(long id, List<PlayerEntity> players, GameState state, List<String> map, LocalDateTime startTime) {
        this.id = id;
        this.players = players;
        this.state = state;
        this.map = map;
        this.startTime=startTime;
   }
}
