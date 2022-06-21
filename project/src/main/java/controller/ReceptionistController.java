package controller;

import view.ReceptionistView;

public class ReceptionistController {
    private ReceptionistView receptionistView;

    public ReceptionistController(ReceptionistView receptionistView) {
        this.receptionistView = receptionistView;
    }

    public int displayMenu(){
        int selection = receptionistView.menuSelection();
        return selection;
    }


}

