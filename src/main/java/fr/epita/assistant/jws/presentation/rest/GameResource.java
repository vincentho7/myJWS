package fr.epita.assistant.jws.presentation.rest;

import fr.epita.assistant.jws.converter.GameEntitytoDetailResponse;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.service.GameService;
import fr.epita.assistant.jws.presentation.rest.request.CreateGameRequest;
import fr.epita.assistant.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistant.jws.presentation.rest.response.GameListResponse;

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
        public Response createGame(CreateGameRequest gameRequest){
                if(gameRequest == null || gameRequest.name == null){
                        return Response.status(400).build();
                }
                var gameCr = converter.convertDTO(gameService.createGame(gameRequest.name));
                return Response.ok(gameCr).build();
        }
}
