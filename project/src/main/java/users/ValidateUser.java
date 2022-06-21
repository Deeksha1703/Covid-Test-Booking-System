package users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import utilities.AccessToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

/**
 * Class for different Users to login/ validate users for the COVID Booking and Testing System
 */
public class ValidateUser {

    /**
     * The User to be validated/ who is logging in to the system
     */
    public ObjectNode user;
    /**
     * Access Token to access the api
     */
    private static final String myApiKey = AccessToken.token;   // note access token is saved in a separate file named AccessToken

    /**
     * Root URL for the web service
     */
    private static final String rootUrl = "https://fit3077.com/api/v2";

    /**
     * URL of endpoint for all users
     */
    String usersUrl = rootUrl + "/user";

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
     * Function for user to log in to the system
     * @return userType an ArrayList with the type of user logging in and the userID
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public ArrayList<String> getUser() throws IOException, InterruptedException {

        System.out.println("Please input your username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        System.out.println("Please input your password: ");
        String password = scanner.nextLine();
        boolean validity = this.returnUser(username);

        if (validity && username.equals(password)){
            System.out.println("Login Successful");
            // pass the user ID here not user node
            return userType(this.user);
        }
        else{
            throw new IOException("Username or password incorrect");
        }
    }

    /**
     * Function to check if a user is valid, given their username
     * @param username Username of the user logging in
     * @return userValid True or False value depending on whether the user is valid or not
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public boolean returnUser(String username) throws IOException, InterruptedException {

        client = HttpClient.newHttpClient();
        request = HttpRequest
                .newBuilder(URI.create(usersUrl))
                .setHeader("Authorization", myApiKey)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // so we can loop through the response
        ObjectNode[] jsonNodes = new ObjectMapper().readValue(response.body(), ObjectNode[].class);

        boolean userValid = false;
        for (ObjectNode node: jsonNodes) {
            if (Objects.equals(node.get("userName").textValue(), username)){
                userValid = true;
                this.user = node;
            }
        }
        AuthenticateUser(this.user);

        return userValid;
    }

    /**
     * Function to authenticate a user's credentials using a jwt
     * @param user User who is logging in
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public void AuthenticateUser(ObjectNode user) throws IOException, InterruptedException {
        // create request body
        String jsonString = "{" +
                "\"userName\":\"" + user.get("userName").textValue() + "\"," +
                "\"password\":\"" + user.get("userName").textValue() + "\"" +
                "}";

        String usersLoginUrl = usersUrl + "/login";
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder(URI.create(usersLoginUrl + "?jwt=true")) // Return a JWT so we can use it for verification
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // verifying the jwt
        ObjectNode jsonNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);

        jsonString = "{\"jwt\":\"" + jsonNode.get("jwt").textValue() + "\"}";

        // Note the POST() method being used here, and the request body is supplied to it.
        // A request body needs to be supplied to this endpoint, otherwise a 400 Bad Request error will be returned.
        String usersVerifyTokenUrl = usersUrl + "/verify-token";
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder(URI.create(usersVerifyTokenUrl)) // Return a JWT so we can verify the user.
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (!response.body().equals("")){
            throw new IOException(String.valueOf(response.statusCode()));
        }
    }

    /**
     * Function to check the type of user
     * @param user users.User who is logging in
     * @return userType contains the type of user (users.Resident, users.Receptionist, users.HealthCareWorker)
     */
    public ArrayList<String> userType(ObjectNode user){
        // probably pass the user id
        String userID = user.get("id").textValue();
        if (user.get("isCustomer").booleanValue()){
            return new ArrayList<>(Arrays.asList("Resident", userID));
        }
        else if(user.get("isReceptionist").booleanValue()){
            return new ArrayList<>(Arrays.asList("Receptionist", userID));
        }
        else {
            return new ArrayList<>(Arrays.asList("HealthCareWorker", userID));
        }
    }

}


