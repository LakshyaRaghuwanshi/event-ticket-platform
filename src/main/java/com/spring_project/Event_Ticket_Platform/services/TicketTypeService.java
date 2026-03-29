package com.spring_project.Event_Ticket_Platform.services;

import com.spring_project.Event_Ticket_Platform.domain.entities.Ticket;

import java.util.UUID;

public interface TicketTypeService {
    Ticket purchaseTicket(UUID userId, UUID ticketTypeId);
}
