package users;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.*;

import controller.ReceptionistController;
import view.ReceptionistView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import booking.Booking;
import facilities.OnSiteTesting;
import utilities.AccessToken;

/**
 * Receptionist class for users.Receptionist who can book on site tests, check booking status or give an RAT kit
 */
public class Receptionist extends ValidateUser implements User, BookableUser {

    /**
     * User ID of the Receptionist
     */
    private final String userID;

    /**
     * ID the user for which the receptionist is making a booking for
     */
    private String customerId;

    /**
     * ID of the site to be booked
     */
    private String siteId;

    /**
     * Instance of ReceptionistController class
     */
    private ReceptionistController receptionistController;

    /**
     * Instance of ReceptionistView class
     */
    private ReceptionistView receptionistView;

    /**
     * API key needed to access the web service
     */
    private static final String myApiKey = AccessToken.token;

    /**
     * Http client which can be used to send requests and retrieve their response
     */
    private HttpClient client;

    /**
     * HttpRequest to send requests
     */
    private HttpRequest request;

    /**
     * HttpResponse to retrieve responses
     */
    private HttpResponse<String> response;

    /**
     * Constructor for Receptionist class
     *
     * @param userID - User ID of the users.Receptionist
     */
    public Receptionist(String userID) {
        this.userID = userID;
        receptionistView = new ReceptionistView();
        receptionistController = new ReceptionistController(receptionistView);
    }

    /**
     * Function to display the menu with options for the users.Receptionist
     */
    @Override
    public int displayMenu() {
        return receptionistController.displayMenu();
    }

    /**
     * Function to create a new booking for the customer
     */
    @Override
    public void makeBooking() {
        Booking booking = new Booking(this);
        try {
            booking.addBooking();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: The booking cannot be made. Invalid user or test site ID");
        }
        System.out.println("Your booking has been created successfully");
        System.out.println("You will receive your PIN number shortly");
    }

    /**
     * Accessor to return the testing site's ID
     * @return siteID ID of the testing site
     */
    @Override
    public String getSiteId() {
        return siteId;
    }

    /**
     * Accessor to return the user ID of the resident for whom the booking is done
     *
     * @return customerID ID of the resident
     */
    @Override
    public String getBookingUserId() {
        return customerId;
    }

