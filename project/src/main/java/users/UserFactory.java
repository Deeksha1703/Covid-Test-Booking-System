package users;

import java.io.IOException;

/**
 * Class to create and return a User depending on the userType that is passed to it
 */
public class UserFactory {

    /**
     * Function to create and return a User depending on the userType that is passed to it
     * @param userType Type of user, ie Resident, Receptionist or HealthCareWorker
     * @param userID ID of the user
     * @return user - the user created i.e. Resident, Receptionist or HealthCareWorker
     * @throws IOException Exception that occurs when an IO operation fails
     */
    public User makeUser(String userType, String userID) throws IOException {
        switch (userType) {
            case "Resident":
                return new Resident(userID);
            case "Receptionist":
                return new Receptionist(userID);
            case "HealthCareWorker":
                return new HealthCareWorker(userID);
            default:
                throw new IOException("Invalid user. Please contact customer support");
        }
    }
}
