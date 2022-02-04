package fr.epita.assistant.jws.converter;

import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.presentation.rest.response.GameDetailResponse;

import java.util.stream.Collectors;

public class GameEntitytoDetailResponse {
    public GameDetailResponse convertDTO(GameEntity gameEntity){
        return new GameDetailResponse(
                gameEntity.startTime,
                gameEntity.state,
                gameEntity.players.stream().map(this::convertPlayerDTO).collect(Collectors.toList()),
                gameEntity.map,
                gameEntity.id
        );

    }
    public GameDetailResponse.Player convertPlayerDTO(PlayerEntity entity){
        return new GameDetailResponse.Player(
                entity.id, entity.name, entity.lives, entity.posX, entity.posY
        );
    }
}
