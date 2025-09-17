package com.impulse.application.coachingsession.mapper;

import com.impulse.application.coachingsession.dto.CreateCoachingSessionCommand;
import com.impulse.application.coachingsession.dto.CoachingSessionResponse;
import com.impulse.domain.coachingsession.CoachingSession;
import org.springframework.stereotype.Component;

@Component
public class CoachingSessionAppMapper {
    public CoachingSession toDomain(CreateCoachingSessionCommand command) {
        return CoachingSession.builder()
                .id(null)
                .studentId(command.getStudentId())
                .coachId(command.getCoachId())
                .scheduledDate(command.getScheduledDate())
                .startTime(command.getStartTime())
                .endTime(command.getEndTime())
                .status(command.getStatus())
                .notes(command.getNotes())
                .feedback(command.getFeedback())
                .rating(command.getRating())
                .completed(command.getCompleted())
                .build();
    }
    public CoachingSessionResponse toResponse(CoachingSession coachingSession) {
        return CoachingSessionResponse.builder()
                .id(coachingSession.getId() != null ? coachingSession.getId().getValue() : null)
                .studentId(coachingSession.getStudentId())
                .coachId(coachingSession.getCoachId())
                .scheduledDate(coachingSession.getScheduledDate())
                .startTime(coachingSession.getStartTime())
                .endTime(coachingSession.getEndTime())
                .status(coachingSession.getStatus())
                .notes(coachingSession.getNotes())
                .feedback(coachingSession.getFeedback())
                .rating(coachingSession.getRating())
                .completed(coachingSession.getCompleted())
                .build();
    }
}
