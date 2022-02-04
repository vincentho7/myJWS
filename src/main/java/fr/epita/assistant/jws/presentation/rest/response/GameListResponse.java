package fr.epita.assistant.jws.presentation.rest.response;
import fr.epita.assistant.jws.utils.GameState;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@Value @With
public class GameListResponse {
    public long id;
    public int players;
    public GameState gameState;
    /*
    public GameListResponse(long id, int players, GameState gameState1) {
        this.id = id;
        this.players = players;
        this.gameState1 = gameState1;
    }*/
}
