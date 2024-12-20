package com.dhosio.voucherscan.data;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
public class User {

    private String empId;
    private String firstName;
    private String lastName;
    private String username;
    private String gender;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private String branch;

    public User(String empId, String firstName, String lastName, String username, String gender, String email, String password, LocalDate dateOfBirth, String branch) {
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.branch = branch;
    }

    public User(){}

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public String getGender(){
        return this.gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "empId='" + empId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", branch='" + branch + '\'' +
                '}';
    }
}
