package users;

import controller.ResidentController;
import model.ResidentModel;
import view.ResidentView;
import booking.Booking;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import facilities.CovidTestingSite;
import utilities.AccessToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


/**
 * Class representing the Resident
 */
public class Resident extends ValidateUser implements User, BookableUser {

    /**
     * User ID of the Resident
     */
    private final String userID;
    /**
     * Represents if the User wants to do a home test or not
     */
    boolean homeTest = false;

    /**
     * List of Covid Testing Sites
     */
    private List<CovidTestingSite> options;

    /**
     * Covid testing site selected by the user
     */
    private CovidTestingSite selectedSite;


    /**
     * ID the user for which the receptionist is making a booking for
     */
    private String customerId;

    /**
     * ID of the site to be booked
     */
    private String siteId;

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
     * Instance of ResidentController class
     */
    private ResidentController residentController;

    /**
     * Instance of ResidentModel class
     */
    private ResidentModel residentModel;

    /**
     * Instance of ResidentView class
     */
    private ResidentView residentView;

    /**
     * Constructor for the users.Resident class
     *
     * @param userID User ID of the users.Resident
     */
    public Resident(String userID) {
        this.userID = userID;
        residentView = new ResidentView();
        residentModel = new ResidentModel();
        residentController = new ResidentController(residentView, residentModel);
    }

    /**
     * Function to display the menu with options for the users.Resident
     */
    @Override
    public int displayMenu() throws IOException, InterruptedException {
        int selection = residentController.displaySearchMenu();
        return selection;
    }

    /**
     * Function to call the respective functions depending on the choice made by the user in the displayMenu method and ask any follow-up questions
     * @param choice choice made by the user from the previous menu
     * @throws IOException          Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void displayOptions(int choice) throws IOException, InterruptedException, ParseException {
        Scanner sc = new Scanner(System.in);
        if(choice != 8 && choice != 9 && choice != 10 && choice != 11 && choice != 12 && choice != 13){
            try{
                selectedSite = residentController.displaySiteOptions(choice);
                makeBooking();
            }
            catch(IllegalArgumentException e){
                System.out.println("No Sites Found");
            }
        }
        else{
            Booking booking = new Booking(this);
            switch(choice){
                case 8:
                    System.out.println("Please enter the pin code");
                    String pinCode = sc.nextLine();
                    System.out.println("Your booking status is: " + booking.getPinStatus(pinCode, siteId, customerId, userID));
                    break;
                case 9:
                    System.out.println("Please enter the booking ID");
                    String bookingId = sc.nextLine();
                    System.out.println("Your booking status is: " + booking.getBookingIdStatus(bookingId, siteId));
                case 10:
                    System.out.println("Please enter the booking ID");
                    String bookingID = sc.nextLine();
                    ObjectNode oldBooking = getBookingById(bookingID);
                    booking.modifyBooking(oldBooking);
                    break;
                case 11:
                    System.out.println("Please enter the booking ID");
                    String ID = sc.nextLine();
                    ObjectNode Booking = getBookingById(ID);
                    booking.cancelBooking(Booking);
                    break;
                case 12:
                    System.out.println("Please enter the current booking ID: ");
                    String currentBookingID = sc.nextLine();
                    ObjectNode currentBooking = getBookingById(currentBookingID);
                    System.out.println("Please enter the past booking ID: ");
                    String pastBookingID = sc.nextLine();
                    ObjectNode pastBooking = getBookingById(pastBookingID);
                    booking.changeToPreviousBooking(currentBooking,pastBooking);
                    break;
                case 13:
                    System.out.println("Please enter your customer ID: ");
                    String customerId = sc.nextLine();
                    getActiveBookings(customerId);
                    break;
                default:
                    System.out.println("Incorrect option selected");
                    break;
            }
        }
    }

    /**
     * Function for the user to make the booking and add it to the api
     */
    @Override
    public void makeBooking() {
        Booking booking = new Booking(this);
        try {
            if (!homeTest) {
                booking.addBooking();
            } else {
                booking.addHomeBooking();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: The booking cannot be made. Invalid user or test site ID");
        }
        System.out.println("Your booking has been created successfully !");
    }

    /**
     * Accessor to get the ID of the covid test site
     * @return ID of covid test site
     */
    @Override
    public String getSiteId() {
        return selectedSite.getSiteId();
    }

    /**
     * Accessor to get the ID of the user for whom the booking is made
     * @return ID of the user
     */
    @Override
    public String getBookingUserId() {
        return userID;
    }

    /**
     * Function to check the user's validity from their username and get their details
     * @return user whose username is entered
     * @throws IOException          Exception that occurs when an IO operation fails
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

    public void getActiveBookings(String customerId) throws IOException, InterruptedException {
        ArrayList<Integer> bookingIds = new ArrayList<Integer>();
        String usersIdUrl = "https://fit3077.com/api/v2/booking";
        client = HttpClient.newHttpClient();
        request = HttpRequest
                .newBuilder(URI.create(usersIdUrl))
                .setHeader("Authorization", myApiKey)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectNode[] bookingNodes = new ObjectMapper().readValue(response.body(), ObjectNode[].class);
        int index = 0;
        for(ObjectNode booking: bookingNodes){
            String bookingId = booking.get("customer").get("id").textValue();
            if (Objects.equals(bookingId, customerId)){
                if (!Objects.equals(booking.get("status").textValue(), "CANCELLED")){
                    index += 1;
                    Booking newBooking = new Booking(this);
                    String stringAns = newBooking.display_booking_description(booking.get("id").textValue());
                    System.out.println(index + ") " + stringAns);
                }

            }
        }
//        System.out.println(response.body());

        /**
         ObjectNode jsonNodes = new ObjectMapper().readValue(response.body(), ObjectNode.class);
         ObjectNode[] array = new ObjectNode[0];
         for (JsonNode node : jsonNodes) {
         if (!Objects.equals(node.get("status").textValue(), "CANCELLED")) {
         }

         }


         }
         */
    }
}

