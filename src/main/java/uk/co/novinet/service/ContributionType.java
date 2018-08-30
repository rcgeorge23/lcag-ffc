package uk.co.novinet.service;

public enum ContributionType {
    DONATION("Donation"), CONTRIBUTION_AGREEMENT("Contribution Agreement");

    private String friendlyName;

    private ContributionType(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
