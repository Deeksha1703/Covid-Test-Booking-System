package facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import utilities.AccessToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class SiteSearch {
    /**
     * Instance of Search class
     */
    private static SiteSearch firstInstance = null;

    /**
     * Root URL for the web service
     */
    private static final String rootUrl = "https://fit3077.com/api/v2";

    /**
     * URL endpoint for all testing sites
     */
    private String siteUrl = rootUrl + "/testing-site";

    /**
     * Nodes for linked list with testing sites in each node
     */
    private ObjectNode[] sites;

    /**
     * Constructor for the Search class
     */
    private SiteSearch(){}

    /**
     * Function to return the singleton Search class instance
     * @return firstInstance Singleton instance of Search class
     */
    public static SiteSearch getInstance() {
        if(firstInstance == null){
            firstInstance = new SiteSearch();
        }
        return firstInstance;
    }

    /**
     * Function to search for covid testing sites based on suburb
     * @param suburb Suburb in which we need to search for covid testing sites
     * @return returnSites Sites which are present in the specified suburb
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public List<CovidTestingSite> searchSuburb(String suburb) throws IOException, InterruptedException {
        String finalSuburb = suburb.toLowerCase();
        sites = this.getTestSites();
        List<CovidTestingSite> returnSites = new ArrayList<>();
        // loop through test sites and look for test site with suburb name
        for (ObjectNode node: sites) {
            String siteSuburb = node.get("address").get("suburb").textValue();
            if (siteSuburb.toLowerCase().equals(finalSuburb)){
                String id = node.get("id").textValue();
                CovidTestingSite testSite = new CovidTestingSite(id);
                returnSites.add(testSite);
            }
        }
        return returnSites;
    }

    /**
     * Function to search for covid test sites which have drive through
     * @return returnSite Covid test sites which have drive through
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public List<CovidTestingSite> searchDriveThrough() throws IOException, InterruptedException {
        sites = this.getTestSites();
        List<CovidTestingSite> returnSites = new ArrayList<>();
        // loop through test sites and look for test site with suburb name
        for (ObjectNode node: sites) {
            boolean siteDriveThrough = node.get("additionalInfo").get("driveThrough").booleanValue();
            if (siteDriveThrough){
                String id = node.get("id").textValue();
                CovidTestingSite testSite = new CovidTestingSite(id);
                returnSites.add(testSite);
            }
        }
        return returnSites;
    }

    /**
     * Function to search for covid test sites which have walk-in covid testing
     * @return returnSites Covid test sites which have walk-in covid testing
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public List<CovidTestingSite> searchWalkIn() throws IOException, InterruptedException {
        sites = this.getTestSites();
        List<CovidTestingSite> returnSites = new ArrayList<>();
        // loop through test sites and look for test site with suburb name
        for (ObjectNode node: sites) {
            boolean siteWalkIn = node.get("additionalInfo").get("walkIn").booleanValue();
            if (siteWalkIn){
                String id = node.get("id").textValue();
                CovidTestingSite testSite = new CovidTestingSite(id);
                returnSites.add(testSite);
            }
        }
        return returnSites;
    }

    /**
     * Function to search for Covid testing sites that are Clinics
     * @return returnSites Covid test sites which are clinics
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public List<CovidTestingSite> searchClinics() throws IOException, InterruptedException {
        sites = this.getTestSites();
        List<CovidTestingSite> returnSites = new ArrayList<>();
        // loop through test sites and look for test site with suburb name
        for (ObjectNode node: sites) {
            boolean siteHospital = node.get("additionalInfo").get("hospital").booleanValue();
            if (!siteHospital){
                String id = node.get("id").textValue();
                CovidTestingSite testSite = new CovidTestingSite(id);
                returnSites.add(testSite);
            }
        }
        return returnSites;
    }

    /**
     * Function to search for covid test sites which are hospitals
     * @return returnSites Covid test sites which are hospitals
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public List<CovidTestingSite> searchHospital() throws IOException, InterruptedException {
        sites = this.getTestSites();
        List<CovidTestingSite> returnSites = new ArrayList<>();
        // loop through test sites and look for test site with suburb name
        for (ObjectNode node: sites) {
            boolean siteHospital = node.get("additionalInfo").get("hospital").booleanValue();
            if (siteHospital){
                String id = node.get("id").textValue();
                CovidTestingSite testSite = new CovidTestingSite(id);
                returnSites.add(testSite);
            }
        }
        return returnSites;
    }

    /**
     * Function to search for test sites which are GPs
     * @return returnSites Covid test sites which are GPs
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public List<CovidTestingSite> searchGPs() throws IOException, InterruptedException {
        sites = this.getTestSites();
        List<CovidTestingSite> returnSites = new ArrayList<>();
        // loop through test sites and look for test site with suburb name
        for (ObjectNode node: sites) {
            boolean siteGP = node.get("additionalInfo").get("GP").booleanValue();
            if (siteGP){
                String id = node.get("id").textValue();
                CovidTestingSite testSite = new CovidTestingSite(id);
                returnSites.add(testSite);
            }
        }
        return returnSites;
    }

    /**
     * Function to search for test sites which provide home testing
     * @return returnSites Covid test sites which provide home testing
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public List<CovidTestingSite> searchHomeTesting() throws IOException, InterruptedException {
        sites = this.getTestSites();
        List<CovidTestingSite> returnSites = new ArrayList<>();
        // loop through test sites and look for test site which provides home testing
        for (ObjectNode node: sites) {
            boolean siteHomeTesting = node.get("additionalInfo").get("homeTesting").booleanValue();
            if (siteHomeTesting){
                String id = node.get("id").textValue();
                CovidTestingSite testSite = new CovidTestingSite(id);
                returnSites.add(testSite);
            }
        }
        return returnSites;
    }

    /**
     * Function to get list of all covid test sites from the api
     * @return jsonSiteNodes Nodes for linked list with testing sites in each node
     * @throws IOException Exception that occurs when an IO operation fails
     * @throws InterruptedException Exception that occurs when a thread is waiting or otherwise occupied and is interrupted
     */
    public ObjectNode[] getTestSites() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest
                .newBuilder(URI.create(siteUrl))
                .setHeader("Authorization", AccessToken.token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readValue(response.body(), ObjectNode[].class);
    }
}
