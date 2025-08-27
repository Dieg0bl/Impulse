package com.impulse.application.ports;

import com.impulse.domain.tutor.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TutorPort extends JpaRepository<Tutor, Long> {
    // puerto para operaciones sobre Tutor
}
