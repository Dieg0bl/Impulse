package com.impulse.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.impulse.domain.support.SupportTicket;
import com.impulse.infrastructure.support.SupportTicketRepository;

@Service
public class SupportService {
    private final SupportTicketRepository repo;
    public SupportService(SupportTicketRepository repo){this.repo=repo;}

    @Transactional
    public SupportTicket create(Long userId, String subject, String body){
        return repo.save(new SupportTicket(userId, subject, body));
    }

    public List<SupportTicket> list(){
        return repo.findAll();
    }

    @Transactional
    public SupportTicket close(Long id){
        var t = repo.findById(id).orElseThrow();
        t.setStatus("CLOSED");
        return repo.save(t);
    }
}
