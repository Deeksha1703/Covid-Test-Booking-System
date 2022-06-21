package view;

import java.util.Scanner;

public class HealthWorkerView {

    /**
     * Function to display menu to the healthcare worker
     * @return selection made by the user
     */
    public int menuSelection(){
        System.out.println("THIS IS THE MENU FOR HEALTH CARE WORKER");
        Scanner sc = new Scanner(System.in);
        System.out.println("1) Conduct interview to determine COVID test type to be taken");
        int selection = sc.nextInt();
        return selection;
    }

    /**
     * Function for user to enter the testing site Id
     */
    public void enterSiteId(){
        System.out.println("Please enter the current test site ID: ");
    }

    /**
     * Function for the user to enter the customer's pin
     */
    public void enterPin(){
        System.out.println("Please enter the customer's PIN: ");
    }
}
