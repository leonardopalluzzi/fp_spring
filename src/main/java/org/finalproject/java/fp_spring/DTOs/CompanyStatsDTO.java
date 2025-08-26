package org.finalproject.java.fp_spring.DTOs;

public class CompanyStatsDTO {
    private int activeServices;
    private int allServices;
    private int employeeNumber;
    private int customerNumber;
    private int openTickets;
    private int pendingTickets;
    private int closedTickets;

    public CompanyStatsDTO(int activeServices, int allServices, int employeeNumber, int customerNumber, int openTickets,
            int pendingTickets, int closedTickets) {
        this.activeServices = activeServices;
        this.allServices = allServices;
        this.employeeNumber = employeeNumber;
        this.customerNumber = customerNumber;
        this.openTickets = openTickets;
        this.pendingTickets = pendingTickets;
        this.closedTickets = closedTickets;
    }

    public int getActiveServices() {
        return this.activeServices;
    }

    public void setActiveServices(int activeServices) {
        this.activeServices = activeServices;
    }

    public int getAllServices() {
        return this.allServices;
    }

    public void setAllServices(int allServices) {
        this.allServices = allServices;
    }

    public int getEmployeeNumber() {
        return this.employeeNumber;
    }

    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public int getCustomerNumber() {
        return this.customerNumber;
    }

    public void setCustomerNumber(int customerNumber) {
        this.customerNumber = customerNumber;
    }

    public int getOpenTickets() {
        return this.openTickets;
    }

    public void setOpenTickets(int openTickets) {
        this.openTickets = openTickets;
    }

    public int getPendingTickets() {
        return this.pendingTickets;
    }

    public void setPendingTickets(int pendingTickets) {
        this.pendingTickets = pendingTickets;
    }

    public int getClosedTickets() {
        return this.closedTickets;
    }

    public void setClosedTickets(int closedTickets) {
        this.closedTickets = closedTickets;
    }

}
