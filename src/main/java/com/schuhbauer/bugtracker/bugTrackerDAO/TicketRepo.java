package com.schuhbauer.bugtracker.bugTrackerDAO;

import com.schuhbauer.bugtracker.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByEmployeeId(int id);

    List<Ticket> findByProjectId(int id);



    @Query("select t from Ticket t where (lower(t.name) like lower(concat('%', :search, '%')) or " +
            "lower(t.type) like lower(concat('%', :search, '%')) or " +
            "lower(t.priority) like lower(concat('%', :search, '%')) or " +
            "lower(t.status) like lower(concat('%', :search, '%'))) " +
            "and (t.creationTimestamp >= :start) and (t.creationTimestamp <= :end) and" +
            "(ifnull(t.employee.id, -1) in (:employeeId))" +
            " and (t.project.id in (:projectId)) and" +
            "(t.status in (:status)) order by t.creationTimestamp desc")
    List<Ticket> findAndFilterAllTickets(@Param("search") String search,
                                         @Param("start") Timestamp start,
                                         @Param("end") Timestamp end,
                                         @Param("employeeId") List<Integer> employeeId,
                                         @Param("projectId") List<Integer> projectId,
                                         @Param("status") List<String> status);




    //stat maps


    @Query("select t.priority, COUNT(t.id) from Ticket as t where t.employee.id = :employeeId group by t.priority")
    List<Object[]> getPriorityCountByEmployeeId(@Param("employeeId") Integer employeeId);

    @Query("select t.type, COUNT(t.id) from Ticket as t where t.employee.id = :employeeId group by t.type")
    List<Object[]> getTypeCountByEmployeeId(@Param("employeeId") Integer employeeId);

    @Query("select t.status, COUNT(t.id) from Ticket as t where t.employee.id = :employeeId group by t.status")
    List<Object[]> getStatusCountByEmployeeId(@Param("employeeId") Integer employeeId);




    @Query("select t.priority, COUNT(t.id) from Ticket as t where t.project.id = :projectId group by t.priority")
    List<Object[]> getPriorityCountByProjectId(@Param("projectId") Integer projectId);

    @Query("select t.type, COUNT(t.id) from Ticket as t where t.project.id = :projectId group by t.type")
    List<Object[]> getTypeCountByProjectId(@Param("projectId") Integer projectId);

    @Query("select t.status, COUNT(t.id) from Ticket as t where t.project.id = :projectId group by t.status")
    List<Object[]> getStatusCountByProjectId(@Param("projectId") Integer projectId);






    @Query("select t.type, COUNT(t.id) from Ticket t where (lower(t.name) like lower(concat('%', :search, '%')) or lower(t.type) like lower(concat('%', :search, '%')) or "  +
            "lower(t.priority) like lower(concat('%', :search, '%')) or lower(t.status) like lower(concat('%', :search, '%'))) " +
            "and (t.creationTimestamp >= :start) and (t.creationTimestamp <= :end) and" +
            "(ifnull(t.employee.id, -1) in (:employeeId)) and (t.project.id in (:projectId)) and" +
            "(t.status in (:status)) group by t.type")
    List<Object[]> getTypeCount(@Param("search") String search,
                                @Param("start") Timestamp start,
                                @Param("end") Timestamp end,
                                @Param("employeeId") List<Integer> employeeId,
                                @Param("projectId") List<Integer> projectId,
                                @Param("status") List<String> status);


    @Query("select t.priority, COUNT(t.id) from Ticket t where (lower(t.name) like lower(concat('%', :search, '%')) or lower(t.type) like lower(concat('%', :search, '%')) or "  +
            "lower(t.priority) like lower(concat('%', :search, '%')) or lower(t.status) like lower(concat('%', :search, '%'))) " +
            "and (t.creationTimestamp >= :start) and (t.creationTimestamp <= :end) and" +
            "(ifnull(t.employee.id, -1) in (:employeeId)) and (t.project.id in (:projectId)) and" +
            "(t.status in (:status)) group by t.priority")
    List<Object[]> getPriorityCount(@Param("search") String search,
                                    @Param("start") Timestamp start,
                                    @Param("end") Timestamp end,
                                    @Param("employeeId") List<Integer> employeeId,
                                    @Param("projectId") List<Integer> projectId,
                                    @Param("status") List<String> status);

    @Query("select t.status, COUNT(t.id) from Ticket t where (lower(t.name) like lower(concat('%', :search, '%')) or " +
            "lower(t.type) like lower(concat('%', :search, '%')) or " +
            "lower(t.priority) like lower(concat('%', :search, '%')) or " +
            "lower(t.status) like lower(concat('%', :search, '%'))) " +
            "and (t.creationTimestamp >= :start) and (t.creationTimestamp <= :end) and" +
            "(ifnull(t.employee.id, -1) in (:employeeId)) and (t.project.id in (:projectId)) and" +
            "(t.status in (:status)) group by t.status")
    List<Object[]> getStatusCount(@Param("search") String search,
                                  @Param("start") Timestamp start,
                                  @Param("end") Timestamp end,
                                  @Param("employeeId") List<Integer> employeeId,
                                  @Param("projectId") List<Integer> projectId,
                                  @Param("status") List<String> status);


    @Query("select t.project, COUNT(t.id) from Ticket t where (lower(t.name) like lower(concat('%', :search, '%')) or " +
            "lower(t.type) like lower(concat('%', :search, '%')) or " +
            "lower(t.priority) like lower(concat('%', :search, '%')) or " +
            "lower(t.status) like lower(concat('%', :search, '%'))) " +
            "and (t.creationTimestamp >= :start) and (t.creationTimestamp <= :end) and" +
            "(ifnull(t.employee.id, -1) in (:employeeId)) and (t.project.id in (:projectId)) and" +
            "(t.status in (:status)) group by t.project")
    List<Object[]> getProjectCount(@Param("search") String search,
                                   @Param("start") Timestamp start,
                                   @Param("end") Timestamp end,
                                   @Param("employeeId") List<Integer> employeeId,
                                   @Param("projectId") List<Integer> projectId,
                                   @Param("status") List<String> status);



    @Query("select t.creationTimestamp, COUNT(t.id) from Ticket t where t.id > 0 group by t.creationTimestamp")
    List<Object[]> getCreationTimestampCount();




}
