package org.finalproject.java.fp_spring.DTOs;

public class CustomerStatsDTO {

    private int pendingTickets;
    private int workingTickets;
    private int resolvedTickets;
    private int activeServices;

    public CustomerStatsDTO(int pendingTickets, int resolvedTickets, int activeServices, int workingTickets) {
        this.activeServices = activeServices;
        this.pendingTickets = pendingTickets;
        this.resolvedTickets = resolvedTickets;
        this.workingTickets = workingTickets;
    }

    public int getWorkingTickets() {
        return workingTickets;
    }

    public void setWorkingTickets(int workingTickets) {
        this.workingTickets = workingTickets;
    }

    public int getPendingTickets() {
        return this.pendingTickets;
    }

    public void setPendingTickets(int pendingTickets) {
        this.pendingTickets = pendingTickets;
    }

    public int getResolvedTickets() {
        return this.resolvedTickets;
    }

    public void setResolvedTickets(int resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

    public int getActiveServices() {
        return this.activeServices;
    }

    public void setActiveServices(int activeServices) {
        this.activeServices = activeServices;
    }

}
