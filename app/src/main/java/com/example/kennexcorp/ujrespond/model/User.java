package com.example.kennexcorp.ujrespond.model;

import android.net.Uri;

/**
 * Created by kennexcorp on 1/14/18.
 */

public class User {
    private String id;
    private String profileName;
    private String matriculationNumber;
    private String firstSOSNumber;
    private String secondSOSNumber;
    private String department;
    private String profileAvatar;
    private String faculty;
    private String address;
    private String otherDetail;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getMatriculationNumber() {
        return matriculationNumber;
    }

    public void setMatriculationNumber(String matriculationNumber) {
        this.matriculationNumber = matriculationNumber;
    }

    public String getFirstSOSNumber() {
        return firstSOSNumber;
    }

    public void setFirstSOSNumber(String firstSOSNumber) {
        this.firstSOSNumber = firstSOSNumber;
    }

    public String getSecondSOSNumber() {
        return secondSOSNumber;
    }

    public void setSecondSOSNumber(String secondSOSNumber) {
        this.secondSOSNumber = secondSOSNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProfileAvatar() {
        return profileAvatar;
    }

    public void setProfileAvatar(String profileAvatar) {
        this.profileAvatar = profileAvatar;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOtherDetail() {
        return otherDetail;
    }

    public void setOtherDetail(String otherDetail) {
        this.otherDetail = otherDetail;
    }
}
