package com.school.schoolmanagement.Admin.Model;

public class BankAccountFields {
    private String name;
    private String address;
    private String accNumber;
    private String instruction;
    private String imageBase64;

    // Constructor
    public BankAccountFields() {}

    public BankAccountFields(String name, String address, String accNumber, String instruction, String imageBase64) {
        this.name = name;
        this.address = address;
        this.accNumber = accNumber;
        this.instruction = instruction;
        this.imageBase64 = imageBase64;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    @Override
    public String toString() {
        return "BankAccountFields{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", accNumber='" + accNumber + '\'' +
                ", instruction='" + instruction + '\'' +
                ", imageBase64='" + (imageBase64 != null ? "Present" : "null") + '\'' +
                '}';
    }
}
