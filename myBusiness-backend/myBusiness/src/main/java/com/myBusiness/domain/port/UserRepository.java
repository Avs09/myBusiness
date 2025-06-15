package com.myBusiness.domain.port;

import com.myBusiness.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);

}
