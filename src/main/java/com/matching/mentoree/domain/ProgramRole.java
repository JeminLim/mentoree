package com.matching.mentoree.domain;

public enum ProgramRole {

    MENTOR("멘토"),
    MENTEE("멘티");

    private String value;

    ProgramRole(String value) { this.value = value;}

    public String getKey() {return name();}
    public String getValue() {return value;}

}
