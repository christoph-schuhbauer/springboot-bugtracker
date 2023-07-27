package com.schuhbauer.bugtracker.bugTrackerDAO;

import com.schuhbauer.bugtracker.entity.TicketLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketLogRepo extends JpaRepository<TicketLog, Integer> {


    List<TicketLog> findAlLByTicketId(int ticketId);



}
