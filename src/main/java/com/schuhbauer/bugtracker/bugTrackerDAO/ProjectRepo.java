package com.schuhbauer.bugtracker.bugTrackerDAO;

import com.schuhbauer.bugtracker.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepo extends JpaRepository<Project, Integer> {

    List<Project> findAllByEmployeesId(int id);
    Project findByTicketsId(int id);
}
