package com.impulse.domain.support;

import java.util.Optional;
import java.util.List;

public interface SupportTicketRepositoryPort {
    Optional<SupportTicket> find(Long id);
    SupportTicket store(SupportTicket ticket);
    List<SupportTicket> listAll();
    void removeById(Long id);
}
