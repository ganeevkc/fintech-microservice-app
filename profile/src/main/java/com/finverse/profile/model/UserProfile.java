package com.finverse.profile.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    @Type(type = "uuid-char") // Stores UUID as readable string in database
    @Convert(converter = UuidConverter.class)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "CHAR(36)")
//    @Convert(converter = UuidConverter.class)
    private UUID userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    @Column(name = "age")
    private Integer age;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "registered_since")
    private LocalDate registeredSince;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    public UserProfile() {}

    public UserProfile(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
        this.registeredSince = LocalDate.now();
    }
    public UserProfile(String username, String firstName, String lastName, int age, String occupation, Role role) {
        this.username = username;
        this.firstname = firstName;
        this.lastname = lastName;
        this.age = age;
        this.occupation = occupation;
        this.registeredSince = LocalDate.now();
        this.role = role;
    }

    @PrePersist
    protected void onCreate() {
        if (this.registeredSince == null) {
            this.registeredSince = LocalDate.now();
        }
    }

    @Converter(autoApply = true)
    public static class UuidConverter implements AttributeConverter<UUID, String> {
        @Override
        public String convertToDatabaseColumn(UUID uuid) {
            return uuid != null ? uuid.toString() : null;
        }

        @Override
        public UUID convertToEntityAttribute(String dbData) {
            return dbData != null ? UUID.fromString(dbData) : null;
        }
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
