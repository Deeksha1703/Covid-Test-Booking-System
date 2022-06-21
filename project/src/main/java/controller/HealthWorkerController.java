package controller;

import view.HealthWorkerView;

public class HealthWorkerController {
    private HealthWorkerView healthWorkerView;

    public HealthWorkerController( HealthWorkerView healthWorkerView) {
        this.healthWorkerView = healthWorkerView;
    }

    public int displayMenu(){
        return healthWorkerView.menuSelection();
    }
}
