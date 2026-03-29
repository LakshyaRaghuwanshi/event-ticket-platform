package com.spring_project.Event_Ticket_Platform.services;

import com.spring_project.Event_Ticket_Platform.domain.entities.QrCode;
import com.spring_project.Event_Ticket_Platform.domain.entities.Ticket;

import java.util.OptionalInt;
import java.util.UUID;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);
}
