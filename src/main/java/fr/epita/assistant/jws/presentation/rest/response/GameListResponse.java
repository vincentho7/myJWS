package fr.epita.assistant.jws.presentation.rest.response;
import fr.epita.assistant.jws.utils.GameState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;


@Value @With
public class GameListResponse {
    public long id;
    public int players;
    public GameState state;
}
