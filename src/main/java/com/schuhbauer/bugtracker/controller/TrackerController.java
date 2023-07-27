package com.schuhbauer.bugtracker.controller;

import com.schuhbauer.bugtracker.bugTrackerDAO.EmployeeRepo;
import com.schuhbauer.bugtracker.bugTrackerDAO.ProjectRepo;
import com.schuhbauer.bugtracker.bugTrackerDAO.TicketRepo;
import com.schuhbauer.bugtracker.entity.*;
import com.schuhbauer.bugtracker.service.TrackerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

//@RestController
@Controller
@RequestMapping("/")
public class TrackerController {

    final
    TicketRepo ticketRepo;
    EmployeeRepo employeeRepo;

    ProjectRepo projectRepo;

    TrackerService trackerService;

    public TrackerController(TicketRepo ticketRepo, EmployeeRepo employeeRepo, ProjectRepo projectRepo, TrackerService trackerService) {
        this.ticketRepo = ticketRepo;
        this.employeeRepo = employeeRepo;
        this.projectRepo = projectRepo;
        this.trackerService = trackerService;
    }



    @GetMapping("/")
    public String showHome(){
        return "redirect:/tickets";
    }


    //tickets

    @GetMapping("/tickets/delete")
    public String deleteTicket(@RequestParam("ticketId") int ticketId){

        if (ticketId != 0){
            trackerService.deleteTicketById(ticketId);
        }

        return "redirect:/tickets";
    }



    @PostMapping("/tickets/save")
    public String saveTicket(@ModelAttribute("ticket") Ticket ticket,
                             @RequestParam("project_id") int project_id, @RequestParam("employee_id")int employee_id){

        trackerService.saveTicket(ticket, employee_id, project_id);
        return "redirect:/tickets";
    }



    @GetMapping("/tickets/addTicket")
    public String addTicket(Model model){

        Ticket ticket = new Ticket();

        List<Employee> employees = trackerService.getAllEmployees();
        List<Project> projects = trackerService.getAllProjects();

        model.addAttribute("employees", employees);
        model.addAttribute("ticket", ticket);
        model.addAttribute("projects", projects);

        List<TicketLog> emptyList = new ArrayList<>();
        model.addAttribute("logs", emptyList);

        return "ticketEditor";
    }

    @GetMapping("/tickets/update")
    public String updateTicket(@RequestParam ("ticketId") int thisTicketId, Model model){

        Ticket ticket = trackerService.getTicketById(thisTicketId);
        List<Employee> employees = trackerService.getAllEmployees();
        List<Project> projects = trackerService.getAllProjects();
        List<List<String>> logs = trackerService.createLogInformation(trackerService.findAllLogsByTicketId(thisTicketId));

        model.addAttribute("ticket", ticket);
        model.addAttribute("employees", employees);
        model.addAttribute("projects", projects);
        model.addAttribute("logs", logs);

        return "ticketEditor";
    }



