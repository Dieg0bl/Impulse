package com.impulse.moderation;
/** Enumeraciones centralizadas para flujos de moderación/DSA. */
public final class ModerationEnums {
    private ModerationEnums(){}
    public enum ContentType { challenge, evidence, comment, profile }
    public enum ReportReason { copyright, privacy, intimate, harassment, hate, other }
    public enum ReportStatus { received, triaged, actioned, dismissed }
    public enum ActionType { remove, restrict, suspend, no_action }
    public enum AppealStatus { received, upheld, reversed, partial }
}
