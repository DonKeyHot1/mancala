package ru.mnagovitsin.mancala.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.mnagovitsin.mancala.model.CreateNewPlayerResponse;
import ru.mnagovitsin.mancala.model.Player;
import ru.mnagovitsin.mancala.repository.PlayerRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: finish integration tests
public class PlayerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    @SneakyThrows
    void testCreatePlayer() {
        var response = mockMvc.perform(post("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
            {
                "requestId": "be2cd74a-5d86-43ad-98bf-a5bc9f92fe8f",
                "title": "Champion_1337"
            }
            """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andReturn()
            .getResponse();

        var id = objectMapper.readValue(response.getContentAsString(), CreateNewPlayerResponse.class)
            .getId();

        addCleanupAction(() -> playerRepository.deleteById(id));

        assertThat(playerRepository.getById(id))
            .isNotNull()
            .map(Player::nickname).hasValue("Champion_1337");
    }
}
