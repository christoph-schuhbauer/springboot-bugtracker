package com.schuhbauer.bugtracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "type")
    private String type;

    @Column(name = "priority")
    private String priority;

    @Column(name = "creation_timestamp", updatable = false)
    private java.sql.Timestamp creationTimestamp;

    @Column(name = "latest_update_timestamp")
    private java.sql.Timestamp latestUpdateTimestamp;




    @ManyToOne(cascade = {  CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;



    @ManyToOne(cascade = {  CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;



    @OneToMany(mappedBy = "ticket",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
    private List<TicketLog> ticketLogs;



    public Ticket() {
    }


    public Ticket(String name, String description, String status, String type, String priority) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
        this.priority = priority;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String urgency) {
        this.priority = urgency;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationDate) {
        this.creationTimestamp = creationDate;
    }

    public Timestamp getLatestUpdateTimestamp() {
        return latestUpdateTimestamp;
    }

    public void setLatestUpdateTimestamp(Timestamp completionDate) {
        this.latestUpdateTimestamp = completionDate;
    }



    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<TicketLog> getTicketLogs() {
        return ticketLogs;
    }

    public void setTicketLogs(List<TicketLog> ticketLogs) {
        this.ticketLogs = ticketLogs;
    }

    public void addTicketLog(TicketLog ticketLog){

        if (this.getTicketLogs() == null){
            this.ticketLogs = new ArrayList<>();
        }

        this.ticketLogs.add(ticketLog);

    }




    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", urgency='" + priority + '\'' +
                ", creationDate=" + creationTimestamp +
                ", completionDate=" + latestUpdateTimestamp +
                '}';
    }
}
