package view;

import java.util.Scanner;

public class ResidentView {

    /**
     * Function to display menu to teh Resident
     * @return the selection made by the resident
     */
    public int searchMenu(){
        System.out.println("What would you like to do?");
        Scanner sc = new Scanner(System.in);
        System.out.println("1) Search for testing site using suburb name");
        System.out.println("2) Search for Drive Through testing sites");
        System.out.println("3) Search for Walk-In testing sites");
        System.out.println("4) Search for testing site clinics");
        System.out.println("5) Search for testing site hospitals");
        System.out.println("6) Search for testing sites requiring GP referral");
        System.out.println("7) Search and register for home testing");
        System.out.println("8) Check booking status via pin code");
        System.out.println("9) Check booking status via booking ID");
        System.out.println("10) Modify booking via booking ID");
        System.out.println("11) Cancel booking");
        System.out.println("12) Change booking to previous booking");
        System.out.println("13) View all active bookings");
        int selection = sc.nextInt();
        return selection;
    }

    /**
     * Function for the Resident to enter suburb name
     */
    public void searchSuburb(){
        System.out.println("Please enter the suburb name: ");
    }
}
