package fr.epita.assistant.jws.presentation.rest;

import fr.epita.assistant.jws.converter.GameEntitytoDetailResponse;
import fr.epita.assistant.jws.domain.service.GameService;
import fr.epita.assistant.jws.presentation.rest.request.CreateGameRequest;
import fr.epita.assistant.jws.presentation.rest.request.JoinGameRequest;
import fr.epita.assistant.jws.presentation.rest.request.MovePlayerRequest;
import fr.epita.assistant.jws.presentation.rest.request.PutBombRequest;
import fr.epita.assistant.jws.presentation.rest.response.GameListResponse;
import fr.epita.assistant.jws.utils.GameState;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class GameResource {
        @Inject GameService gameService;

        @GET @Path("/games")
        public List<GameListResponse> gameList(){
                var list = gameService.getAllGameEntity();
                return list.stream().map(gameEntity -> new GameListResponse(
                        gameEntity.id,
                        gameEntity.players.size(),
                        gameEntity.state
                )).collect(Collectors.toList());
        }

        @Inject
        GameEntitytoDetailResponse converter;
        @POST @Path("/games")
        public Response create(CreateGameRequest gameRequest){
                if(gameRequest == null || gameRequest.name == null){
                        return Response.status(400).build();
                }
                var gameCr = converter.convertDTO(gameService.createGame(gameRequest.name));
                return Response.ok(gameCr).build();
        }

        @GET @Path("/games/{gamesId}")
        public Response gameDetailbyId(@PathParam("gamesId") Long id){
                var entity = gameService.getGamebyId(id);
                if(entity == null || id == null)
                        return Response.status(404).build();
                var gameCr = converter.convertDTO(entity);
                return Response.ok(gameCr).build();
        }
        @POST @Path("/games/{gamesId}")
        public Response joinGame(@PathParam("gamesId") Long id, JoinGameRequest joinRequest){
                if(joinRequest == null || joinRequest.name == null){
                        return Response.status(400).build();
                }
                //need to verify size, or game too many players
                var gameModel = gameService.getGamebyId(id);

                if (gameModel == null)
                        return Response.status(404).build();

                if(gameModel.players.size() >= 4 || gameModel.state != GameState.STARTING)
                        return Response.status(400).build();

                var entity = gameService.joinGame(id, joinRequest.name);

                var gameCr = converter.convertDTO(entity);
                return Response.ok(gameCr).build();
        }

        @PATCH @Path("/games/{gameId}/start")
        public Response startGame(@PathParam("gameId") Long id){
                if(id == null){
                        return Response.status(404).build();
                }
                var entity = gameService.startGame(id);
                if(entity == null)
                        return Response.status(404).build();
                var gameCr = converter.convertDTO(entity);
                return Response.ok(gameCr).build();
        }

        @POST @Path("/games/{gameId}/players/{playerID}/move")
        public Response movePlayer(@PathParam("gameId") Long gameId, @PathParam("playerID") Long playerId, MovePlayerRequest movePlayerRequest){
                if(playerId == null || gameId == null || movePlayerRequest == null) {
                        return Response.status(404).build();
                }

                var gameModel = gameService.checkGame(gameId);
                var playerModel = gameService.checkPlayer(playerId);

                if(gameModel == null || playerModel == null)
                {
                        return Response.status(404).build();
                }

                if(gameService.checkGameRunning(gameModel, playerModel))
                        return Response.status(400).build();
                if(gameService.checkMoveTick(playerModel))
                        return Response.status(429).build();

                var entity = gameService.movePlayer(gameId.longValue(), playerId.longValue(), movePlayerRequest);
                if (entity == null)
                        return Response.status(400).build();

                var gameCr = converter.convertDTO(entity);
                return Response.ok(gameCr).build();
        }

        @POST @Path("/games/{gameId}/players/{playerID}/bomb")
        public Response putBomb(@PathParam("gameId") Long gameId, @PathParam("playerID") Long playerId, PutBombRequest putBombRequest){
                if(playerId == null || gameId == null){
                        return Response.status(404).build();
                }
                var gameModel = gameService.checkGame(gameId);
                var playerModel = gameService.checkPlayer(playerId);

                if(gameModel == null || playerModel == null)
                {
                        return Response.status(404).build();
                }

                if(putBombRequest == null || playerModel.name == null || gameModel.state != GameState.RUNNING || gameModel.players.size() > 4)
                        return Response.status(400).build();

                /*if (entity == null)
                        return Response.status(404).build();
                var gameCr = converter.convertDTO(entity);
                return Response.ok(gameCr).build();

                 */
                return null;
        }

}
