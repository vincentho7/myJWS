package fr.epita.assistant.jws.domain.service;


import fr.epita.assistant.jws.converter.GameModeltoEntity;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.data.repository.PlayerRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;


import fr.epita.assistant.jws.presentation.rest.request.MovePlayerRequest;
import fr.epita.assistant.jws.presentation.rest.request.PutBombRequest;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ApplicationScoped
public class GameService {
    @ConfigProperty(name = "JWS_MAP_PATH") String Path;
    @Inject GameRepository gameRepository;
    @Inject PlayerRepository playerRepository;
    @Inject GameModeltoEntity gameModeltoEntity;

    //Check Repository
    @Transactional
    public GameModel checkGame(Long gameId){
        return gameRepository.findById(gameId.longValue());
    }
    @Transactional
    public PlayerModel checkPlayer(Long playerId){
        return playerRepository.findById(playerId.longValue());
    }
    //get game by id
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
            x = 15;
            y = 13;
        } else if (count == 3) {
            x = 1;
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

    /*Read file imported by JWS path*/
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
        if (game == null || game.state != GameState.STARTING)
            return null;
        if(game.players.size() == 1)
            game.state = GameState.FINISHED;
        else
            game.state = GameState.RUNNING;
        return gameModeltoEntity.convert(game);
    }

    /*Reencode  RLE dile*/
    public String encodeRLEString(String string) {
        var str = new StringBuilder();
        int len = string.length() - 1;
        int count = 1;
        int j = 1;
        for (j = 0; j < len; j++) {
            if (string.charAt(j) == string.charAt(j + 1) && count < 9) {
                count++;
            } else {
                str.append(String.valueOf(count));
                str.append(string.charAt(j));
                count = 1;
            }
        }
        str.append(count);
        str.append(string.charAt(j));
        return str.toString();
    }

    /*Decoding RLE Files*/
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

    @Transactional
     public Boolean checkMoveTick(PlayerModel player){
         var mv = player.lastmovement;
         return (mv != null && LocalDateTime.now().isBefore(mv.plusNanos((long) delay * ticks * 1000000)));
     }

     @Transactional
     public Boolean checkGameRunning(GameModel gameModel, PlayerModel playerModel){
        return (gameModel.state != GameState.RUNNING || playerModel.lives == 0);
     }

     //if game is not running or player is already dead or can't move to the specified position error 400
    @ConfigProperty(name = "JWS_DELAY_MOVEMENT") int delay;
    @ConfigProperty(name = "JWS_TICK_DURATION") int ticks;
    @Transactional
    public GameEntity movePlayer(long gameId, long playerId, MovePlayerRequest movePlayerRequest) {
        var game = gameRepository.findById(gameId);
        var player = playerRepository.findById(playerId);
        //var mv = player.lastmovement;
        // verify time
        //if (mv != null && LocalDateTime.now().isBefore(mv.plusNanos((long) delay * ticks * 1000000))){
           // return null;
        //}
        player.lastmovement = LocalDateTime.now();

        if (check_Map_Moves(game.map, movePlayerRequest)){
            player.posX = movePlayerRequest.posX;
            player.posY = movePlayerRequest.posY;
            return gameModeltoEntity.convert(game);
        }
        else
            return null;
    }

    private boolean check_Map_Moves(List<String> mapList, MovePlayerRequest movePlayerRequest) {
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


    @Transactional
    public GameEntity putBomb(long gameId, long playerId, PutBombRequest putBombRequest) {
        var gameModel = checkGame(gameId);
        //var playerModel = checkPlayer(playerId);

        if(gameModel == null)
            return null;
        var decodedMap = gameModel.map.stream().map((e->decodeRLEString(e))).collect(Collectors.toList());
        var lineBomb = new StringBuilder(decodedMap.get(putBombRequest.posY));

        lineBomb.setCharAt(putBombRequest.posX, 'B');
        decodedMap.set(putBombRequest.posY, lineBomb.toString());

        var reencodedMap = decodedMap.stream().map(this::encodeRLEString).collect(Collectors.toList());
        gameModel.map=reencodedMap;
        var e = CompletableFuture.delayedExecutor(ticks * delayBomb, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(()->{ explosion(playerId, gameId, putBombRequest);
            },e);
        return gameModeltoEntity.convert(gameModel);
    }
    @Transactional
    public boolean delayBomb(long pLayerId){
        var playerModel = checkPlayer(pLayerId);
        var bomb = playerModel.lastbomb;
        return (bomb != null && LocalDateTime.now().isBefore(bomb.plusNanos((long) delay * ticks * 1000000)));
    }
    @Transactional
    public void explosion(long playerId, long gameId, PutBombRequest putBombRequest){
        var playerModel = checkPlayer(playerId);
        playerModel.lastbomb = LocalDateTime.now();
        explode(gameId, playerId, putBombRequest);
    }

    @Transactional
    private void checkGamePlayers(long gameId, PutBombRequest putBombRequest) {
        var gameModel = checkGame(gameId);
        int count = 0;
        for(int i = 0; i < gameModel.players.size(); i++){
            damagePlayer(gameModel.players.get(i).id, putBombRequest);
        }
        for (int i = 0; i < gameModel.players.size(); i++) {
            if(gameModel.players.get(i).lives > 0){
                   count++;
            }
        }
        if (count <= 1){
            gameModel.state = GameState.FINISHED;
        }
    }

    @Transactional
    public void damagePlayer(long playerId, PutBombRequest putBombRequest)
    {
        var playerModel = checkPlayer(playerId);
        if (playerModel.posX == putBombRequest.posX + 1 || putBombRequest.posX - 1 == playerModel.posX
                || playerModel.posY == putBombRequest.posY + 1 || playerModel.posY == putBombRequest.posY - 1)
        {
            playerModel.lives -= 1;
        }
        return;
    }

    @Transactional
    public void explode(long gameId, long playerId, PutBombRequest putBombRequest){
        //decodage

        var gameModel = checkGame(gameId);
        var decodedMap = gameModel.map.stream().map((this::decodeRLEString)).collect(Collectors.toList());

        var upLine = new StringBuilder(decodedMap.get(putBombRequest.posY - 1));
        var bombLine = new StringBuilder(decodedMap.get(putBombRequest.posY));
        var downline = new StringBuilder(decodedMap.get(putBombRequest.posY + 1));

        if(bombLine.charAt(putBombRequest.posX) == 'B')
            bombLine.setCharAt(putBombRequest.posX, 'G');

        if(bombLine.charAt(putBombRequest.posX + 1) == 'W')
            bombLine.setCharAt(putBombRequest.posX + 1, 'G');

        if(bombLine.charAt(putBombRequest.posX - 1) == 'W')
            bombLine.setCharAt(putBombRequest.posX - 1, 'G');

        decodedMap.set(putBombRequest.posY, bombLine.toString());

        if(upLine.charAt(putBombRequest.posX) == 'W')
        {
            upLine.setCharAt(putBombRequest.posX, 'G');
            decodedMap.set(putBombRequest.posY - 1, upLine.toString());
        }
        if(downline.charAt(putBombRequest.posX) == 'W'){
            downline.setCharAt(putBombRequest.posX, 'G');
            decodedMap.set(putBombRequest.posY + 1, downline.toString());
        }
        var stringList = decodedMap.stream().map(this::encodeRLEString).collect(Collectors.toList());

        gameModel.map = stringList;
        checkGamePlayers(gameId, putBombRequest);
        return;
    }
}

