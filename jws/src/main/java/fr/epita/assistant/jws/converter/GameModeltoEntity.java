package fr.epita.assistant.jws.converter;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GameModeltoEntity {

    public GameEntity convert(GameModel gameModel){
        return new GameEntity(
                gameModel.id,
                gameModel.players.stream().map(playerModel -> convertP(playerModel)).collect(Collectors.toList()),
                gameModel.state,
                List.copyOf(gameModel.map),
                gameModel.startTime);
    }

    public PlayerEntity convertP(PlayerModel playerModel){
        return new PlayerEntity(
                playerModel.id,
                playerModel.lastbomb,
                playerModel.lastmovement,
                playerModel.lives,
                playerModel.name,
                playerModel.posX,
                playerModel.posY
                );
    }
}
