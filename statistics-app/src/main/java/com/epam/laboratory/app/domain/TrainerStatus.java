package com.epam.laboratory.app.domain;

public enum TrainerStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private String status;

    TrainerStatus(String status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return status;
    }
}
