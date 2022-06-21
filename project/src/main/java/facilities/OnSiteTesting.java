package facilities;

import com.fasterxml.jackson.databind.node.ObjectNode;
import utilities.AccessToken;
import utilities.Question;
import utilities.SymptomsCollection;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Class representing On Site Testing to be used by Health Care worker to conduct relevant functions
 * such as to conduct an interview on the user, fill a form on the system, and
 * suggest the appropriate covid 19 test to be taken, ie either PCR or ART test
 */
public class OnSiteTesting extends CovidTestingSite {

    /**
     * SMS pin code held by the user for their booking
     */
    private final String pinCode;

    /**
     * ID of the healthcare worker
     */
    private final String adminId;

    /**
     * Constructor for CovidTestingSite class
     * @param siteId ID of the testing site
     * @param pinCode SMS pincode of the user
     * @param adminId ID of the users.HealthCareWorker
     */
    public OnSiteTesting(String siteId, String pinCode, String adminId) {
        super(siteId);
        this.pinCode = pinCode;
        this.adminId = adminId;
    }

    /**
     * Function to check if the resident has specific symptoms
     * @param symptom Symptom that we need to check if the resident has
     * @return symptoms number of symptoms the resident has
     */
    private Integer checkSymptoms(String symptom) {
        int symptoms;
        boolean resp = Question.getInstance().interview("Does the Resident have " + symptom + "?");
        if(resp){
            symptoms = 1;
        }
        else{
            symptoms = 0;
        }
        return symptoms;
    }

    /**
     * Function to check the level of contact the resident has had with a covid 19 case
     * @return Map containing the level of contact, and it's corresponding description
     */
    private Map<Integer, String> levelOfContact() {
        Map<Integer, String> contact = new HashMap<>();
        contact.put(1, "Resident has been in contact with a confirmed COVID-19 case");
        contact.put(2, "Resident has been in contact with suspected COVID-19 case");
        contact.put(3, "Resident has been in contact with a COVID-19 case");
        return contact;
    }

    /**
     * Function to recommend the type of covid test that the resident should take
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void onSiteTestForm() throws IOException, InterruptedException {
        System.out.println("FORM FOR ON SITE TESTING");
        boolean overseasTravel = false;
        boolean RATPositive = false;
        boolean submitForm = false;
        int levelOfContact = 0;
        int mildSymptoms = 0;
        int moderateSymptoms = 0;
        int severeSymptoms = 0;
        List<String> mildSymptomsList = SymptomsCollection.getInstance().getMildSymptoms();
        List<String> moderateSymptomsList = SymptomsCollection.getInstance().getModerateSymptoms();
        List<String> severeSymptomsList = SymptomsCollection.getInstance().getSevereSymptoms();

        try {
            while (!submitForm) {
                overseasTravel = Question.getInstance().interview("Have you travelled overseas in the last 7 days ?");
                RATPositive = Question.getInstance().interview("Have you received a positive RAT test result in the last 7 days ?");
                while (levelOfContact <= 0 || levelOfContact > 3) {
                    System.out.println("The level of contact of the resident has been: ");
                    for (Entry<Integer, String> entry : levelOfContact().entrySet()) {
                        System.out.println(entry.getKey() + ":" + entry.getValue());
                    }
                    Scanner sc = new Scanner(System.in);
                    levelOfContact = sc.nextInt();
                    if (levelOfContact <= 0 || levelOfContact > 3) {
                        System.out.println("Please enter a valid level of contact");
                    }
                }
                System.out.println("Which of the following symptoms does the resident have ? ");
                for (String s : mildSymptomsList) {
                    mildSymptoms = mildSymptoms + checkSymptoms(s);
                }
                for (String s : moderateSymptomsList) {
                    moderateSymptoms = moderateSymptoms + checkSymptoms(s);
                }
                for (String s : severeSymptomsList) {
                    severeSymptoms = severeSymptoms + checkSymptoms(s);
                }
                submitForm = Question.getInstance().interview("Are you sure you want to submit the form ?");
                if(!submitForm) {
                    return;
                }
            }
        }
        catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        String test;
        if(RATPositive || overseasTravel || levelOfContact == 1 || severeSymptoms > 0 || moderateSymptoms > 2 || mildSymptoms == 4){
            test = "PCR";
        }
        else{
            test = "RAT";
        }
        ArrayList<String> userAndBookingId = getUserAndBookingIdWithPin(pinCode);  // users id + booking id
        addTestType(userAndBookingId.get(0), userAndBookingId.get(1), test);
        if(test.equals("PCR")) {
            System.out.println("The System recommends that the Resident should take a " + test + " test");
        }
        else{
            System.out.println("The System recommends that the Resident should take an " + test + " test");
        }
    }

    /***
     * Function to get the User and Booking ID from the pin code generated
     * @param pin The pin code of the booking
     * @return users.User's ID and booking ID
     */
    public ArrayList<String> getUserAndBookingIdWithPin(String pin) throws IOException, InterruptedException {

        String userId;
        String bookingId;
        ArrayList<String> pinInfo = new ArrayList<>();
        ObjectNode[] bookingNodes = getBookings();
        boolean validity = false;
        for (ObjectNode node: bookingNodes) {
            if (Objects.equals(node.get("smsPin").textValue(), pin)){
                userId = node.get("customer").get("id").textValue();
                pinInfo.add(userId);
                bookingId = node.get("id").textValue();
                pinInfo.add(bookingId);
                validity = true;
            }
        }
        if(!validity){
            throw new IllegalArgumentException("Invalid PIN");
        }
        else{
            return pinInfo;
        }
    }

    /**
     * Function to add the test type to the covid-test field of the Booking object as well as change status of the booking to PROCESSED
     * @param userId ID of the resident
     * @param bookingId ID of the booking
     * @param testType test type recommended by the system
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void addTestType(String userId, String bookingId, String testType) throws IOException, InterruptedException {
        String testUrl = "https://fit3077.com/api/v2/covid-test";

        String testString = "{\n" +
                "  \"type\": \""+ testType + "\",\n" +
                "  \"patientId\": \"" + userId + "\",\n" +
                "  \"administererId\": \"" + adminId + "\",\n" +
                "  \"bookingId\": \"" + bookingId + "\",\n" +
                "  \"result\": \"PENDING\",\n" +
                "  \"status\": \"NOT INITIATED\",\n" +
                "  \"notes\": \"Test type recommended\",\n" +
                "  \"additionalInfo\": {}\n" +
                "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(testUrl)) // Return a JWT so we can use it in Part 5 later.
                .setHeader("Authorization", AccessToken.token)
                .header("Content-Type","application/json") // This header needs to be set when sending a JSON request body.
                .POST(HttpRequest.BodyPublishers.ofString(testString))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        // patch booking status to PROCESSED
        String bookingUrl = "https://fit3077.com/api/v2/booking/" + bookingId;

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);

        String bookingString = "{\n" +
                "  \"customerId\": \"" + userId + "\",\n" +
                "  \"testingSiteId\": \"" + getSiteId() + "\",\n" +
                "  \"startTime\": \"" + strDate + "\",\n" +
                "  \"status\": \"PROCESSED\",\n" +
                "  \"notes\": \"Test type recommended\",\n" +
                "  \"additionalInfo\": {}\n" +
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
