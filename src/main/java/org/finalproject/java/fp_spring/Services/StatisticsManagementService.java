package org.finalproject.java.fp_spring.Services;

import org.finalproject.java.fp_spring.DTOs.CompanyStatsDTO;
import org.finalproject.java.fp_spring.DTOs.CustomerStatsDTO;
import org.finalproject.java.fp_spring.DTOs.EmployeeStatsDTO;
import org.finalproject.java.fp_spring.Enum.ServiceStatus;
import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.CompanyRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsManagementService {
        @Autowired
        CompanyRepository companyRepo;

        @Autowired
        UserService userService;

        public CompanyStatsDTO getCompanyStats(DatabaseUserDetails currentUser) throws NotFoundException {

                // recupera copmany da userid
                Company company = companyRepo.findById(currentUser.getCompany().getId())
                                .orElseThrow(() -> new NotFoundException("Compnay Not Found"));

                // recupera stats company
                CompanyStatsDTO companyStats = new CompanyStatsDTO(0, 0, 0, 0, 0, 0, 0);

                companyStats.setActiveServices(
                                (int) company.getServices().stream()
                                                .filter(s -> s.getStatus().equals(ServiceStatus.ACTIVE)).count());
                companyStats.setAllServices((int) company.getServices().size());
                companyStats.setClosedTickets((int) company.getServices().stream().flatMap(
                                s -> s.getTickets().stream().filter(t -> t.getStatus().equals(TicketStatus.RESOLVED)))
                                .count());
                companyStats.setCustomerNumber(
                                (int) company.getServices().stream().flatMap(s -> s.getCustomers().stream()).count());
                companyStats.setEmployeeNumber(
                                (int) company.getServices().stream().flatMap(s -> s.getOperators().stream()).count());
                companyStats.setOpenTickets((int) company.getServices().stream().flatMap(
                                s -> s.getTickets().stream().filter(t -> t.getStatus().equals(TicketStatus.WORKING)))
                                .count());
                companyStats.setPendingTickets((int) company.getServices().stream().flatMap(
                                s -> s.getTickets().stream().filter(t -> t.getStatus().equals(TicketStatus.PENDING)))
                                .count());

                // return
                return companyStats;

        }

        public EmployeeStatsDTO getEmployeeStats(DatabaseUserDetails currentUser) throws NotFoundException {
                User user = userService.findById(currentUser.getId())
                                .orElseThrow(() -> new NotFoundException("User Not Found"));

                EmployeeStatsDTO employeeStats = new EmployeeStatsDTO(0, 0, 0);

                employeeStats.setAssignedTickets((int) user.getAdminTickets().size());
                employeeStats.setPoolTickets((int) user.getServices().stream()
                                .flatMap(s -> s.getTickets().stream().filter(t -> t.getAssignedTo() == null)).count());
                employeeStats.setResolvedTickets(
                                (int) user.getAdminTickets().stream()
                                                .filter(t -> t.getStatus().equals(TicketStatus.RESOLVED)).count());

                return employeeStats;

        }

        public CustomerStatsDTO getCustomerStats(DatabaseUserDetails currentUser) throws NotFoundException {
                User user = userService.findById(currentUser.getId())
                                .orElseThrow(() -> new NotFoundException("User Not Found"));

                CustomerStatsDTO customerStats = new CustomerStatsDTO(0, 0, 0, 0);

                customerStats.setActiveServices(
                                (int) user.getCustomerServices().stream()
                                                .filter(s -> s.getStatus().equals(ServiceStatus.ACTIVE)).count());
                customerStats.setPendingTickets((int) user.getCustomerServices().stream()
                                .flatMap(s -> s.getTickets().stream()
                                                .filter(t -> t.getStatus().equals(TicketStatus.PENDING) && t.getRequester().getId().equals(user.getId())))
                                .count());
                customerStats.setResolvedTickets((int) user.getCustomerServices().stream()
                                .flatMap(s -> s.getTickets().stream()
                                                .filter(t -> t.getStatus().equals(TicketStatus.RESOLVED)  && t.getRequester().getId().equals(user.getId())))
                                .count());
                customerStats.setWorkingTickets((int) user.getCustomerServices().stream()
                                .flatMap(s -> s.getTickets().stream()
                                                .filter(t -> t.getStatus().equals(TicketStatus.WORKING)  && t.getRequester().getId().equals(user.getId())))
                                .count());

                return customerStats;
        }

}
