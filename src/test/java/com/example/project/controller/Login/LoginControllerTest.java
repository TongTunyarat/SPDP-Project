package com.example.project.controller.Login;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.mockito.Mockito.mock;

class LoginControllerTest {
    LoginController loginController = new LoginController();

    @Test
    void testLoginPage() {
        Model model = mock(Model.class); // mock Model

        String result = loginController.loginPage("error", "logout", model);
        Assertions.assertEquals("login", result);
    }


}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme