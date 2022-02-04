package fr.epita.assistant.jws.presentation.rest.response;
import fr.epita.assistant.jws.utils.GameState;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

public class GameDetailResponse {
    public LocalDateTime startTime;
    public GameState state;
    public List<Player> players;
    public List<String> map;
    public long id;

    public GameDetailResponse(LocalDateTime startTime, GameState state, List<Player> players, List<String> map, long id) {
        this.startTime = startTime;
        this.state = state;
        this.players = players;
        this.map = map;
        this.id = id;
    }

    @With
    @NoArgsConstructor @AllArgsConstructor
    public static class Player{
        public long id;
        public String name;
        public int lives;
        public int posX;
        public int posY;
    }
}
