package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import facilities.CovidTestingSite;
import facilities.SiteSearch;
import users.BookableUser;
import utilities.AccessToken;
import utilities.Question;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class for users to make a booking
 */
public class Booking {

    /**
     * ID of the user making the booking
     */
    private String userId;

    /**
     * ID of the covid testing site
     */
    private String siteId;

    /**
     * API key needed to access the web service
     */
    private static final String myApiKey = AccessToken.token;

    /**
     * Root URL for the web service
     */
    private static final String rootUrl = "https://fit3077.com/api/v2";

    /**
     * URL endpoint for all bookings
     */
    String bookingUrl = rootUrl + "/booking";

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

    private String bookingId;

    /**
     * List of Covid Testing Sites
     */
    private List<CovidTestingSite> options;

    /**
     * Covid testing site selected by the user
     */
    private CovidTestingSite selectedSite;

    /**
     * Constructor for Booking class
     * @param user A BookableUser (i.e., a user capable of making a booking) who is booking the test
     */
    public Booking(BookableUser user){
        this.userId = user.getBookingUserId();
        try{
            this.siteId = user.getSiteId();
        }
        catch(NullPointerException e){
            this.siteId = "";
        }

    }

    public String getBookingId() {
        return bookingId;
    }

    /**
     * Function to add the booking to the list of bookings on the api
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void addBooking() throws IOException, InterruptedException {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);

        String bookingJsonString = "{\n" +
                "  \"customerId\": \""+ userId + "\",\n" +
                "  \"testingSiteId\": \""+ siteId + "\",\n" +
                "  \"startTime\": \""+ strDate+ "\",\n" +
                "  \"notes\": \"string\",\n" +
                "  \"additionalInfo\": {\"modified\": " + "false" + "} " +
                "}";

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder(URI.create(bookingUrl))
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bookingJsonString))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectNode jsonNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
        bookingId = jsonNode.get("id").textValue();
    }

    /**
     * Adds a home booking with relevant information including RAT kit requirements to the api
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void addHomeBooking() throws IOException, InterruptedException {
        String qrCode = generateQR();
        String url = generateURL();

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);

        boolean ratKitRequired = Question.getInstance().interview("Would you like to collect your RAT kits from a test center ?");

        String ratKitStatus;

        if(ratKitRequired){
            ratKitStatus = "NOT RECEIVED";
        }
        else{
            ratKitStatus = "N/A";
        }

        String homeBookingJsonString = "{\n" +
                "  \"customerId\": \""+ userId + "\",\n" +
                "  \"testingSiteId\": \""+ siteId + "\",\n" +
                "  \"startTime\": \""+ strDate+ "\",\n" +
                "  \"notes\": \"string\",\n" +
                "  \"additionalInfo\": {\n" +
                "       \"homeTest\": true,\n" +
                "       \"ratKitRequired\":" + ratKitRequired + ",\n"+
                "       \"ratKitStatus\": \"" + ratKitStatus + "\",\n"+
                "       \"qrCode\": \"" + qrCode + "\",\n" +
                "       \"url\": \""+ url + "\"\n" +
                "       \"modified\": "+ "false" + "\n" +
                "  }\n" +
                "}";

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder(URI.create(bookingUrl))
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(homeBookingJsonString))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Your url to connect for testing is: " + url);
        System.out.println("Your QR code to receive a RAT kit from site (if required) is: " + qrCode);
    }

    /**
     * Function to generate a random string as a substitute for a QR Code
     * @return stringBuilder A random string representing the QR code
     */
    public String generateQR(){
        int n = 12;

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder stringBuilder = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            stringBuilder.append(AlphaNumericString
                    .charAt(index));
        }
        return stringBuilder.toString();
    }

    /**
     * Function to generate a URL
     * @return finalURL The URL that is generated
     */
    public static String generateURL() {
        int max = 1000000000;
        int min = 500;
        String rootURL = "@videoConferencing.com";
        Random r = new Random();
        int randomNumber = r.nextInt((max - min) + 1) + min;
        String s = Integer.toString(randomNumber);

        return s + rootURL;
    }

    /**
     * Function to get the status of a booking using the booking pin
     * @param pin pin generated after the booking
     * @param siteId site id of the chosen site
     * @param customerId Id of the customer
     * @param userID ID of the user
     * @return status of the booking
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public String getPinStatus(String pin, String siteId, String customerId, String userID) throws IOException, InterruptedException {
        CovidTestingSite covidSite = new CovidTestingSite(siteId);
        ObjectNode[] bookingNodes = covidSite.getBookings();
        String status = "Invalid Pin";
        for (ObjectNode node : bookingNodes) {
            if (Objects.equals(node.get("smsPin").textValue(), pin)) {
                status = node.get("status").textValue();
            }
        }
        return status;
    }

    /**
     * Function to check whether the booking ID is valid and return the booking status
     *
     * @param bookingId Booking ID, generated after the booking
     * @return status of the booking (i.e, initiated, processed)
     * @throws IOException          Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public String getBookingIdStatus(String bookingId, String siteId) throws IOException, InterruptedException {
        CovidTestingSite covidSite = new CovidTestingSite(siteId);
        ObjectNode[] bookingNodes = covidSite.getBookings();
        String status = "Invalid Booking ID";
        for (ObjectNode node : bookingNodes) {
            if (Objects.equals(node.get("id").textValue(), bookingId)) {
                status = node.get("status").textValue();
            }
        }
        return status;
    }

    /**
     * Function to allow the user to modify the booking venue and/or time
     * @param booking The booking to be modified
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     * @throws ParseException
     */
    public void modifyBooking(ObjectNode booking) throws IOException, InterruptedException, ParseException {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        String customerId = booking.get("customer").get("id").textValue();
        String bookingStatus = booking.get("status").textValue();
        String bookingStartTime = booking.get("startTime").textValue();

        String s = bookingStartTime.replace('T',' ');
        String finalString = s.substring(0,s.length() - 5);

        String currentTestingSite = booking.get("testingSite").get("name").textValue();
        String bookingUrl = "https://fit3077.com/api/v2/booking/";
        SiteSearch searchFunction = SiteSearch.getInstance();
        Scanner sc = new Scanner(System.in);

        if(strDate.compareTo(finalString) > 0 ) {
            System.out.println("Your booking time has lapsed. Please create a new booking.");
            return;
        }
        if (bookingStatus.equals("COMPLETED")) {
            System.out.println("Your test has already been conducted. Please create a new booking.");
        }
        else{
            boolean modifyTime = Question.getInstance().interview("Would you like to modify your booking time ?");
            boolean modifyVenue = Question.getInstance().interview("Would you like to modify your booking venue ?");
            if(modifyTime && !modifyVenue){
                changeTime(booking);
            }
            if(modifyVenue && !modifyTime){
                changeVenue(booking);
            }
            if(modifyTime && modifyVenue){
                System.out.println("Your current testing time is: " + bookingStartTime);
                Date newDate = inputDate();
                String finalDate = dateFormat.format(newDate);

                System.out.println("Your current testing venue is: " + currentTestingSite);
                System.out.println("You can choose a new testing venue by entering a suburb name");
                System.out.println("Please enter the suburb name: ");
                String suburb = sc.nextLine();
                options = searchFunction.searchSuburb(suburb);
                if (options.size() == 0) {
                    System.out.println("No sites were found");
                } else {
                    System.out.println("Select site to book:");
                    for (int i = 0; i < options.size(); i++) {
                        System.out.println((i + 1) + ") " + options.get(i).getDescription());
                    }
                    Scanner selection = new Scanner(System.in);
                    int siteChoice = selection.nextInt();
                    // **below is the test site instance selected. you can use this and the current resident instance to make a booking
                    selectedSite = options.get(siteChoice - 1);
                    String selectedSiteId = selectedSite.getSiteId();

                    String bookingJsonString = "{\n" +
                            "  \"customerId\": \""+ customerId + "\",\n" +
                            "  \"testingSiteId\": \""+ selectedSiteId + "\",\n" +
                            "  \"startTime\": \""+ finalDate+ "\",\n" +
                            "  \"notes\": \"string\",\n" +
                            "  \"additionalInfo\": {\"modified\": " + "true" + "} " +
                            "}";

                    client = HttpClient.newHttpClient();
                    request = HttpRequest.newBuilder(URI.create(bookingUrl))
                            .setHeader("Authorization", myApiKey)
                            .header("Content-Type","application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(bookingJsonString))
                            .build();

                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    ObjectNode jsonNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
                    String bookingId = jsonNode.get("id").textValue();

                    cancelBooking(booking);
                    System.out.println("Your booking has been successfully modified");
                    System.out.println("Your new booking id is: " + bookingId);
                }
            }
        }
    }

    /**
     * Function to modify the start time of the booking
     * @param booking The booking whose time is to be modified
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     * @throws ParseException Exception that occurs when you fail to parse a String that is ought to have a special format
     */
    public void changeTime(ObjectNode booking) throws IOException, InterruptedException, ParseException {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String customerId = booking.get("customer").get("id").textValue();
        String testingSiteId = booking.get("testingSite").get("id").textValue();
        String bookingStartTime = booking.get("startTime").textValue();
        String bookingUrl = "https://fit3077.com/api/v2/booking/";

        System.out.println("Your current testing time is: " + bookingStartTime);
        Date newDate = inputDate();
        String finalDate = dateFormat.format(newDate);

        String bookingJsonString = "{\n" +
                "  \"customerId\": \""+ customerId + "\",\n" +
                "  \"testingSiteId\": \""+ testingSiteId + "\",\n" +
                "  \"startTime\": \""+ finalDate+ "\",\n" +
                "  \"notes\": \"string\",\n" +
                "  \"additionalInfo\": {\"modified\": " + "true" + "} " +
                "}";

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder(URI.create(bookingUrl))
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bookingJsonString))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectNode jsonNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
        String bookingId = jsonNode.get("id").textValue();

        cancelBooking(booking);
        System.out.println("Your booking has been successfully modified");
        System.out.println("Your new booking id is: " + bookingId);

    }

    /**
     * Function to modify the venue of the booking
     * @param booking The booking whose venue is to be modified
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void changeVenue(ObjectNode booking) throws IOException, InterruptedException {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String customerId = booking.get("customer").get("id").textValue();
        String bookingStartTime = booking.get("startTime").textValue();
        String currentTestingSite = booking.get("testingSite").get("name").textValue();
        String bookingUrl = "https://fit3077.com/api/v2/booking/";
        SiteSearch searchFunction = SiteSearch.getInstance();
        Scanner sc = new Scanner(System.in);

        System.out.println("Your current testing venue is: " + currentTestingSite);
        System.out.println("You can choose a new testing venue by entering a suburb name : ");
        System.out.println("Please enter the suburb name: ");
        String suburb = sc.nextLine();
        options = searchFunction.searchSuburb(suburb);
        if (options.size() == 0) {
            System.out.println("No sites were found");
        } else {
            System.out.println("Select site to book:");
            for (int i = 0; i < options.size(); i++) {
                System.out.println((i + 1) + ") " + options.get(i).getDescription());
            }
            Scanner selection = new Scanner(System.in);
            int siteChoice = selection.nextInt();
            // **below is the test site instance selected. you can use this and the current resident instance to make a booking
            selectedSite = options.get(siteChoice - 1);
            String selectedSiteId = selectedSite.getSiteId();

            String bookingJsonString = "{\n" +
                    "  \"customerId\": \""+ customerId + "\",\n" +
                    "  \"testingSiteId\": \""+ selectedSiteId + "\",\n" +
                    "  \"startTime\": \""+ bookingStartTime+ "\",\n" +
                    "  \"notes\": \"string\",\n" +
                    "  \"additionalInfo\": {\"modified\": " + "true" + "} " +
                    "}";

            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder(URI.create(bookingUrl))
                    .setHeader("Authorization", myApiKey)
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bookingJsonString))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectNode jsonNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
            String bookingId = jsonNode.get("id").textValue();

            cancelBooking(booking);
            System.out.println("Your booking has been successfully modified");
            System.out.println("Your new booking id is: " + bookingId);
        }
    }

    /**
     * Function to get date and time input from the user
     * @return Date entered by the user
     * @throws ParseException Exception that occurs when you fail to parse a String that is ought to have a special format
     */
    public Date inputDate() throws ParseException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the date and time [yyyy-MM-dd hh:mm:ss]: ");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateInput = sc.nextLine();
        Date date = null;
        if (null != dateInput && dateInput.trim().length() > 0) {
            date = dateFormat.parse(dateInput);
        }
        return date;
    }

    /**
     * Function to cancel a booking
     * @param booking Booking to be cancelled
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void cancelBooking(ObjectNode booking) throws IOException, InterruptedException {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        String bookingStatus = booking.get("status").textValue();
        String customerId = booking.get("customer").get("id").textValue();
        String testingSiteId = booking.get("testingSite").get("id").textValue();
        String bookingId = booking.get("id").textValue();
        String bookingStartTime = booking.get("startTime").textValue();
        String bookingUrl = "https://fit3077.com/api/v2/booking/" + bookingId;

        if(strDate.compareTo(bookingStartTime) > 0 ) {
            System.out.println("Your booking time has lapsed. Please create a new booking.");
            return;
        }
        if (bookingStatus.equals("COMPLETED")) {
            System.out.println("Your test has already been conducted. Please create a new booking.");
            return;
        }
        else{
            String bookingString = "{\n" +
                    "  \"customerId\": \"" + customerId + "\",\n" +
                    "  \"testingSiteId\": \"" + testingSiteId + "\",\n" +
                    "  \"startTime\": \"" + strDate + "\",\n" +
                    "  \"status\": \"CANCELLED\",\n" +
                    "  \"notes\": \"Booking has been cancelled\",\n" +
                    "  \"additionalInfo\": {\"modified\": " + "true" + "} " +
                    "}";

            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder(URI.create(bookingUrl)) // Return a JWT so we can use it in Part 5 later.
                    .setHeader("Authorization", AccessToken.token)
                    .header("Content-Type","application/json") // This header needs to be set when sending a JSON request body.
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(bookingString))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    /**
     * Function to change current booking to previous booking
     * @param currentBooking The current booking
     * @param pastBooking The previous booking that we need to modify
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void changeToPreviousBooking(ObjectNode currentBooking, ObjectNode pastBooking) throws IOException, InterruptedException {
        String pastBookingId = pastBooking.get("id").textValue();
        String pastBookingCustomerId = pastBooking.get("customer").get("id").textValue();
        String currentBookingStatus = currentBooking.get("status").textValue();
        String pastBookingStatus = pastBooking.get("status").textValue();
        String pastBookingStartTime = pastBooking.get("startTime").textValue();
        String currentBookingStartTime = currentBooking.get("startTime").textValue();
        String pastTestSiteId = pastBooking.get("testingSite").get("id").textValue();
        String pastBookingUrl = "https://fit3077.com/api/v2/booking/" + pastBookingId;
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);

        if (currentBookingStatus.equals("COMPLETED") || pastBookingStatus.equals("COMPLETED")) {
            System.out.println("Your test has already been conducted. Please create a new booking.");
        }

        if(strDate.compareTo(pastBookingStartTime) > 0 || strDate.compareTo(currentBookingStartTime) > 0 ) {
            System.out.println("Your booking time has lapsed. Please create a new booking.");
            return;
        }
        cancelBooking(currentBooking);

        String pastBookingString = "{\n" +
                "  \"customerId\": \"" + pastBookingCustomerId + "\",\n" +
                "  \"testingSiteId\": \"" + pastTestSiteId + "\",\n" +
                "  \"startTime\": \"" + pastBookingStartTime + "\",\n" +
                "  \"status\": \"INITIATED\",\n" +
                "  \"notes\": \"\",\n" +
                "  \"additionalInfo\": {\"modified\": " + "true" + "} " +
                "}";

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder(URI.create(pastBookingUrl)) // Return a JWT so we can use it in Part 5 later.
                .setHeader("Authorization", AccessToken.token)
                .header("Content-Type","application/json") // This header needs to be set when sending a JSON request body.
                .method("PATCH", HttpRequest.BodyPublishers.ofString(pastBookingString))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Your booking has been successfully modified");
    }

    public String display_booking_description(String bookingId) throws IOException, InterruptedException {
        String bookingUrl = "https://fit3077.com/api/v2/booking/" + bookingId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(bookingUrl))
                .setHeader("Authorization", AccessToken.token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectNode booking = new ObjectMapper().readValue(response.body(), ObjectNode.class);
        String description = "Booking ID: " + bookingId + "\n" +
                "Customer ID: " + booking.get("customer").get("id").textValue() + "\n" +
                "Customer Name: " + booking.get("customer").get("givenName").textValue() + " "+booking.get("customer").get("familyName").textValue() + "\n" +
                "Testing Site: " + booking.get("testingSite").get("name").textValue() + "\n" +
                "Booking Status: " + booking.get("status").textValue()+ "\n" +
                "Last Updated: " + booking.get("updatedAt").textValue();

        return description;
    }

    public void delete_booking(String bookingId) throws IOException, InterruptedException {
        String bookingUrl = "https://fit3077.com/api/v2/booking/" + bookingId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(bookingUrl))
                .setHeader("Authorization", AccessToken.token)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Booking successfully deleted");
    }

}

