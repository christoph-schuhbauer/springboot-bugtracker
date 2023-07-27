package com.schuhbauer.bugtracker.bugTrackerDAO;

import com.schuhbauer.bugtracker.entity.Role;

public interface RoleDao {

    public Role findRoleByName(String theRoleName);

}
