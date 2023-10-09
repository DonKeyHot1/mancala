package ru.mnagovitsin.mancala.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ru.mnagovitsin.mancala.service.PlayerService;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerIdFilter implements Filter {

    private final PlayerService playerService;
    private final PlayerIdHolder playerIdHolder;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws ServletException, IOException {

        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;

        if (request.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        var playerId = request.getHeader("x-player-id");

        if (!isPlayerIdValid(playerId)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid x-player-id header");
            return;
        }

        playerIdHolder.set(UUID.fromString(playerId));

        chain.doFilter(request, response);
    }

    private boolean isPlayerIdValid(String playerId) {
        var isStringIdValid = playerId != null && !playerId.isBlank();

        if (!isStringIdValid) {
            return false;
        }

        UUID playerUUID;
        try {
            playerUUID = UUID.fromString(playerId);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return playerService.existsById(playerUUID);
    }
}
