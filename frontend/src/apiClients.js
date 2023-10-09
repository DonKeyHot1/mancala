import {ApiClient, GameApi, ParticipationApi, PlayerApi} from "./generated/src/index.js";
import {getCurrentPlayerId} from "./clientStorage.js";

const apiClient = new ApiClient(`${window.location.protocol}//${window.location.hostname}:8080`);
apiClient.defaultHeaders = {
    "x-player-id": getCurrentPlayerId()?.toString()
};

const gameApiClient = new GameApi(apiClient)
const participationApiClient = new ParticipationApi(apiClient)
const playerApiClient = new PlayerApi(apiClient)

export {
    gameApiClient,
    participationApiClient,
    playerApiClient
};
