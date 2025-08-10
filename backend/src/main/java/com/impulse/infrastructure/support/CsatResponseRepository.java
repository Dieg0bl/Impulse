package com.impulse.infrastructure.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.support.CsatResponse;

@Repository
public interface CsatResponseRepository extends JpaRepository<CsatResponse, Long> {
}
