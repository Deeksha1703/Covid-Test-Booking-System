package utilities;

import java.util.Scanner;

/**
 * Class which is used to take responses from the user in the form of yes or no responses
 */
public class Question {

    /**
     * Question being asked
     */
    private static Question firstInstance = null;

    /**
     * Function to create a new instance of the class
     * @return question-First Instance of the question class
     */
    public static Question getInstance() {
        if (firstInstance == null) {
            firstInstance = new Question();
        }
        return firstInstance;
    }

    /**
     * Function to take the response of the user for a given question
     * @param question The question asked by the system
     * @return answer-The user's response
     */
    public boolean interview(String question) {
        String userResponse;
        boolean validResponse = false;
        boolean answer = false;
        while (!validResponse) {
            System.out.print(question  + " [Answer either YES/NO] ");
            try {
                Scanner sc = new Scanner(System.in);
                userResponse = sc.nextLine();
                answer = validateUserResponse(userResponse);
                validResponse = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return answer;
    }

    /**
     * Function to validate the user's response, ie whether it is yes or no
     * @param answer The user's response
     * @return true if the user's response is valid, else false
     */
    public boolean validateUserResponse(String answer) {
        if (answer.equalsIgnoreCase("NO")) {
            return false;
        } else if (answer.equalsIgnoreCase("YES")) {
            return true;
        } else {
            throw new IllegalArgumentException("You must answer either YES or NO !");
        }    }

}
