package com.spring_project.Event_Ticket_Platform.services;

import com.spring_project.Event_Ticket_Platform.domain.entities.TicketValidation;

import java.util.UUID;

public interface TicketValidationService {
    TicketValidation validateTicketByQrCode(UUID qrCodeId);
    TicketValidation validateTicketManually(UUID ticketId);
}
