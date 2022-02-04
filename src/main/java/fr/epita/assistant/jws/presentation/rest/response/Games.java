package fr.epita.assistant.jws.presentation.rest.response;
import fr.epita.assistant.jws.utils.GameState;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor @NoArgsConstructor
public class Games {
    LocalDateTime startTime;
    GameState gameState;
    int players;
    List<String> map;
    long id;
}