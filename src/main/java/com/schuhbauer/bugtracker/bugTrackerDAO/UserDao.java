package com.schuhbauer.bugtracker.bugTrackerDAO;

import com.schuhbauer.bugtracker.entity.User;

public interface UserDao {

    User findByUserName(String userName);

}
