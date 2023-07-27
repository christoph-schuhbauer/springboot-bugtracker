package com.schuhbauer.bugtracker.service;

import com.schuhbauer.bugtracker.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    public User findByUserName(String userName);

}
