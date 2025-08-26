package org.finalproject.java.fp_spring.DTOs;

public class EmployeeStatsDTO {

    private int assignedTickets;
    private int resolvedTickets;
    private int poolTickets;

    public EmployeeStatsDTO(int assignedTickets, int resolvedTickets, int poolTickets) {
        this.assignedTickets = assignedTickets;
        this.resolvedTickets = resolvedTickets;
        this.poolTickets = poolTickets;
    }

    public int getAssignedTickets() {
        return this.assignedTickets;
    }

    public void setAssignedTickets(int assignedTickets) {
        this.assignedTickets = assignedTickets;
    }

    public int getResolvedTickets() {
        return this.resolvedTickets;
    }

    public void setResolvedTickets(int resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

    public int getPoolTickets() {
        return this.poolTickets;
    }

    public void setPoolTickets(int poolTickets) {
        this.poolTickets = poolTickets;
    }

}
