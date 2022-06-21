package users;

import controller.HealthWorkerController;

import facilities.OnSiteTesting;
import view.HealthWorkerView;


import java.io.IOException;
import java.util.Scanner;

/**
 * Class for Healthcare Worker who can conduct interviews on the resident and then determine the covid test type
 */
public class HealthCareWorker extends ValidateUser implements User {
    /**
     * ID of the healthcare worker
     */
    private String userID;

    private HealthWorkerView healthWorkerView;
    private HealthWorkerController healthWorkerController;

    /**
     * Constructor for the users.HealthCareWorker class
     * @param userID ID of the users.HealthCareWorker
     */
    public HealthCareWorker(String userID) {
        this.userID = userID;
        healthWorkerView = new HealthWorkerView();
        healthWorkerController = new HealthWorkerController(healthWorkerView);
    }

    /**
     * Function to display the menu with options for the Health Care worker
     */
    @Override
    public int displayMenu() {
        return healthWorkerController.displayMenu();
    }

    /**
     * Function to call the respective functions depending on the choice made by the user in the displayMenu method and also ask any follow-up questions
     * @param choice integer choice selected from the menu
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void displayOptions(int choice) throws IOException, InterruptedException {
        if(choice == 1){
            Scanner sc = new Scanner(System.in);
            healthWorkerView.enterSiteId();
            String siteId = sc.nextLine();
            healthWorkerView.enterPin();
            String pinCode = sc.nextLine();
            OnSiteTesting onSite = new OnSiteTesting(siteId, pinCode, userID);
            onSite.onSiteTestForm();
        }
    }
}
