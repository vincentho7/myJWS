package fr.epita.assistant.jws.domain.entity;

import fr.epita.assistant.jws.data.model.GameModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;
@With @NoArgsConstructor @AllArgsConstructor
public class PlayerEntity {
    public long id;
    public LocalDateTime lastbomb;
    public LocalDateTime lastmovement;
    public int lives;
    public String name;
    public int posX;
    public int posY;
    public int position;
    public GameModel gameModel;

    /*
    public PlayerEntity(long id, LocalDateTime lastbomb, LocalDateTime lastmovement,
                        int lives, String name, int posX, int posY, int position, GameModel gameModel) {

        this.id = id;
        this.lastbomb = lastbomb;
        this.lastmovement = lastmovement;
        this.lives = lives;
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.position = position;
        this.gameModel = gameModel;
    }*/
}
