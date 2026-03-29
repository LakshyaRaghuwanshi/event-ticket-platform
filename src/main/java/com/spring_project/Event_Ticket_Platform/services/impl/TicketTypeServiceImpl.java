package com.spring_project.Event_Ticket_Platform.services.impl;

import com.spring_project.Event_Ticket_Platform.domain.entities.Ticket;
import com.spring_project.Event_Ticket_Platform.domain.entities.TicketType;
import com.spring_project.Event_Ticket_Platform.domain.entities.User;
import com.spring_project.Event_Ticket_Platform.domain.enums.TicketStatusEnum;
import com.spring_project.Event_Ticket_Platform.exceptions.TicketTypeNotFoundException;
import com.spring_project.Event_Ticket_Platform.exceptions.TicketsSoldOutException;
import com.spring_project.Event_Ticket_Platform.exceptions.UserNotFoundException;
import com.spring_project.Event_Ticket_Platform.repositories.TicketRepository;
import com.spring_project.Event_Ticket_Platform.repositories.TicketTypeRepository;
import com.spring_project.Event_Ticket_Platform.repositories.UserRepository;
import com.spring_project.Event_Ticket_Platform.services.QrCodeService;
import com.spring_project.Event_Ticket_Platform.services.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final QrCodeService qrCodeService;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                    String.format("User with ID %s was not found", userId)
                ));

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId)
                .orElseThrow(() -> new TicketTypeNotFoundException(
                    String.format("Ticket Type with ID %s was not found", ticketTypeId)
                ));

        int purchasedTickets = ticketRepository.countByTicketTypeId(ticketTypeId);
        Integer totalAvailable = ticketType.getTotalAvailable();

        if (purchasedTickets + 1 > totalAvailable) {
            throw new TicketsSoldOutException();
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);

        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQrCode(savedTicket);

        return ticketRepository.save(savedTicket);
    }

}