    /**
     * Function to call the respective functions depending on the choice made by the user in the displayMenu method and ask any more relevant questions
     *
     * @param choice choice made by the user from the previous menu
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void displayOptions(int choice) throws IOException, InterruptedException, ParseException {
        Scanner sc = new Scanner(System.in);
        receptionistView.menuOptions(choice);
        Booking booking = new Booking(this);
        switch (choice) {

            case 1:
                String possibleSiteId = sc.nextLine();
                if (validateSiteId(possibleSiteId)) {
                    siteId = possibleSiteId;
                    customerId = getUser().get(1);
                    makeBooking();
                }
                break;
            case 2:
                String pinCode = sc.nextLine();
                System.out.println("Booking Status: " + booking.getPinStatus(pinCode, siteId, customerId, userID));
                break;
            case 4:
                System.out.println("Please enter the booking ID: ");
                String bookingId = sc.nextLine();
                System.out.println("Booking Status: " + booking.getBookingIdStatus(bookingId, siteId));
                break;

            case 3:
                System.out.println("Please enter the QR code: ");
                String qrCode = sc.nextLine();
                boolean valid = validateQrCode(qrCode);
                if (valid) {
                    System.out.println("QR code valid, please collect your RAT kit");
                } else {
                    System.out.println("Invalid QR code");
                }
                break;
            case 5:
                System.out.println("Please enter the booking ID");
                String bookingID = sc.nextLine();
                ObjectNode old_booking = getBookingById(bookingID);
                String customerId = old_booking.get("customer").get("id").textValue();
                System.out.println("Please enter the customer ID: ");
                String id = sc.nextLine();
                if(customerId.equalsIgnoreCase(id)) {
                    booking.modifyBooking(old_booking);
                }
                else{
                    System.out.println("You have entered an incorrect customer ID");
                    return;
                }
                break;
            case 6:
                System.out.println("Please enter the booking ID");
                String ID = sc.nextLine();
                ObjectNode cancelBooking = getBookingById(ID);
                String customerID = cancelBooking.get("customer").get("id").textValue();
                System.out.println("Please enter the customer ID: ");
                String cID= sc.nextLine();
                if(customerID.equalsIgnoreCase(cID)) {
                    booking.cancelBooking(cancelBooking);
                }
                else{
                    System.out.println("You have entered an incorrect customer ID");
                    return;
                }
                break;
            case 7:
                System.out.println("Please enter the current booking ID: ");
                String currentBookingID = sc.nextLine();
                ObjectNode currentBooking = getBookingById(currentBookingID);
                System.out.println("Please enter the past booking ID: ");
                String pastBookingID = sc.nextLine();
                ObjectNode pastBooking = getBookingById(pastBookingID);
                booking.changeToPreviousBooking(currentBooking,pastBooking);
                break;
            case 8:
                System.out.println("Please enter your test site ID");
                String notifId = sc.nextLine();
                System.out.println("Please find all new cancellations and modified bookings made: ");
                System.out.println("---------------------------------------------------------------------");
                view_notifs(notifId);
                break;
            case 9:
                System.out.println("Please enter the booking id");
                String viewBookingId = sc.nextLine();
                System.out.println(booking.display_booking_description(viewBookingId));
                break;
            case 10:
                System.out.println("Please enter the booking id");
                String deleteBookingId = sc.nextLine();
                booking.delete_booking(deleteBookingId);

        }
    }

    public void view_notifs(String notifSiteId) throws IOException, InterruptedException {
        // siteId
        // obtaining all bookings
        String bookingUrl = "https://fit3077.com/api/v2/booking";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(bookingUrl))
                .setHeader("Authorization", AccessToken.token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectNode[] bookingNodes = new ObjectMapper().readValue(response.body(), ObjectNode[].class);
        int index = 1;
        for (ObjectNode node: bookingNodes) {

            // if booking was made at this testing site
            String siteId = node.get("testingSite").get("id").textValue();
            if (Objects.equals(siteId, notifSiteId)){
                // and if booking was modified
                try{
                    if (node.get("additionalInfo").get("modified").booleanValue()){
                        Booking booking = new Booking(this);
                        System.out.println(index + ")" +booking.display_booking_description(node.get("id").textValue()));
                        index++;
                        System.out.println("---------------------------------------------------------------------");
                    }
                } catch(NullPointerException ignored){

                }

            }

        }
    }

    /**
     * Function to validate the QR code provided by the resident
     * @param qrCode QR code provided by the resident
     * @return true/false depending on if the qr code is valid or not
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public boolean validateQrCode(String qrCode) throws IOException, InterruptedException {
        OnSiteTesting onSite = new OnSiteTesting(siteId, customerId, userID);
        ObjectNode[] bookingNodes = onSite.getBookings();
        boolean validity = false;
        for (ObjectNode node : bookingNodes) {
            if (node.get("testingSite").get("additionalInfo").get("homeTesting").booleanValue()) {
                try {
                    if (Objects.equals(node.get("additionalInfo").get("qrCode").textValue(), qrCode)) {
                        validity = true;
                        updateRATState(node);
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }
        return validity;
    }

    /**
     * This function updated the state of the RAT kit in the user's booking (i.e., from NOT RECEIVED TO RECEIVED)
     * @param booking the booking from the api
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void updateRATState(ObjectNode booking) throws IOException, InterruptedException {
        String actualQrCode = booking.get("additionalInfo").get("qrCode").textValue();
        String bookingId = booking.get("id").textValue();
        String ratUrl = booking.get("additionalInfo").get("url").textValue();

        String bookingUrl = "https://fit3077.com/api/v2/booking/" + bookingId;

        String bookingString = "{\n" +
                "  \"additionalInfo\": {\n" +
                "      \"homeTest\": true,\n" +
                "      \"ratKitRequired\": true,\n" +
                "      \"ratKitStatus\": \"RECEIVED\",\n" +
                "      \"qrCode\": \"" + actualQrCode + "\",\n" +
                "      \"url\": \"" + ratUrl + "\"\n" +
                "   }\n" +
                "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(bookingUrl)) // Return a JWT, so we can use it in Part 5 later.
                .setHeader("Authorization", AccessToken.token)
                .header("Content-Type", "application/json") // This header needs to be set when sending a JSON request body.
                .method("PATCH", HttpRequest.BodyPublishers.ofString(bookingString))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Function to get all the covid test sites from the api
     * @return a list of all testing site returned by the api
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public ObjectNode[] getTestSites() throws IOException, InterruptedException {
        String siteUrl = "https://fit3077.com/api/v2/testing-site";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(siteUrl))
                .setHeader("Authorization", AccessToken.token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // so we can loop through the response
        return new ObjectMapper().readValue(response.body(), ObjectNode[].class);
    }

    /**
     * Function to check whether the site ID is valid
     *
     * @param siteId ID of the testing site that needs to be validated
     * @return true if the site ID is valid, else false
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public boolean validateSiteId(String siteId) throws IOException, InterruptedException {
        ObjectNode[] siteNodes = getTestSites();

        boolean siteValid = false;
        for (ObjectNode node : siteNodes) {
            if (Objects.equals(node.get("id").textValue(), siteId)) {
                siteValid = true;
            }
        }
        return siteValid;
    }

    /**
     * Function to check if the user exists by taking the username as input and if so, return the user type and ID
     * @return the type of user (i.e., users.Resident, users.Receptionist, users.HealthCareWorker) and the user's ID
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    @Override
    public ArrayList<String> getUser() throws IOException, InterruptedException {
        System.out.println("Please input the customer's username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        boolean validity = this.returnUser(username);

        if (validity) {
            System.out.println("User Verified");
            return userType(this.user);
        } else {
            throw new IOException("Username incorrect");
        }
    }

    /**
     * Function to return the booking object using the booking ID
     * @param bookingId ID of the booking
     * @return booking object
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public ObjectNode getBookingById(String bookingId)throws IOException, InterruptedException {
        String bookingUrl = "https://fit3077.com/api/v2/booking/"+bookingId;
        client = HttpClient.newHttpClient();
        request = HttpRequest
                .newBuilder(URI.create(bookingUrl))
                .setHeader("Authorization", myApiKey)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectNode jsonNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
        return jsonNode;
    }
    /**
     * Function to check whether the pin is valid and return the booking status
     * @param pin pin code generated after the booking
     * @return status of the booking (i.e, initiated, processed)
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public ObjectNode getBookingByPin(String pin)throws IOException, InterruptedException {
        OnSiteTesting onSite = new OnSiteTesting(siteId, customerId, userID);
        ObjectNode[] bookingNodes = onSite.getBookings();
        for (ObjectNode node : bookingNodes) {
            if (Objects.equals(node.get("smsPin").textValue(), pin)) {
                return node;
            }
            else{
                System.out.println("Pin code is invalid");
                return null;
            }
        }
        return null;
    }

    /**
     * Function to check whether the booking ID is valid and return the booking status
     *
     * @param bookingId Booking ID, generated after the booking
     * @return status of the booking (i.e, initiated, processed)
     * @throws IOException          Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public String getBookingStatus(String bookingId) throws IOException, InterruptedException {
        OnSiteTesting onSite = new OnSiteTesting(siteId, customerId, userID);
        ObjectNode[] bookingNodes = onSite.getBookings();
        String status = "Invalid Booking ID";
        for (ObjectNode node : bookingNodes) {
            if (Objects.equals(node.get("id").textValue(), bookingId)) {
                status = node.get("status").textValue();
            }
        }
        return status;
    }
}