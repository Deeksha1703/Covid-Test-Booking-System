package users;

import java.io.IOException;
import java.text.ParseException;

public class UserFacade {

    /**
     * Creating new User
     */
    private User newUser;

    /**
     * Constructor for UserFacade class
     * @param newUser Instance of User class
     */
    public UserFacade(User newUser) {
        this.newUser = newUser;
    }

    /**
     * Function for user to log in to the system
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void login() throws IOException, InterruptedException {
        int selection = newUser.displayMenu();
        try{
            newUser.displayOptions(selection);
        }
        catch(IOException | InterruptedException | ParseException e){
            System.out.println(e.getMessage());
        }
    }
}
