package ru.mnagovitsin.mancala.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTestBase {

    private static final Deque<Runnable> CLEANUP_ACTIONS = new ConcurrentLinkedDeque<>();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected void addCleanupAction(Runnable action) {
        CLEANUP_ACTIONS.add(action);
    }

    @AfterEach
    protected void cleanup() {
        while (!CLEANUP_ACTIONS.isEmpty()) {
            CLEANUP_ACTIONS.pop().run();
        }
    }
}
