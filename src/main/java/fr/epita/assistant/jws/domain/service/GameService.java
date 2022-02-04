package fr.epita.assistant.jws.domain.service;

import fr.epita.assistant.jws.converter.GameModeltoEntity;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;

import fr.epita.assistant.jws.utils.GameState;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GameService  {
    @ConfigProperty(name = "JWS_MAP_PATH") String Path;
    @Inject GameRepository gameRepository;
    @Inject GameModeltoEntity gameModeltoEntity;
    @Transactional
    public GameEntity createGame(String name){
        GameModel gameModel = new GameModel().withStartTime(LocalDateTime.now()).withPlayers(new ArrayList<>()).withState(GameState.FINISHED);
        gameRepository.persist(gameModel);
        return gameModeltoEntity.convert(gameModel);
    }


    public List<GameEntity> getAllGameEntity(){
        var gamelist = gameRepository.findAll();
        return gamelist.stream().map(gameModel -> gameModeltoEntity.convert(gameModel)).collect(Collectors.toList());
    }
}
