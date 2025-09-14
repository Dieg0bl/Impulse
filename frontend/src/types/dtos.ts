// TypeScript interfaces matching backend DTOs exactly

import {
  UserStatus,
  ChallengeCategory,
  ChallengeDifficulty,
  ChallengeStatus,
  EvidenceType,
  EvidenceStatus,
  ValidationStatus,
  ValidationDecision,
  ValidationMethod,
  ValidationType,
  ValidationPriority,
  ValidatorStatus,
  ValidatorSpecialty,
  CoachStatus,
  CoachLevel,
  RatingType,
  NotificationType,
  InvitationStatus,
  ReportStatus,
  ReportReason,
  ReportPriority,
  ActionTaken,
  ContentType
} from './enums';// User DTOs
export interface UserResponseDto {
  id: number;
  uuid: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  profileImageUrl?: string;
  bio?: string;
  status: UserStatus;
  createdAt: string;
  updatedAt: string;
  lastLoginAt?: string;
  isActive: boolean;
  isVerified: boolean;
  challengesCreated: number;
  challengesCompleted: number;
  totalPoints: number;
}

export interface UserCreateRequestDto {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  password: string;
  bio?: string;
  profileImageUrl?: string;
}

export interface UserUpdateRequestDto {
  username?: string;
  firstName?: string;
  lastName?: string;
  bio?: string;
  profileImageUrl?: string;
  status?: UserStatus;
}

// Challenge DTOs
export interface ChallengeResponseDto {
  id: number;
  uuid: string;
  title: string;
  description: string;
  category: ChallengeCategory;
  difficulty: ChallengeDifficulty;
  status: ChallengeStatus;
  rewardAmount: number;
  rewardCurrency: string;
  startDate: string;
  endDate: string;
  createdAt: string;
  updatedAt: string;
  creatorId: number;
  creatorUsername: string;
  participantCount: number;
  maxParticipants?: number;
  isPublic: boolean;
}

export interface ChallengeCreateRequestDto {
  title: string;
  description: string;
  category: ChallengeCategory;
  difficulty: ChallengeDifficulty;
  rewardAmount?: number;
  rewardCurrency?: string;
  startDate?: string;
  endDate?: string;
  maxParticipants?: number;
  isPublic?: boolean;
}

export interface ChallengeUpdateRequestDto {
  title?: string;
  description?: string;
  category?: ChallengeCategory;
  difficulty?: ChallengeDifficulty;
  status?: ChallengeStatus;
  rewardAmount?: number;
  rewardCurrency?: string;
  startDate?: string;
  endDate?: string;
  maxParticipants?: number;
  isPublic?: boolean;
}

export interface ChallengeJoinRequestDto {
  challengeId: number;
  participationNotes?: string;
}

// Evidence DTOs
export interface EvidenceResponseDto {
  id: number;
  uuid: string;
  challengeId: number;
  userId: number;
  username: string;
  type: EvidenceType;
  filename: string;
  originalFilename: string;
  fileUrl: string;
  description?: string;
  metadata?: Record<string, any>;
  status: EvidenceStatus;
  validationScore?: number;
  submittedAt: string;
  validatedAt?: string;
  validatorId?: number;
  validatorUsername?: string;
}

export interface EvidenceCreateRequestDto {
  challengeId: number;
  type: EvidenceType;
  filename: string;
  description?: string;
  metadata?: Record<string, any>;
}

export interface EvidenceUpdateRequestDto {
  description?: string;
  metadata?: Record<string, any>;
}

// Validation DTOs
export interface ValidationResponseDto {
  id: number;
  uuid: string;
  evidenceId: number;
  validatorId: number;
  validatorUsername: string;
  status: ValidationStatus;
  decision?: ValidationDecision;
  method: ValidationMethod;
  type: ValidationType;
  priority: ValidationPriority;
  comments?: string;
  score?: number;
  confidence?: number;
  startedAt: string;
  completedAt?: string;
  metadata?: Record<string, any>;
}

export interface ValidationCreateRequestDto {
  evidenceId: number;
  method: ValidationMethod;
  type: ValidationType;
  priority?: ValidationPriority;
  assignedValidatorId?: number;
  comments?: string;
}

export interface ValidationUpdateRequestDto {
  status?: ValidationStatus;
  decision?: ValidationDecision;
  comments?: string;
  score?: number;
  confidence?: number;
  metadata?: Record<string, any>;
}

