package utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Class to extract the symptoms from the individual text files
 */
public class SymptomsCollection {

    /**
     * First instance of the SymptomsCollection class
     */
    private static SymptomsCollection firstInstance = null;

    /**
     * List for severe symptoms
     */
    private List<String> severeSymptoms;

    /**
     * List for mild symptoms
     */
    private List<String> mildSymptoms;
    /**
     * List for moderate symptoms
     */
    private List<String> moderateSymptoms;

    /**
     * Constructor for the SymptomsCollection class
     */
    private SymptomsCollection()  {
        this.getSymptoms();
    }

    /**
     * Function to create a new instance of the SymptomsCollection class
     * @return firstInstance Instance of SymptomsCollection class
     */
    public static SymptomsCollection getInstance()  {
        if (firstInstance == null) {
            firstInstance = new SymptomsCollection();
        }
        return firstInstance;
    }

    /**
     * Function to read the symptoms from their text files and combine them to a single list
     */
    private void getSymptoms(){
        String mild_symptoms_path = "src/main/java/utilities/mildsymptoms.txt";
        String moderate_symptoms_path = "src/main/java/utilities/moderatesymptoms.txt";
        String severe_symptoms_path = "src/main/java/utilities/severesymptoms.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mild_symptoms_path));
            String[] symptoms = reader.readLine().split(",");
            mildSymptoms = List.of(symptoms);
            reader.close();

            reader = new BufferedReader(new FileReader(moderate_symptoms_path));
            symptoms = reader.readLine().split(",");
            moderateSymptoms = List.of(symptoms);
            reader.close();

            reader = new BufferedReader(new FileReader(severe_symptoms_path));
            symptoms = reader.readLine().split(",");
            severeSymptoms = List.of(symptoms);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Function to get the list of severe symptoms
     * @return severeSymptoms List of severe symptoms
     */
    public List<String> getSevereSymptoms() {
        return severeSymptoms;
    }

    /**
     * Function to get the list of mild symptoms
     * @return mildSymptoms List of mild symptoms
     */
    public List<String> getMildSymptoms() {
        return mildSymptoms;
    }

    /**
     * Function to get the list of moderate symptoms
     * @return moderateSymptoms List of moderate symptoms
     */
    public List<String> getModerateSymptoms() {
        return moderateSymptoms;
    }
}
