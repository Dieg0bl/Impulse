package com.impulse.infrastructure.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.support.SupportTicket;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
}
