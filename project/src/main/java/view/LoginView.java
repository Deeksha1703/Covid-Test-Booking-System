package view;

import java.util.Scanner;

public class LoginView {
    /**
     * Displays the menu shown to all users
     * @return integer representing the choice made by the user
     */
    public int menuSelection() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--------------------------");
        System.out.println("   COVID-19 Booking Site   ");
        System.out.println("--------------------------");
        System.out.println("To continue, please login by selecting 1");
        return scanner.nextInt();
    }
}
