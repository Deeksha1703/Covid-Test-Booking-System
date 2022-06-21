package model;

import users.User;
import users.UserFactory;
import users.ValidateUser;
import utilities.CovidFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginModel {
    ArrayList<String> userType = new ArrayList<>(Arrays.asList("None", "None"));

    public void login(int selection) throws IOException, InterruptedException {
        if (selection == 1) {
            ValidateUser login = new ValidateUser();
            try {
                userType = login.getUser();
            } catch (IOException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
            UserFactory userFactory = new UserFactory();
            // here you have the instance of the user - resident, receptionist, health care worker. each instance will also contain their id
            User newUser = userFactory.makeUser(userType.get(0), userType.get(1));
            CovidFacade covidFacade = CovidFacade.getInstance();
            covidFacade.login(newUser);
        }
        else{
            System.out.println("Invalid input");
        }

    }
}
