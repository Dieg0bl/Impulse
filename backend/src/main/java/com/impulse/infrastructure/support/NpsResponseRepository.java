package com.impulse.infrastructure.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.impulse.domain.support.NpsResponse;

@Repository
public interface NpsResponseRepository extends JpaRepository<NpsResponse, Long> {
}
