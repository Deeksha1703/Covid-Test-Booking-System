package utilities;

//import facilities.FacilitiesFacade;
import facilities.FacilitiesFacade;
import users.User;
import users.UserFacade;

import java.io.IOException;

public class CovidFacade {

    /**
     * Creating a new instance of User class
     */
    private User user;

    /**
     * Instance of UserFacade class
     */
    private UserFacade userFacade;

    /**
     * Instance of CovidFacade class
     */
    private static CovidFacade firstInstance = null;

    /**
     * Constructor for CovidFacade class
     * @return first instance of CovidFacade class
     */
    public static CovidFacade getInstance()  {
        if (firstInstance == null) {
            firstInstance = new CovidFacade();
        }
        return firstInstance;
    }

    /**
     * Accessor for user
     * @return
     */
    public User getNewUser() {
        return this.user;
    }

    /**
     * Function for user to log in to the system
     * @param newUser New User
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void login(User newUser) throws IOException, InterruptedException {
        this.user = newUser;
        userFacade = new UserFacade(user);
        userFacade.login();
    }

    /**
     * Function to create and return a new instance of the FacilitiesFacade class
     * @return Instance of FacilitiesFacade class
     */
    public FacilitiesFacade testSite(){
        return new FacilitiesFacade();
    }
}
