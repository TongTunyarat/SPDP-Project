package com.example.project.controller.Login;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoginControllerTest {
    LoginController loginController = new LoginController();

    @Test
    void testDefaultPage() {
        String result = loginController.defaultPage();
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }

    @Test
    void testLoginPage() {
        String result = loginController.loginPage("error", "logout", null);
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }

    @Test
    void testCheckSession() {
        String result = loginController.checkSession();
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme