package view;

import java.util.Scanner;

public class ReceptionistView {
    /**
     * Function to display menu to the user
     * @return the input taken by the user
     */
    public int menuSelection(){
        System.out.println("THIS IS THE MENU FOR RECEPTIONIST");
        Scanner sc = new Scanner(System.in);
        System.out.println("1) Book on site test");
        System.out.println("2) Check booking status via PIN");
        System.out.println("3) Verify QR Code for RAT kit collection");
        System.out.println("4) Check booking status via booking ID");
        System.out.println("5) Modify booking via booking ID");
        System.out.println("6) Cancel booking");
        System.out.println("7) Change booking to previous booking");
        System.out.println("8) View notifications");
        System.out.println("9) View a booking");
        System.out.println("10) Delete a booking");
        return sc.nextInt();
    }

    /**
     * Function for Receptionist to enter testing site Id, pin code and qr code
     * @param choice
     */
    public void menuOptions(int choice){
        switch (choice) {
            case 1:
                System.out.println("Please enter this testing site ID");
                break;
            case 2:
                System.out.println("Please enter the pin code: ");
                break;
            case 3:
                System.out.println("Please enter the QR code: ");
                break;
        }
    }
}
