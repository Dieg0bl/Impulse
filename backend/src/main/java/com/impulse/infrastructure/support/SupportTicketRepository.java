package com.impulse.infrastructure.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.support.SupportTicket;
import com.impulse.domain.support.SupportTicketRepositoryPort;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long>, SupportTicketRepositoryPort {
	default java.util.Optional<SupportTicket> find(Long id) { return findById(id); }
	default SupportTicket store(SupportTicket t) { return save(t); }
	default java.util.List<SupportTicket> listAll() { return findAll(); }
	default void removeById(Long id) { deleteById(id); }
}
