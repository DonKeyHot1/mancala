const getCurrentPlayerId = () => {
    return localStorage.getItem('playerId');
}

const setCurrentPlayerId = (playerId) => {
    localStorage.setItem('playerId', playerId);
}

export {
    getCurrentPlayerId,
    setCurrentPlayerId
}
