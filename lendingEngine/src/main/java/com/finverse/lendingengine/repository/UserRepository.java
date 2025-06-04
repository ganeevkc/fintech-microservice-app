package com.finverse.lendingengine.repository;

import com.finverse.lendingengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
}