// Validator DTOs
export interface ValidatorResponseDto {
  id: number;
  uuid: string;
  userId: number;
  username: string;
  status: ValidatorStatus;
  specialty: ValidatorSpecialty;
  validationCount: number;
  successRate: number;
  averageScore: number;
  certifiedAt?: string;
  lastActiveAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ValidatorCreateRequestDto {
  userId: number;
  specialty: ValidatorSpecialty;
  certificationDocument?: string;
  experience?: string;
}

export interface ValidatorUpdateRequestDto {
  status?: ValidatorStatus;
  specialty?: ValidatorSpecialty;
  certificationDocument?: string;
  experience?: string;
}

// Coach DTOs
export interface CoachResponseDto {
  id: number;
  uuid: string;
  userId: number;
  username: string;
  firstName: string;
  lastName: string;
  bio?: string;
  profileImageUrl?: string;
  status: CoachStatus;
  level: CoachLevel;
  specialties: string[];
  hourlyRate?: number;
  currency?: string;
  rating: number;
  reviewCount: number;
  clientCount: number;
  yearsExperience?: number;
  certifications: string[];
  availableSlots: string[];
  createdAt: string;
  updatedAt: string;
}

export interface CoachCreateRequestDto {
  userId: number;
  bio?: string;
  specialties: string[];
  hourlyRate?: number;
  currency?: string;
  yearsExperience?: number;
  certifications?: string[];
  availableSlots?: string[];
}

export interface CoachUpdateRequestDto {
  bio?: string;
  status?: CoachStatus;
  level?: CoachLevel;
  specialties?: string[];
  hourlyRate?: number;
  currency?: string;
  yearsExperience?: number;
  certifications?: string[];
  availableSlots?: string[];
}

// Rating DTOs
export interface RatingResponseDto {
  id: number;
  uuid: string;
  userId: number;
  username: string;
  targetId: number;
  targetType: RatingType;
  rating: number;
  review?: string;
  isAnonymous: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface RatingCreateRequestDto {
  targetId: number;
  targetType: RatingType;
  rating: number;
  review?: string;
  isAnonymous?: boolean;
}

export interface RatingUpdateRequestDto {
  rating?: number;
  review?: string;
  isAnonymous?: boolean;
}

// Common DTOs
export interface PageRequestDto {
  page: number;
  size: number;
  sort?: string;
  direction?: 'ASC' | 'DESC';
}

export interface PageResponseDto<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ApiResponseDto<T> {
  success: boolean;
  data?: T;
  message?: string;
  errors?: string[];
  timestamp: string;
}

// Authentication DTOs
export interface LoginRequestDto {
  email: string;
  password: string;
}

export interface LoginResponseDto {
  token: string;
  refreshToken: string;
  user: UserResponseDto;
  expiresIn: number;
}

export interface RegisterRequestDto {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface RefreshTokenRequestDto {
  refreshToken: string;
}

// Notification DTOs
export interface NotificationResponseDto {
  id: number;
  uuid: string;
  userId: number;
  type: NotificationType;
  title: string;
  message: string;
  data?: Record<string, any>;
  isRead: boolean;
  createdAt: string;
  readAt?: string;
}

export interface NotificationCreateRequestDto {
  userId: number;
  type: NotificationType;
  title: string;
  message: string;
  data?: Record<string, any>;
}

// Invitation DTOs
export interface InvitationResponseDto {
  id: number;
  uuid: string;
  challengeId: number;
  challengeTitle: string;
  inviterId: number;
  inviterUsername: string;
  inviteeEmail: string;
  status: InvitationStatus;
  message?: string;
  createdAt: string;
  expiresAt: string;
  respondedAt?: string;
}

export interface InvitationCreateRequestDto {
  challengeId: number;
  inviteeEmail: string;
  message?: string;
  expiresAt?: string;
}

// Report DTOs
export interface ReportResponseDto {
  id: number;
  uuid: string;
  reporterId: number;
  reporterUsername: string;
  targetId: number;
  targetType: ContentType;
  reason: ReportReason;
  description?: string;
  status: ReportStatus;
  priority: ReportPriority;
  actionTaken?: ActionTaken;
  moderatorId?: number;
  moderatorUsername?: string;
  createdAt: string;
  resolvedAt?: string;
}

export interface ReportCreateRequestDto {
  targetId: number;
  targetType: ContentType;
  reason: ReportReason;
  description?: string;
}

// Statistics DTOs
export interface UserStatsDto {
  userId: number;
  challengesCreated: number;
  challengesCompleted: number;
  challengesWon: number;
  totalPoints: number;
  currentStreak: number;
  longestStreak: number;
  averageRating: number;
  evidenceSubmitted: number;
  evidenceApproved: number;
}

export interface ChallengeStatsDto {
  challengeId: number;
  participantCount: number;
  completionRate: number;
  averageRating: number;
  evidenceCount: number;
  validationCount: number;
  avgCompletionTime: number;
}

export interface PlatformStatsDto {
  totalUsers: number;
  activeUsers: number;
  totalChallenges: number;
  activeChallenges: number;
  totalEvidence: number;
  pendingValidations: number;
  completionRate: number;
  avgUserRating: number;
}
