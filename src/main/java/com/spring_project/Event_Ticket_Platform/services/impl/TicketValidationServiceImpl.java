package com.spring_project.Event_Ticket_Platform.services.impl;

import com.spring_project.Event_Ticket_Platform.domain.entities.QrCode;
import com.spring_project.Event_Ticket_Platform.domain.entities.Ticket;
import com.spring_project.Event_Ticket_Platform.domain.entities.TicketValidation;
import com.spring_project.Event_Ticket_Platform.domain.enums.QrCodeStatusEnum;
import com.spring_project.Event_Ticket_Platform.domain.enums.TicketValidationMethod;
import com.spring_project.Event_Ticket_Platform.domain.enums.TicketValidationStatusEnum;
import com.spring_project.Event_Ticket_Platform.exceptions.QrCodeNotFoundException;
import com.spring_project.Event_Ticket_Platform.exceptions.TicketNotFoundException;
import com.spring_project.Event_Ticket_Platform.repositories.QrCodeRepository;
import com.spring_project.Event_Ticket_Platform.repositories.TicketRepository;
import com.spring_project.Event_Ticket_Platform.repositories.TicketValidationRepository;
import com.spring_project.Event_Ticket_Platform.services.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketValidationServiceImpl implements TicketValidationService {

    private final TicketValidationRepository ticketValidationRepository;
    private final QrCodeRepository qrCodeRepository;
    private final TicketRepository ticketRepository;

    @Override
    public TicketValidation validateTicketByQrCode(UUID qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeStatusEnum.ACTIVE)
                .orElseThrow(() -> new QrCodeNotFoundException(
                        String.format(
                                "QR Code with ID %s was not found", qrCodeId
                        )
                ));

        Ticket ticket = qrCode.getTicket();

        return validateTicket(ticket, TicketValidationMethod.QR_SCAN);
    }

    private TicketValidation validateTicket(Ticket ticket,
                                            TicketValidationMethod ticketValidationMethod) {
        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setTicket(ticket);

        ticketValidation.setValidationMethod(ticketValidationMethod);

        TicketValidationStatusEnum ticketValidationStatus = ticket.getValidations()
                .stream()
                .filter(v -> TicketValidationStatusEnum.VALID.equals(v.getStatus()))
                .findFirst()
                .map(v -> TicketValidationStatusEnum.INVALID)
                .orElse(TicketValidationStatusEnum.VALID);

        ticketValidation.setStatus(ticketValidationStatus);

        return ticketValidationRepository.save(ticketValidation);
    }

    @Override
    public TicketValidation validateTicketManually(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);

        return validateTicket(ticket, TicketValidationMethod.MANUAL);
    }
}
