package controller;

import model.ResidentModel;
import view.ResidentView;
import facilities.CovidTestingSite;

import java.io.IOException;

public class ResidentController {
    private ResidentView residentView;
    private ResidentModel residentModel;

    public ResidentController(ResidentView residentView, ResidentModel residentModel){
        this.residentView = residentView;
        this.residentModel = residentModel;
    }

    public int displaySearchMenu() throws IOException, InterruptedException {
        return residentView.searchMenu();
        //return searchModel.searchOptions(selection);
    }

    public CovidTestingSite displaySiteOptions(int selection) throws IOException, InterruptedException {
        return residentModel.searchOptions(selection);
    }

}
