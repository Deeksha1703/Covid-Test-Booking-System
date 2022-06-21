package facilities;

import java.io.IOException;
import java.util.List;

public class FacilitiesFacade {

    public List<CovidTestingSite> searchSuburb(String suburb) throws IOException, InterruptedException {
        return SiteSearch.getInstance().searchSuburb(suburb);
    }

    public List<CovidTestingSite> searchDriveThrough() throws IOException, InterruptedException {
        return SiteSearch.getInstance().searchDriveThrough();
    }

    public List<CovidTestingSite> searchWalkIn() throws IOException, InterruptedException {
        return SiteSearch.getInstance().searchWalkIn();
    }

    public List<CovidTestingSite> searchClinics() throws IOException, InterruptedException {
        return SiteSearch.getInstance().searchClinics();
    }

    public List<CovidTestingSite> searchHospital() throws IOException, InterruptedException {
        return SiteSearch.getInstance().searchHospital();
    }

    public List<CovidTestingSite> searchGPs() throws IOException, InterruptedException {
        return SiteSearch.getInstance().searchGPs();
    }

}
