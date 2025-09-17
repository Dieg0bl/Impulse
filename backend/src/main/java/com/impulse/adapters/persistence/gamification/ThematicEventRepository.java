package com.impulse.adapters.persistence.gamification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThematicEventRepository extends JpaRepository<ThematicEvent, String> {
}
