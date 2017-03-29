package org.redsha.transbox.bean;

import java.util.List;

public class KeywordBean {

    private List<String> bloodType;
    private List<String> organ;
    private List<String> organisationSample;
    private List<String> tracfficType;

    public List<String> getBloodType() {
        return bloodType;
    }

    public void setBloodType(List<String> bloodType) {
        this.bloodType = bloodType;
    }

    public List<String> getOrgan() {
        return organ;
    }

    public void setOrgan(List<String> organ) {
        this.organ = organ;
    }

    public List<String> getOrganisationSample() {
        return organisationSample;
    }

    public void setOrganisationSample(List<String> organisationSample) {
        this.organisationSample = organisationSample;
    }

    public List<String> getTracfficType() {
        return tracfficType;
    }

    public void setTracfficType(List<String> tracfficType) {
        this.tracfficType = tracfficType;
    }
}
