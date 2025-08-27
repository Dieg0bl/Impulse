package com.impulse.application.ports;

import com.impulse.domain.support.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SupportTicketPort extends JpaRepository<SupportTicket, Long> {}
