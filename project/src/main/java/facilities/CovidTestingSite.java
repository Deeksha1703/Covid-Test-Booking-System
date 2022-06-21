package facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import utilities.AccessToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


/**
 * Class that represents a Covid testing site
 */
public class CovidTestingSite {

    /**
     * ID of the covid testing site
     */
    private String id;

    /**
     * Description of the covid testing site
     */
    private String description;

    /**
     * Root URL for the web service
     */
    private static final String rootUrl = "https://fit3077.com/api/v2";

    /**
     * URL endpoint for all testing sites
     */
    String siteUrl = rootUrl + "/testing-site";

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
     * Constructor for CovidTestingSite class
     * @param id ID of the testing site
     */
    public CovidTestingSite(String id) {
        this.id = id;
    }

    public String getSiteId() {
        return id;
    }

    /**
     * Function to retrieve a covid test site using its site id through the api
     * @return an object node containing the covid test site as received from the api
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public ObjectNode getSite() throws IOException, InterruptedException {
        String finalSiteUrl = siteUrl + "/" + id;
        client = HttpClient.newHttpClient();
        request = HttpRequest
                .newBuilder(URI.create(finalSiteUrl))
                .setHeader("Authorization", AccessToken.token)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readValue(response.body(), ObjectNode.class);
    }

    /**
     * Function to get the description of a covid test site
     * @return Description of covid test site including its name, description, open/closed and waiting time
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public String getDescription() throws IOException, InterruptedException {
        ObjectNode site = this.getSite();
        description = "  " + site.get("name") + "\n     " + site.get("description") + "\n     " + "This site is currently: " + this.getState() + "\n     " + "Waiting time: " + getWaitingTime() + " hours";
        return description;
    }

    /**
     * Function to get the waiting time at the current covid testing site. Assumes each booking takes an hour
     * @return waiting time at the testing site
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public String getWaitingTime() throws IOException, InterruptedException {
        // Assumes each booking takes an hour
        String bookingsUrl = siteUrl + "/" + id +"?fields=bookings";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(bookingsUrl))
                .setHeader("Authorization", AccessToken.token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectNode testSite = new ObjectMapper().readValue(response.body(), ObjectNode.class);

        int bookingsNum = 0;

        bookingsNum = testSite.get("bookings").size();

        return Integer.toString(bookingsNum);


    }

    /**
     * Function to get the state of covid test site i.e. whether it is open or closed
     * @return State of the covid test site i.e. whether it is open or closed
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public String getState() throws IOException, InterruptedException {
        ObjectNode site = this.getSite();
        String open = site.get("additionalInfo").get("openTime").textValue();
        String close = site.get("additionalInfo").get("closeTime").textValue();
        LocalTime openTime = LocalTime.parse(open);
        LocalTime closeTime = LocalTime.parse(close);
        String now = DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now());
        LocalTime timeNow = LocalTime.parse(now);
        if(timeNow.isAfter(openTime) && timeNow.isBefore(closeTime)){
            return "Open";
        }
        else{
            return "Closed";
        }
    }

    /**
     * Function to get list of bookings
     * @return List of bookings
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public ObjectNode[] getBookings() throws IOException, InterruptedException {
        String bookingUrl = "https://fit3077.com/api/v2/booking";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(bookingUrl))
                .setHeader("Authorization", AccessToken.token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // so we can loop through the response
        return new ObjectMapper().readValue(response.body(), ObjectNode[].class);
    }
}
