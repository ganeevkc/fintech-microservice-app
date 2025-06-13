package com.finverse.lendingengine.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private UUID userId;

//    @Column(nullable = false, unique = true)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;
//
//    private String first_name;
//    private String last_name;
//    private int age;
//    private String occupation;
    @Enumerated(EnumType.STRING)
    private String role;

    @OneToOne(cascade = CascadeType.ALL) /*when user object gets deleted, balance will also get deleted*/
    private Balance balance;


    @PrePersist
    public void prePersist() {
        if (this.userId == null) {
            this.userId = UUID.randomUUID();
        }
    }

    public void setBalance(Balance balance){
        if(this.balance==null){
            this.balance=balance;
        }
    }

    public void topUp(final Money money){
        balance.topUp(money);
    }

    public void withdraw(final Money money){
        balance.withdraw(money);
    }
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        User user = (User) o;
//        return age == user.age && Objects.equals(username, user.username) && Objects.equals(first_name, user.first_name) && Objects.equals(last_name, user.last_name) && Objects.equals(occupation, user.occupation);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(username, first_name, last_name, age, occupation);
//    }
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "username='" + username + '\'' +
//                ", first_name='" + first_name + '\'' +
//                ", last_name='" + last_name + '\'' +
//                ", age=" + age +
//                ", occupation='" + occupation + '\'' +
//                '}';
//    }
}
