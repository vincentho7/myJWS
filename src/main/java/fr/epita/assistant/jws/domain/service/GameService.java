package fr.epita.assistant.jws.domain.service;

import fr.epita.assistant.jws.converter.GameEntitytoDetailResponse;
import fr.epita.assistant.jws.converter.GameModeltoEntity;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.data.repository.PlayerRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;


import fr.epita.assistant.jws.presentation.rest.request.MovePlayerRequest;
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
public class GameService {
    @ConfigProperty(name = "JWS_MAP_PATH")

    String Path;
    @Inject
    GameRepository gameRepository;
    @Inject
    PlayerRepository playerRepository;
    @Inject
    GameModeltoEntity gameModeltoEntity;
    //@Inject GameEntitytoDetailResponse gameEntitytoDetailResponse;
    @Transactional
    public GameEntity getGamebyId(Long id) {
        var game = gameRepository.findById(id);
        if (game == null)
            return null;
        return gameModeltoEntity.convert(game);
    }

    @Transactional
    public GameEntity joinGame(Long id, String name) {
        var game = gameRepository.findById(id);
        if (game == null)
            return null;
        int count = game.players.size();
        int x;
        int y;
        if (count == 1) {
            x = 15;
            y = 1;
        } else if (count == 2) {
            x = 1;
            y = 13;
        } else if (count == 3) {
            x = 15;
            y = 13;
        } else
            return gameModeltoEntity.convert(game);

        PlayerModel playerModel = new PlayerModel().withLives(3).withName(name).withLives(3).withPosX(x).withPosY(y).withGameModel(game);
        playerRepository.persist(playerModel);
        game.players.add(playerModel);
        return gameModeltoEntity.convert(game);
    }

    @Transactional
    public GameEntity createGame(String name) {
        GameModel gameModel = new GameModel().withStartTime(LocalDateTime.now()).withPlayers(new ArrayList<>()).withState(GameState.STARTING).withMap(takefile(Path));
        gameRepository.persist(gameModel);
        PlayerModel playerModel = new PlayerModel().withLives(3).withName(name).withPosX(1).withPosY(1);
        playerRepository.persist(playerModel);
        gameModel.players.add(playerModel);
        return gameModeltoEntity.convert(gameModel);
    }


    @Transactional
    public List<GameEntity> getAllGameEntity() {
        var gamelist = gameRepository.findAll();
        return gamelist.stream().map(gameModel -> gameModeltoEntity.convert(gameModel)).collect(Collectors.toList());
    }

    @Transactional
    public List<String> takefile(String fileName) {
        Path file = Paths.get(fileName);
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Transactional
    public GameEntity startGame(Long id) {
        var game = gameRepository.findById(id);
        if (game == null)
            return null;
        if(game.state == GameState.STARTING)
            game.state = GameState.RUNNING;
        return gameModeltoEntity.convert(game);
    }

    public String encodeRLEString(String string) {
        var str = new StringBuilder();
        int len = string.length();
        int count = 1;
        int j = 1;
        for (j = 0; j < len - 1; j++) {
            if (string.charAt(j) == string.charAt(j + 1)) {
                count++;
            } else {
                str.append(String.valueOf(count) + string.charAt(j));
                count = 1;
            }
        }
        return str.toString();
    }

    //for bomb
    public String decodeRLEString(String string) {
        var decodedFile = new StringBuilder();
        var file = new StringBuilder(string);
        for (int i = 0; i < string.length(); i+=2) {
            char str = file.charAt(i + 1);
            int count = file.charAt(i) - '0';
            for (int j = 0; j < count; j++) {
                decodedFile.append(str);
            }
        }
        return decodedFile.toString();
    }
    @ConfigProperty(name = "JWS_DELAY_MOVEMENT") int delay;
    @ConfigProperty(name = "JWS_TICK_DURATION") int ticks;
    @Transactional
    public GameEntity movePlayer(long gameId, long playerId, MovePlayerRequest movePlayerRequest) {
        var game = gameRepository.findById(gameId);
        var player = playerRepository.findById(playerId);
        var mv = player.lastmovement;
        if (mv != null && LocalDateTime.now().isBefore(mv.plusNanos((long) delay * ticks * 1000000))){
            return null;
        }
        player.lastmovement = LocalDateTime.now();

        if (check_Map_Moves(game.map, movePlayerRequest)){
            player.posX = movePlayerRequest.posX;
            player.posY = movePlayerRequest.posY;
            return gameModeltoEntity.convert(game);
        }
        else
            return null;
    }
    private boolean check_Map_Moves(List<String> mapList, MovePlayerRequest movePlayerRequest) { //put System print to test
        var decodedlist = mapList.stream().map(e -> decodeRLEString(e)).collect(Collectors.toList());
        if((movePlayerRequest.posY >= decodedlist.size() || movePlayerRequest.posY <= 0))
            return false;
        var lineY = decodedlist.get(movePlayerRequest.posY);
        if(lineY.length() <= movePlayerRequest.posX)
            return false;
        var elt = lineY.charAt(movePlayerRequest.posX);
        if(elt == 'W' || elt == 'M' || elt == 'B')
            return false;
        return true;
    }

    @ConfigProperty(name = "JWS_DELAY_BOMB") int delayBomb;
    @ConfigProperty(name = "JWS_DELAY_FREE") int delayFree;
    public GameEntity putBomb(long longValue, long longValue1, MovePlayerRequest movePlayerRequest) {
        return null;
    }
}
