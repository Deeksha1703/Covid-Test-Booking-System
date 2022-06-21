package model;

import facilities.CovidTestingSite;
import facilities.SiteSearch;
import utilities.CovidFacade;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ResidentModel {
    /**
     * List of Covid Testing Sites
     */
    private List<CovidTestingSite> options;
    /**
     * Represents if theUser wants to do a home test or not
     */
    boolean homeTest = false;

    public CovidTestingSite searchOptions(int choice) throws IOException, InterruptedException {
        SiteSearch siteSearchFunction = SiteSearch.getInstance();


        if (choice == 1){
            Scanner sc = new Scanner(System.in);
            System.out.println("Please enter the suburb name: ");
            String suburb = sc.nextLine();
            options = CovidFacade.getInstance().testSite().searchSuburb(suburb);
        }
        else if(choice == 7){
            options = siteSearchFunction.searchHomeTesting();
            homeTest = true;
        }
        else{
            switch(choice){
                case 2:
                    options = CovidFacade.getInstance().testSite().searchDriveThrough();
                    break;
                case 3:
                    options = CovidFacade.getInstance().testSite().searchWalkIn();
                    break;
                case 4:
                    options = CovidFacade.getInstance().testSite().searchClinics();
                    break;
                case 5:
                    options = CovidFacade.getInstance().testSite().searchHospital();
                    break;
                case 6:
                    options = CovidFacade.getInstance().testSite().searchGPs();
                    break;
                default:
                    System.out.println("Incorrect option selected");
                    break;
            }
        }

        if(options.size() == 0){
            System.out.println("No sites were found");
            return null;
        }
        else{
            System.out.println("Select site to book:");
            for(int i=0; i<options.size(); i++){
                System.out.println((i + 1) + ") " + options.get(i).getDescription());
            }
            Scanner selection = new Scanner(System.in);
            int siteChoice = selection.nextInt();
            // **below is the test site instance selected. you can use this and the current resident instance to make a booking
            return options.get(siteChoice - 1);
        }

    }
}
