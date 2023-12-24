package com.mycompany.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

public class ServerViewTest {

    private ServerView serverView;

    @BeforeEach
    public void setUp() throws InterruptedException, IOException {
        serverView = new ServerView();
    }

    @Test
    public void testModel() {
        assertNotNull(serverView.model);
    }

    @Test
    public void testController() {
        assertNotNull(serverView.controller);
    }

    @Test
    public void testPanel() {
        assertNotNull(serverView.mainPanel);
    }

}