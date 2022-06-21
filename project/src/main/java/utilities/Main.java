package utilities;

import controller.LoginController;
import model.LoginModel;
import view.LoginView;


import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        LoginView loginView = new LoginView();

        LoginModel loginModel = new LoginModel();

        LoginController loginController = new LoginController(loginModel, loginView);

        loginController.displayMenu();

    }
}
