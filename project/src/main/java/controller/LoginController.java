package controller;

import model.LoginModel;
import view.LoginView;

import java.io.IOException;

public class LoginController {
    private LoginModel loginModel;
    private LoginView loginView;

    public LoginController(LoginModel loginModel, LoginView loginView) {
        this.loginModel = loginModel;
        this.loginView = loginView;
    }

    public void displayMenu() throws IOException, InterruptedException {
        int selection =  loginView.menuSelection();
        loginModel.login(selection);
    }
}
