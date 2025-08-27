package com.impulse.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.impulse.domain.tutor.Validation;
import com.impulse.application.ports.ValidationPort;

@Service
public class ValidationService {
    private final ValidationPort repo;
    public ValidationService(ValidationPort repo){this.repo=repo;}

    @Transactional
    public Validation submit(Long retoId, Long validatorId, String status, String feedback){
        return repo.save(new Validation(retoId, validatorId, status, feedback));
    }

    public List<Validation> list(){return repo.findAll();}
}
