package com.finverse.profile.domain.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char") // Stores UUID as readable string in database
    private UUID id;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "first-name",nullable = false)
    private String firstname;
    @Column(name = "last-name",nullable = false)
    private String lastname;
    @Column(name = "age",nullable = false)
    private int age;
    @Column(name = "occupation",nullable = false)
    private String occupation;
    @Column(name = "registered-since",nullable = false)
    private LocalDate registeredSince;
    public UserProfile() {}

    public UserProfile(String username, String firstName, String lastName, int age, String occupation) {
        this.username = username;
        this.firstname = firstName;
        this.lastname = lastName;
        this.age = age;
        this.occupation = occupation;
        this.registeredSince = LocalDate.now();
    }


//    public void setRegisteredSince(LocalDate registeredSince) {
//        this.registeredSince = registeredSince;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public String getFirstname() {
//        return firstname;
//    }
//
//    public String getLastname() {
//        return lastname;
//    }
//
//    public int getAge() {
//        return age;
//    }
//
//    public String getOccupation() {
//        return occupation;
//    }
//
//    public LocalDate getRegisteredSince() {
//        return registeredSince;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile user = (UserProfile) o;
        return age == user.age && Objects.equals(username, user.username) && Objects.equals(firstname, user.firstname) && Objects.equals(lastname, user.lastname) && Objects.equals(occupation, user.occupation) && Objects.equals(registeredSince, user.registeredSince);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, firstname, lastname, age, occupation, registeredSince);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", age=" + age +
                ", occupation='" + occupation + '\'' +
                ", registeredSince=" + registeredSince +
                '}';
    }
}
