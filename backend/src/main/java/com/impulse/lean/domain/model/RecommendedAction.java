package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Recommended Action Enum
 * 
 * Represents recommended actions that can be taken based on validation results
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum RecommendedAction {
    
    // Immediate actions
    APPROVE_IMMEDIATELY("Approve Immediately", "Evidence can be approved without further review"),
    REJECT_IMMEDIATELY("Reject Immediately", "Evidence should be rejected without further consideration"),
    
    // Review actions
    REQUEST_ADDITIONAL_INFO("Request Additional Information", "Ask submitter for more details or documentation"),
    REQUEST_CLARIFICATION("Request Clarification", "Ask submitter to clarify specific points"),
    REQUEST_REVISION("Request Revision", "Ask submitter to revise and resubmit evidence"),
    
    // Escalation actions
    ESCALATE_TO_SENIOR("Escalate to Senior Validator", "Refer to senior validator for expert opinion"),
    ESCALATE_TO_MODERATOR("Escalate to Moderator", "Refer to moderator for policy decision"),
    ESCALATE_TO_SPECIALIST("Escalate to Specialist", "Refer to subject matter expert"),
    
    // Quality actions
    FLAG_FOR_QUALITY_REVIEW("Flag for Quality Review", "Mark for quality assurance review"),
    FLAG_FOR_COMPLIANCE("Flag for Compliance Review", "Mark for regulatory compliance check"),
    FLAG_FOR_SECURITY("Flag for Security Review", "Mark for security assessment"),
    
    // Process actions
    EXTEND_DEADLINE("Extend Deadline", "Provide additional time for completion"),
    SPLIT_VALIDATION("Split Validation", "Break into multiple validation tasks"),
    MERGE_WITH_RELATED("Merge with Related Evidence", "Combine with related submissions"),
    
    // Documentation actions
    UPDATE_DOCUMENTATION("Update Documentation", "Improve or update related documentation"),
    CREATE_KNOWLEDGE_ARTICLE("Create Knowledge Article", "Document learnings for future reference"),
    UPDATE_VALIDATION_CRITERIA("Update Validation Criteria", "Refine validation standards"),
    
    // Training actions
    RECOMMEND_TRAINING("Recommend Training", "Suggest training for submitter"),
    SCHEDULE_MENTORING("Schedule Mentoring", "Arrange mentoring session"),
    
    // Administrative actions
    ARCHIVE_EVIDENCE("Archive Evidence", "Move to archive for future reference"),
    DELETE_EVIDENCE("Delete Evidence", "Remove evidence from system"),
    ANONYMIZE_DATA("Anonymize Data", "Remove personal identifiers"),
    
    // Notification actions
    NOTIFY_STAKEHOLDERS("Notify Stakeholders", "Inform relevant parties of decision"),
    PUBLISH_RESULTS("Publish Results", "Make results publicly available"),
    
    // Follow-up actions
    SCHEDULE_FOLLOW_UP("Schedule Follow-up", "Plan future review or check"),
    SET_REMINDER("Set Reminder", "Create reminder for future action"),
    CREATE_IMPROVEMENT_PLAN("Create Improvement Plan", "Develop plan for enhancement");

    private final String displayName;
    private final String description;

    RecommendedAction(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get the category of this action
     */
    public ActionCategory getCategory() {
        switch (this) {
            case APPROVE_IMMEDIATELY:
            case REJECT_IMMEDIATELY:
                return ActionCategory.IMMEDIATE;
                
            case REQUEST_ADDITIONAL_INFO:
            case REQUEST_CLARIFICATION:
            case REQUEST_REVISION:
                return ActionCategory.REVIEW;
                
            case ESCALATE_TO_SENIOR:
            case ESCALATE_TO_MODERATOR:
            case ESCALATE_TO_SPECIALIST:
                return ActionCategory.ESCALATION;
                
            case FLAG_FOR_QUALITY_REVIEW:
            case FLAG_FOR_COMPLIANCE:
            case FLAG_FOR_SECURITY:
                return ActionCategory.QUALITY;
                
            case EXTEND_DEADLINE:
            case SPLIT_VALIDATION:
            case MERGE_WITH_RELATED:
                return ActionCategory.PROCESS;
                
            case UPDATE_DOCUMENTATION:
            case CREATE_KNOWLEDGE_ARTICLE:
            case UPDATE_VALIDATION_CRITERIA:
                return ActionCategory.DOCUMENTATION;
                
            case RECOMMEND_TRAINING:
            case SCHEDULE_MENTORING:
                return ActionCategory.TRAINING;
                
            case ARCHIVE_EVIDENCE:
            case DELETE_EVIDENCE:
            case ANONYMIZE_DATA:
                return ActionCategory.ADMINISTRATIVE;
                
            case NOTIFY_STAKEHOLDERS:
            case PUBLISH_RESULTS:
                return ActionCategory.NOTIFICATION;
                
            case SCHEDULE_FOLLOW_UP:
            case SET_REMINDER:
            case CREATE_IMPROVEMENT_PLAN:
                return ActionCategory.FOLLOW_UP;
                
            default:
                return ActionCategory.OTHER;
        }
    }

    /**
     * Check if this action requires immediate attention
     */
    public boolean isUrgent() {
        return getCategory() == ActionCategory.IMMEDIATE || 
               this == ESCALATE_TO_MODERATOR ||
               this == FLAG_FOR_SECURITY;
    }

    /**
     * Check if this action requires external communication
     */
    public boolean requiresExternalCommunication() {
        return this == REQUEST_ADDITIONAL_INFO ||
               this == REQUEST_CLARIFICATION ||
               this == REQUEST_REVISION ||
               this == NOTIFY_STAKEHOLDERS ||
               this == PUBLISH_RESULTS;
    }

    /**
     * Action categories for grouping
     */
    public enum ActionCategory {
        IMMEDIATE, REVIEW, ESCALATION, QUALITY, PROCESS, 
        DOCUMENTATION, TRAINING, ADMINISTRATIVE, NOTIFICATION, 
        FOLLOW_UP, OTHER
    }
}
