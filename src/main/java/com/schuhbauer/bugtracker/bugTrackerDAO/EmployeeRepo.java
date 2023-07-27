package com.schuhbauer.bugtracker.bugTrackerDAO;

import com.schuhbauer.bugtracker.entity.Employee;
import com.schuhbauer.bugtracker.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepo extends JpaRepository<Employee , Integer> {

    List<Employee> findAllByProjectsId(int id);
    Employee findByTicketsId(int id);
    Employee findByUserId(Long userId);
    Employee findByUserUserName(String username);
}
