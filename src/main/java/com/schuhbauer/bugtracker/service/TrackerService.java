package com.schuhbauer.bugtracker.service;

import com.schuhbauer.bugtracker.bugTrackerDAO.*;
import com.schuhbauer.bugtracker.entity.*;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class TrackerService {

    private EmployeeRepo employeeRepo;
    private TicketRepo ticketRepo;

    private ProjectRepo projectRepo;

    private TicketLogRepo ticketLogRepo;


    public TrackerService(EmployeeRepo employeeRepo, TicketRepo ticketRepo, ProjectRepo projectRepo, TicketLogRepo ticketLogRepo) {
        this.employeeRepo = employeeRepo;
        this.ticketRepo = ticketRepo;
        this.projectRepo = projectRepo;
        this.ticketLogRepo = ticketLogRepo;
    }




    public void saveTicket(Ticket ticket, int employeeId, int projectId){

        Project project = projectRepo.findById(projectId).get();
        Employee newEmployee = null;

        if (employeeId != 0){
            newEmployee = employeeRepo.getReferenceById(employeeId);
        }

        long millies = System.currentTimeMillis();
        java.sql.Timestamp timestamp = new Timestamp(millies);
        ticket.setLatestUpdateTimestamp(timestamp);


        //for saving a new ticket
        if (ticket.getId() == 0){

            ticket.setEmployee(newEmployee);
            ticket.setProject(project);

            long millis = System.currentTimeMillis();
            java.sql.Timestamp date = new Timestamp(millis);
            ticket.setCreationTimestamp(date);


            //if new ticket has assigned employee
            if (newEmployee != null){
                newEmployee.addProject(project);
                employeeRepo.save(newEmployee);
            }
            ticket.addTicketLog(createLog(ticket));
            ticketRepo.save(ticket);

        // for updating an existing ticket
        }else {

            Ticket oldTicket = ticketRepo.findById(ticket.getId()).get();
            Employee oldEmployee = employeeRepo.findByTicketsId(ticket.getId());

            ticket.setProject(project);
            // if ticket current employee is not existent
            if (oldEmployee == null){
                // if ticket stays unassigned
                if (newEmployee != null){

                    newEmployee.addProject(project);
                    employeeRepo.save(newEmployee);

                    ticket.setEmployee(newEmployee);
                    ticket.addTicketLog(createLog(ticket));
                    ticketRepo.save(ticket);
                // ticket employee goes from unassigned to assigned
                }else {

                    ticket.addTicketLog(createLog(ticket));
                    ticketRepo.save(ticket);

                }
            //tickets has assigned employee
            }else {
                //if tickets get a new assigned employee
                if (newEmployee != null){

                    oldEmployee.removeProject(oldTicket.getProject());
                    employeeRepo.save(oldEmployee);

                    newEmployee.addProject(project);
                    employeeRepo.save(newEmployee);

                    ticket.setEmployee(newEmployee);
                    ticket.addTicketLog(createLog(ticket));
                    ticketRepo.save(ticket);

                // ticket employee changes to unassigned
                }else {

                    oldEmployee.removeProject(oldTicket.getProject());
                    employeeRepo.save(oldEmployee);

                    ticket.setEmployee(null);
                    ticket.addTicketLog(createLog(ticket));

                    ticketRepo.save(ticket);

                }
            }
        }
    }


    public void deleteTicketById(int ticketId){

        Ticket ticket = ticketRepo.findById(ticketId).get();
        if (ticket.getEmployee() != null){
            Employee employee = ticket.getEmployee();
            Project project = ticket.getProject();
            employee.removeProject(project);

            employeeRepo.save(employee);
        }

        ticketRepo.deleteById(ticketId);
    }


    public Ticket getTicketById(int ticketId){
        return ticketRepo.findById(ticketId).get();
    }
    public List<Ticket> getTicketsByEmployeeId(int employeeId){
        return ticketRepo.findByEmployeeId(employeeId);
    }
    public List<Ticket> getTicketsByProjectId(int projectId){
        return ticketRepo.findByProjectId(projectId);
    }



    public List<Ticket> findAndFilterAllTickets(String search, Timestamp start, Timestamp end,
                                                List<Integer> employeeId, List<Integer> projectId,
                                                List<String> status){
        return ticketRepo.findAndFilterAllTickets(search, start, end, employeeId, projectId, status);
    }




    public List<Integer> createdAndClosedInDays(int numberOfDays, List<Ticket> tickets){

        int count_new = 0;
        int count_closed = 0;
        LocalDate lastWeek = LocalDate.now().minusDays(numberOfDays);
        for (Ticket ticket: tickets){
            if (ticket.getCreationTimestamp().after(Date.valueOf(lastWeek))){
                count_new++;
                if (ticket.getStatus().equals("closed")){
                    count_closed++;
                }
            }else {
                break;
            }
        }

        List<Integer> result = new ArrayList<>();
        result.add(count_new);
        result.add(count_closed);
        return result;
    }




    //-------------maps---------


    public HashMap<String, Long> getPriorityCountHashMapByEmployeeId(int employeeId){

        List<Object[]> result = ticketRepo.getPriorityCountByEmployeeId(employeeId);

        HashMap<String, Long> priorityMap = new HashMap<>();
        priorityMap.put("high", 0L);
        priorityMap.put("low", 0L);
        priorityMap.put("mid", 0L);

        for (Object[] temp: result) {
            priorityMap.put((String) temp[0],(Long) temp[1]);

        }
        return priorityMap;
    }


    public HashMap<String, Long> getPriorityCountHashMapByProjectId(int projectId){

        List<Object[]> result = ticketRepo.getPriorityCountByProjectId(projectId);

        HashMap<String, Long> priorityMap = new HashMap<>();
        priorityMap.put("high", 0L);
        priorityMap.put("low", 0L);
        priorityMap.put("mid", 0L);

        for (Object[] temp: result) {
            priorityMap.put((String) temp[0],(Long) temp[1]);

        }
        return priorityMap;
    }




    public HashMap<String, Long> getPriorityCountHashMap(String search, Timestamp start, Timestamp end, List<Integer> employeeId, List<Integer> projectId, List<String> status){

        List<Object[]> result = ticketRepo.getPriorityCount(search, start, end, employeeId, projectId, status);
        HashMap<String, Long> map = new HashMap<>();
        map.put("high", 0L);
        map.put("low", 0L);
        map.put("mid", 0L);

        for (Object[] temp: result) {
            map.put((String) temp[0],(Long) temp[1]);
        }
        return map;
    }




    //-------------status maps-----------

    public HashMap<String, Long> getStatusCountHashMapByEmployeeId(int employeeId){
        List<Object[]> result = ticketRepo.getStatusCountByEmployeeId(employeeId);

        HashMap<String, Long> statusMap = new HashMap<>();
        statusMap.put("waiting", 0L);
        statusMap.put("active", 0L);
        statusMap.put("closed", 0L);

        for (Object[] temp: result) {
            statusMap.put((String) temp[0],(Long) temp[1]);
        }
        return statusMap;
    }



    public HashMap<String, Long> getStatusCountHashMapByProjectId(int projectId){
        List<Object[]> result = ticketRepo.getStatusCountByProjectId(projectId);

        HashMap<String, Long> statusMap = new HashMap<>();
        statusMap.put("waiting", 0L);
        statusMap.put("active", 0L);
        statusMap.put("closed", 0L);

        for (Object[] temp: result) {
            statusMap.put((String) temp[0],(Long) temp[1]);
        }
        return statusMap;
    }




    public HashMap<String, Long> getStatusCountHashMap(String search, Timestamp start, Timestamp end, List<Integer> employeeId, List<Integer> projectId, List<String> status){
        List<Object[]> result = ticketRepo.getStatusCount(search, start, end, employeeId, projectId, status);
        HashMap<String, Long> map = new HashMap<>();
        map.put("active", 0L);
        map.put("waiting", 0L);
        map.put("closed", 0L);

        for (Object[] temp: result) {
            map.put((String) temp[0],(Long) temp[1]);
        }
        return map;
    }





    //-------------type maps-----------

    public HashMap<String, Long> getTypeCountHashMapByEmployeeId(int employeeId){
        List<Object[]> result = ticketRepo.getTypeCountByEmployeeId(employeeId);

        HashMap<String, Long> typeMap = new HashMap<>();
        typeMap.put("bug", 0L);
        typeMap.put("feature", 0L);

        for (Object[] temp: result) {
            typeMap.put((String) temp[0],(Long) temp[1]);
        }
        return typeMap;
    }





    public HashMap<String, Long> getTypeCountHashMapByProjectId(int projectId){
        List<Object[]> result = ticketRepo.getTypeCountByProjectId(projectId);

        HashMap<String, Long> typeMap = new HashMap<>();
        typeMap.put("bug", 0L);
        typeMap.put("feature", 0L);

        for (Object[] temp: result) {
            typeMap.put((String) temp[0],(Long) temp[1]);
        }
        return typeMap;
    }




    public HashMap<String, Long> getTypeCountHashMap(String search, Timestamp start, Timestamp end, List<Integer> employeeId, List<Integer> projectId, List<String> status){

        List<Object[]> result = ticketRepo.getTypeCount(search, start, end, employeeId, projectId, status);
        HashMap<String, Long> map = new HashMap<>();
        map.put("feature", 0L);
        map.put("bug", 0L);

        for (Object[] temp: result) {
            map.put((String) temp[0],(Long) temp[1]);
        }
        return map;
    }





    //-------------project maps-----------
    public HashMap<String, Long> getProjectCountHashMap(String search, Timestamp start, Timestamp end, List<Integer> employeeId, List<Integer> projectId, List<String> status){

        List<Object[]> result = ticketRepo.getProjectCount(search, start, end, employeeId, projectId, status);
        HashMap<String, Long> map = new HashMap<>();

        for (Object[] temp: result) {
            map.put(((Project) temp[0]).getTitle(),(Long) temp[1]);
        }

        return map;
    }


    //-------------timestamp map

    public HashMap<Timestamp, Long> getCreationTimestampCountHasMap(){
        List<Object[]> result = ticketRepo.getCreationTimestampCount();

        HashMap<Timestamp, Long> timeMap = new HashMap<>();

        for (Object[] temp: result) {
            timeMap.put((Timestamp)temp[0],(Long) temp[1]);

        }
        return timeMap;
    }


    //--------------projects-----------------------
    public Project getProjectById(int projectId){
        return projectRepo.findById(projectId).get();
    }


    public List<Project> getProjectsByEmployeeId(int employeeId){
        return projectRepo.findAllByEmployeesId(employeeId);
    }


    public List<Project> getAllProjects(){
        return projectRepo.findAll();
    }
    //--------------employees-----------------------



    public Employee findEmployeeByUserId(Long userId){
        return employeeRepo.findByUserId(userId);
    }

    public Employee findEmployeeByUserName(String username){
        return employeeRepo.findByUserUserName(username);
    };

    public  Employee getEmployeeById(int employeeId){
        return employeeRepo.findById(employeeId).get();
    }

    public List<Employee> getEmployeesByProjectId(int projectId){
        return employeeRepo.findAllByProjectsId(projectId);
    }


    public List<Employee> getAllEmployees(){
        return employeeRepo.findAll();
    }





    //----------------ticketLogs------------------

    public List<TicketLog> findAllLogsByTicketId(int ticketId){
        return ticketLogRepo.findAlLByTicketId(ticketId);
    }


    public HashMap<String, List<Object>> compare(TicketLog oldTicket, TicketLog newTicket){


        HashMap<String, List<Object>> changed_values = new HashMap<>();

        if (!(Objects.equals(oldTicket.getName(), newTicket.getName()))){
            List<Object> nameValues = new ArrayList<>();
            nameValues.add(oldTicket.getName());
            nameValues.add(newTicket.getName());
            changed_values.put("name", nameValues);
        }

        if (!(Objects.equals(oldTicket.getType(), newTicket.getType()))){
            List<Object> typeValues = new ArrayList<>();
            typeValues.add(oldTicket.getType());
            typeValues.add(newTicket.getType());
            changed_values.put("type", typeValues);
        }

        if (!(Objects.equals(oldTicket.getStatus(), newTicket.getStatus()))){
            List<Object> statusValues = new ArrayList<>();
            statusValues.add(oldTicket.getStatus());
            statusValues.add(newTicket.getStatus());
            changed_values.put("status", statusValues);
        }

        if (!(Objects.equals(oldTicket.getPriority(), newTicket.getPriority()))){
            List<Object> priorityValues = new ArrayList<>();
            priorityValues.add(oldTicket.getPriority());
            priorityValues.add(newTicket.getPriority());
            changed_values.put("priority", priorityValues);
        }

        if (!(Objects.equals(oldTicket.getDescription(), newTicket.getDescription()))){
            List<Object> descriptionValues = new ArrayList<>();
            descriptionValues.add(oldTicket.getDescription());
            descriptionValues.add(newTicket.getDescription());
            changed_values.put("description", descriptionValues);
        }

        if (!(Objects.equals(oldTicket.getEmployee(), newTicket.getEmployee()))){
            List<Object> employeeValues = new ArrayList<>();
            if (oldTicket.getEmployee() != null){
                employeeValues.add(oldTicket.getEmployee().getId());
            }else {
                employeeValues.add(0);
            }
            if (newTicket.getEmployee() != null){
                employeeValues.add(newTicket.getEmployee().getId());
            }else {
                employeeValues.add(0);
            }
            changed_values.put("employee", employeeValues);
        }

        if (!(Objects.equals(oldTicket.getProject(), newTicket.getProject()))){
            List<Object> projectValues = new ArrayList<>();
            projectValues.add(oldTicket.getProject().getId());
            projectValues.add(newTicket.getProject().getId());
            changed_values.put("project", projectValues);
        }


        return changed_values;

    }


    public List<List<String>> createLogInformation(List<TicketLog> ticketLogs){


        List<List<String>> changedInformation = new ArrayList<>();

        for (int i = 0; i< ticketLogs.size() - 1; i++){

            TicketLog oldTicketLog = ticketLogs.get(i);
            TicketLog newTicketLog = ticketLogs.get(i+1);

            HashMap<String, List<Object>> result = compare(oldTicketLog, newTicketLog);

            for (String key: result.keySet()) {
                List<String> valuesItem = new ArrayList<>();
                List<Object> values = result.get(key);

                valuesItem.add(key);

                if (values.get(0).getClass() == Integer.class){
                    valuesItem.add( values.get(0).toString());
                }else {
                    valuesItem.add((String) values.get(0));
                }

                if (values.get(1).getClass() == Integer.class){
                    valuesItem.add(values.get(1).toString());
                }else {
                    valuesItem.add((String) values.get(1));
                }

                SimpleDateFormat f = new SimpleDateFormat("dd:MM:yyyy / HH:mm");
                String timestampString = f.format(newTicketLog.getTimestamp());
                valuesItem.add(timestampString);
                changedInformation.add(valuesItem);

            }

        }

        return changedInformation;

    }

    public TicketLog createLog(Ticket oldTicket){


        long millies = System.currentTimeMillis();
        java.sql.Timestamp timestamp = new Timestamp(millies);

        TicketLog ticketLog = new TicketLog(oldTicket.getName(), oldTicket.getDescription(), oldTicket.getStatus(), oldTicket.getType(),
                oldTicket.getPriority(), timestamp);

        ticketLog.setTicket(oldTicket);
        ticketLog.setProject(oldTicket.getProject());
        ticketLog.setEmployee(oldTicket.getEmployee());

        return ticketLog;

    }




}
