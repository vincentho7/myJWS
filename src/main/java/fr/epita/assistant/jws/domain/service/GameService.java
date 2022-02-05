package fr.epita.assistant.jws.domain.service;

import fr.epita.assistant.jws.converter.GameEntitytoDetailResponse;
import fr.epita.assistant.jws.converter.GameModeltoEntity;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.data.repository.PlayerRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;


import fr.epita.assistant.jws.utils.GameState;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GameService  {
    @ConfigProperty(name = "JWS_MAP_PATH") String Path;
    @Inject GameRepository gameRepository;
    @Inject
    PlayerRepository playerRepository;
    @Inject GameModeltoEntity gameModeltoEntity;
    //@Inject GameEntitytoDetailResponse gameEntitytoDetailResponse;

    @Transactional
    public GameEntity getGamebyId(Long id) {
        var game = gameRepository.findById(id);
        if (game == null)
            return null;
        return gameModeltoEntity.convert(game);
    }

    @Transactional
    public GameEntity joinGame(Long id , String name){
        var game = gameRepository.findById(id);
        if (game == null)
            return null;
        int count = game.players.size();
        int x;
        int y;
        if (count == 1){
            x = 15;
            y = 1;
        }
        else if (count == 2){
            x = 1;
            y = 13;
        }
        else if (count == 3){
            x = 15;
            y = 13;
        }
        else
            return gameModeltoEntity.convert(game);

        PlayerModel playerModel = new PlayerModel().withLives(3).withName(name).withLives(3).withPosX(x).withPosY(y).withGameModel(game);
        playerRepository.persist(playerModel);
        game.players.add(playerModel);
        return gameModeltoEntity.convert(game);
    }
    @Transactional
    public GameEntity createGame(String name){
        GameModel gameModel = new GameModel().withStartTime(LocalDateTime.now()).withPlayers(new ArrayList<>()).withState(GameState.STARTING).withMap(takefile(Path));
        gameRepository.persist(gameModel);
        PlayerModel playerModel = new PlayerModel().withLives(3).withName(name).withPosX(1).withPosY(1);
        playerRepository.persist(playerModel);
        gameModel.players.add(playerModel);
        return gameModeltoEntity.convert(gameModel);
    }


    @Transactional
    public List<GameEntity> getAllGameEntity(){
        var gamelist = gameRepository.findAll();
        return gamelist.stream().map(gameModel -> gameModeltoEntity.convert(gameModel)).collect(Collectors.toList());
    }
    @Transactional
    public List<String> takefile(String fileName){
        Path file = Paths.get(fileName);
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public GameEntity startGame(Long id) {
        var game = gameRepository.findById(id);
        if (game == null)
            return null;
        game.state = GameState.RUNNING;
        return gameModeltoEntity.convert(game);
    }
}
