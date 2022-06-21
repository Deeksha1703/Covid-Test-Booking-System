package users;

import java.io.IOException;
import java.text.ParseException;

/**
 * Interface for users.User which is implemented by the users.Resident,users.Receptionist and users.HealthCareWorker classes
 */
public interface User {
    int displayMenu() throws IOException, InterruptedException;
    void displayOptions(int choice) throws IOException, InterruptedException, ParseException;
}