    @RequestMapping(value = "/tickets", method = {RequestMethod.GET, RequestMethod.POST})
    public String getTicketListBySearch(Model model,
                                        @RequestParam (value = "search", required = false) String search,
                                        @RequestParam(value = "include_all", required = false) String include_all,
                                        @RequestParam(value = "start", required = false) String startDate,
                                        @RequestParam(value = "end", required = false) String endDate,
                                        @RequestParam(value = "employeeId", required = false) Integer employeeId,
                                        @RequestParam(value = "projectId", required = false) Integer projectId,
                                        Authentication authentication){


        List<Ticket> tickets;

        HashMap<String, Long> priorityHashMap;
        HashMap<String, Long> typeHashMap;
        HashMap<String, Long> statusHashMap;

        List<Project> projects = trackerService.getAllProjects();
        List<Employee> employees = trackerService.getAllEmployees();

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String userName = user.getUsername();
        Employee loginEmployee = trackerService.findEmployeeByUserName(userName);



        //start
        Timestamp start;
        if (startDate == null){
            startDate = "";
        }

        if (startDate.length() != 0){
            startDate = startDate + " 00:00:00";
            start = Timestamp.valueOf(startDate);
        }else {
            startDate = LocalDate.now().minusDays(7).toString();
            startDate = startDate + " 00:00:00";
            start = Timestamp.valueOf(startDate);
        }



        //end
        Timestamp end;
        if (endDate == null){
            endDate = "";
        }

        if (endDate.length() != 0){
            endDate = endDate + " 23:59:59";
            end = Timestamp.valueOf(endDate);
        }else {
            endDate = LocalDate.now().toString();
            endDate = endDate + " 23:59:59";
            end = Timestamp.valueOf(endDate);
        }


        // employeeId == -1 means unassigned -- employeeId == 0 means all
        List<Integer> employeeIdList = new ArrayList<>();

        if (employeeId == null){

            employeeIdList.add(loginEmployee.getId());
            employeeId = loginEmployee.getId();

        }else if (employeeId == 0){
            for (Employee empl: employees) {
                employeeIdList.add(empl.getId());
            }
            employeeIdList.add(-1);

        }else if (employeeId == -1){
            employeeIdList.add(-1);

        }else {
            employeeIdList.add(employeeId);
        }




        //projectId
        List<Integer> projectIdList = new ArrayList<>();
        if (projectId == null || projectId == 0){
            for (Project pro: projects) {
                projectIdList.add(pro.getId());
            }
        }else {
            projectIdList.add(projectId);
        }



        //status
        List<String> statusList = new ArrayList<>();
        statusList.add("active");
        statusList.add("waiting");
        statusList.add("closed");
        if (include_all == null){
            statusList.remove("closed");
        }



        //search
        if (search == null){
            search = "";
        }


        tickets = trackerService.findAndFilterAllTickets(search, start, end, employeeIdList, projectIdList, statusList);
        priorityHashMap = trackerService.getPriorityCountHashMap(search, start, end, employeeIdList, projectIdList, statusList);
        typeHashMap = trackerService.getTypeCountHashMap(search, start, end, employeeIdList, projectIdList, statusList);
        statusHashMap = trackerService.getStatusCountHashMap(search, start, end, employeeIdList, projectIdList, statusList);


        List<Integer> createdAndClosedCount = trackerService.createdAndClosedInDays(7, tickets);
        HashMap<String,Long> projectMap = trackerService.getProjectCountHashMap(search, start, end, employeeIdList, projectIdList, statusList);



        //new last week && closed last week
        model.addAttribute("search", search);
        model.addAttribute("include_all", include_all);
        model.addAttribute("start", startDate.substring(0,10));
        model.addAttribute("end", endDate.substring(0,10));

        model.addAttribute("projects", projects);
        model.addAttribute("employees", employees);

        model.addAttribute("tickets", tickets);
        model.addAttribute("data_priority", priorityHashMap.values());
        model.addAttribute("data_status", statusHashMap.values());
        model.addAttribute("data_type", typeHashMap.values());

        model.addAttribute("employeeId", employeeId);
        model.addAttribute("projectId", projectId);

        model.addAttribute("count_new", createdAndClosedCount.get(0));
        model.addAttribute("count_closed", createdAndClosedCount.get(1));

        model.addAttribute("projectMapKeys", projectMap.keySet());
        model.addAttribute("projectMapValues", projectMap.values());

        return "ticketList";
    }




    //----------------------------employees-----------------------------------



    @GetMapping("/employeeDetails")
    public String employeeDetails(Model model, @RequestParam ("employeeId") int employeeId){

        Employee employee = trackerService.getEmployeeById(employeeId);
        List<Ticket> tickets = trackerService.getTicketsByEmployeeId(employeeId);
        List<Project> projects = trackerService.getProjectsByEmployeeId(employeeId);

        List<Employee> employees = trackerService.getAllEmployees();
        List<Project> allProjects = trackerService.getAllProjects();

        model.addAttribute("employees", employees);
        model.addAttribute("allProjects", allProjects);

        model.addAttribute("employee", employee);


        model.addAttribute("ticketsByEmployeeId", tickets);
        model.addAttribute("projectsByEmployeeId", projects);

        model.addAttribute("data_priority", trackerService.getPriorityCountHashMapByEmployeeId(employeeId).values());
        model.addAttribute("data_status", trackerService.getStatusCountHashMapByEmployeeId(employeeId).values());
        model.addAttribute("data_type", trackerService.getTypeCountHashMapByEmployeeId(employeeId).values());

        return "employeeDetails";
    }




    //-------------------------projects--------------------------------



    @GetMapping("/projectDetails")
    public String projectDetails(Model model, @RequestParam ("projectId") int projectId){


        Project project = trackerService.getProjectById(projectId);
        List<Ticket> tickets = trackerService.getTicketsByProjectId(projectId);
        List<Employee> employees = trackerService.getEmployeesByProjectId(projectId);

        List<Project> allProjects = trackerService.getAllProjects();
        List<Employee> allEmployees = trackerService.getAllEmployees();

        model.addAttribute("allProjects", allProjects);
        model.addAttribute("allEmployees", allEmployees);

        model.addAttribute("project", project);
        model.addAttribute("ticketsByProjectId", tickets);
        model.addAttribute("employeesByProjectId", employees);

        model.addAttribute("data_priority", trackerService.getPriorityCountHashMapByProjectId(projectId).values());
        model.addAttribute("data_status", trackerService.getStatusCountHashMapByProjectId(projectId).values());
        model.addAttribute("data_type", trackerService.getTypeCountHashMapByProjectId(projectId).values());

        return "projectDetails";

    }




}
