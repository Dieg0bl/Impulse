import axios from "axios";
import mockStore from "./mockStore";
import {
  ApiResponseDto,
  PageResponseDto,
  UserResponseDto,
  UserCreateRequestDto,
  UserUpdateRequestDto,
  ChallengeResponseDto,
  ChallengeCreateRequestDto,
  ChallengeUpdateRequestDto,
  ChallengeJoinRequestDto,
  EvidenceResponseDto,
  EvidenceCreateRequestDto,
  ValidationResponseDto,
  ValidationCreateRequestDto,
  ValidationUpdateRequestDto,
  ValidatorResponseDto,
  CoachResponseDto,
  RatingResponseDto,
  RatingCreateRequestDto,
  LoginRequestDto,
  LoginResponseDto,
  RegisterRequestDto,
  NotificationResponseDto,
  InvitationResponseDto,
  InvitationCreateRequestDto,
  ReportResponseDto,
  ReportCreateRequestDto,
  UserStatsDto,
  ChallengeStatsDto,
  PlatformStatsDto,
  PageRequestDto,
  ForgotPasswordRequestDto,
  ResetPasswordRequestDto,
  VerifyEmailRequestDto,
} from "../types/dtos";
import { EvidenceType, EvidenceStatus } from "../types/enums";

const BASE =
  import.meta.env.VITE_API_BASE || import.meta.env.VITE_API_URL || "http://localhost:8080";

const http = axios.create({
  baseURL: BASE,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

// Attach token if present in localStorage
http.interceptors.request.use((cfg) => {
  const token = localStorage.getItem("token");
  if (token) {
    cfg.headers = Object.assign(cfg.headers || {}, { Authorization: `Bearer ${token}` });
  }
  return cfg;
});

// Response interceptor for standardized error handling
http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(new Error(error?.response?.data?.message || "Request failed"));
  },
);

// Authentication API
export const authApi = {
  login: async (data: LoginRequestDto): Promise<LoginResponseDto> => {
    const response = await http.post<ApiResponseDto<LoginResponseDto>>("/api/v1/auth/login", data);
    return response.data.data!;
  },

  register: async (data: RegisterRequestDto): Promise<UserResponseDto> => {
    const response = await http.post<ApiResponseDto<UserResponseDto>>(
      "/api/v1/auth/register",
      data,
    );
    return response.data.data!;
  },

  refreshToken: async (refreshToken: string): Promise<LoginResponseDto> => {
    const response = await http.post<ApiResponseDto<LoginResponseDto>>("/api/v1/auth/refresh", {
      refreshToken,
    });
    return response.data.data!;
  },

  logout: async (): Promise<void> => {
    await http.post("/api/v1/auth/logout");
  },

  forgotPassword: async (data: ForgotPasswordRequestDto): Promise<string> => {
    const response = await http.post<ApiResponseDto<any>>("/api/v1/auth/forgot-password", data);
    return response.data.message || "Solicitud enviada";
  },

  resetPassword: async (
    data: ResetPasswordRequestDto,
    options?: { headers?: Record<string, string> }
  ): Promise<string> => {
    const response = await http.post<ApiResponseDto<any>>(
      "/api/v1/auth/reset-password",
      data,
      options ? { headers: options.headers } : undefined
    );
    return response.data.message || "Contraseña actualizada";
  },

  verifyEmail: async (
    data: VerifyEmailRequestDto,
    options?: { headers?: Record<string, string> }
  ): Promise<string> => {
    const response = await http.post<ApiResponseDto<any>>(
      "/api/v1/auth/verify-email",
      data,
      options ? { headers: options.headers } : undefined
    );
    return response.data.message || "Email verificado";
  },
};

// User API
export const userApi = {
  getUsers: async (params?: PageRequestDto): Promise<PageResponseDto<UserResponseDto>> => {
    const response = await http.get<ApiResponseDto<PageResponseDto<UserResponseDto>>>(
      "/api/v1/users",
      { params },
    );
    return response.data.data!;
  },

  getUserById: async (id: number): Promise<UserResponseDto> => {
    const response = await http.get<ApiResponseDto<UserResponseDto>>(`/api/v1/users/${id}`);
    return response.data.data!;
  },

  getCurrentUser: async (): Promise<UserResponseDto> => {
    const response = await http.get<ApiResponseDto<UserResponseDto>>("/api/v1/users/me");
    return response.data.data!;
  },

  createUser: async (data: UserCreateRequestDto): Promise<UserResponseDto> => {
    const response = await http.post<ApiResponseDto<UserResponseDto>>("/api/v1/users", data);
    return response.data.data!;
  },

  updateUser: async (id: number, data: UserUpdateRequestDto): Promise<UserResponseDto> => {
    const response = await http.put<ApiResponseDto<UserResponseDto>>(`/api/v1/users/${id}`, data);
    return response.data.data!;
  },

  deleteUser: async (id: number): Promise<void> => {
    await http.delete(`/api/v1/users/${id}`);
  },

  getUserStats: async (id: number): Promise<UserStatsDto> => {
    const response = await http.get<ApiResponseDto<UserStatsDto>>(`/api/v1/users/${id}/stats`);
    return response.data.data!;
  },
};

// Challenge API
export const challengeApi = {
  getChallenges: async (
    params?: PageRequestDto,
  ): Promise<PageResponseDto<ChallengeResponseDto>> => {
    const response = await http.get<ApiResponseDto<PageResponseDto<ChallengeResponseDto>>>(
      "/api/v1/challenges",
      { params },
    );
    return response.data.data!;
  },

  getChallengeById: async (id: number): Promise<ChallengeResponseDto> => {
    const response = await http.get<ApiResponseDto<ChallengeResponseDto>>(
      `/api/v1/challenges/${id}`,
    );
    return response.data.data!;
  },

  createChallenge: async (data: ChallengeCreateRequestDto): Promise<ChallengeResponseDto> => {
    const response = await http.post<ApiResponseDto<ChallengeResponseDto>>(
      "/api/v1/challenges",
      data,
    );
    return response.data.data!;
  },

  updateChallenge: async (
    id: number,
    data: ChallengeUpdateRequestDto,
  ): Promise<ChallengeResponseDto> => {
    const response = await http.put<ApiResponseDto<ChallengeResponseDto>>(
      `/api/v1/challenges/${id}`,
      data,
    );
    return response.data.data!;
  },

  deleteChallenge: async (id: number): Promise<void> => {
    await http.delete(`/api/v1/challenges/${id}`);
  },

  joinChallenge: async (data: ChallengeJoinRequestDto): Promise<void> => {
    await http.post(`/api/v1/challenges/${data.challengeId}/join`, data);
  },

  leaveChallenge: async (challengeId: number): Promise<void> => {
    await http.post(`/api/v1/challenges/${challengeId}/leave`);
  },

  getChallengeStats: async (id: number): Promise<ChallengeStatsDto> => {
    const response = await http.get<ApiResponseDto<ChallengeStatsDto>>(
      `/api/v1/challenges/${id}/stats`,
    );
    return response.data.data!;
  },
};

// Evidence API
export const evidenceApi = {
  getEvidence: async (
    challengeId: number,
    params?: PageRequestDto,
  ): Promise<PageResponseDto<EvidenceResponseDto>> => {
    const response = await http.get<ApiResponseDto<PageResponseDto<EvidenceResponseDto>>>(
      `/api/v1/challenges/${challengeId}/evidences`,
      { params },
    );
    return response.data.data!;
  },

  getEvidenceById: async (id: number): Promise<EvidenceResponseDto> => {
    const response = await http.get<ApiResponseDto<EvidenceResponseDto>>(`/api/v1/evidences/${id}`);
    return response.data.data!;
  },

  createEvidence: async (data: EvidenceCreateRequestDto): Promise<EvidenceResponseDto> => {
    const response = await http.post<ApiResponseDto<EvidenceResponseDto>>(
      "/api/v1/evidences",
      data,
    );
    return response.data.data!;
  },

  updateEvidence: async (
    id: number,
    data: { description?: string; metadata?: Record<string, any> },
  ): Promise<EvidenceResponseDto> => {
    const response = await http.put<ApiResponseDto<EvidenceResponseDto>>(
      `/api/v1/evidences/${id}`,
      data,
    );
    return response.data.data!;
  },

  deleteEvidence: async (id: number): Promise<void> => {
    await http.delete(`/api/v1/evidences/${id}`);
  },

  uploadEvidenceFile: async (file: File, evidenceId: number): Promise<string> => {
    const formData = new FormData();
    formData.append("file", file);
    const response = await http.post<ApiResponseDto<{ fileUrl: string }>>(
      `/api/v1/evidences/${evidenceId}/upload`,
      formData,
      {
        headers: { "Content-Type": "multipart/form-data" },
      },
    );
    return response.data.data!.fileUrl;
  },
};

// Validation API
export const validationApi = {
  getValidations: async (
    params?: PageRequestDto,
  ): Promise<PageResponseDto<ValidationResponseDto>> => {
    const response = await http.get<ApiResponseDto<PageResponseDto<ValidationResponseDto>>>(
      "/api/v1/validations",
      { params },
    );
    return response.data.data!;
  },

  getValidationById: async (id: number): Promise<ValidationResponseDto> => {
    const response = await http.get<ApiResponseDto<ValidationResponseDto>>(
      `/api/v1/validations/${id}`,
    );
    return response.data.data!;
  },

  createValidation: async (data: ValidationCreateRequestDto): Promise<ValidationResponseDto> => {
    const response = await http.post<ApiResponseDto<ValidationResponseDto>>(
      "/api/v1/validations",
      data,
    );
    return response.data.data!;
  },

  updateValidation: async (
    id: number,
    data: ValidationUpdateRequestDto,
  ): Promise<ValidationResponseDto> => {
    const response = await http.put<ApiResponseDto<ValidationResponseDto>>(
      `/api/v1/validations/${id}`,
      data,
    );
    return response.data.data!;
  },

  deleteValidation: async (id: number): Promise<void> => {
    await http.delete(`/api/v1/validations/${id}`);
  },

  assignValidation: async (
    validationId: number,
    validatorId: number,
  ): Promise<ValidationResponseDto> => {
    const response = await http.post<ApiResponseDto<ValidationResponseDto>>(
      `/api/v1/validations/${validationId}/assign`,
      { validatorId },
    );
    return response.data.data!;
  },
};

// Validator API
export const validatorApi = {
  getValidators: async (
    params?: PageRequestDto,
  ): Promise<PageResponseDto<ValidatorResponseDto>> => {
    const response = await http.get<ApiResponseDto<PageResponseDto<ValidatorResponseDto>>>(
      "/api/v1/validators",
      { params },
    );
    return response.data.data!;
  },

  getValidatorById: async (id: number): Promise<ValidatorResponseDto> => {
    const response = await http.get<ApiResponseDto<ValidatorResponseDto>>(
      `/api/v1/validators/${id}`,
    );
    return response.data.data!;
  },

  applyAsValidator: async (data: {
    specialty: string;
    certificationDocument?: string;
    experience?: string;
  }): Promise<ValidatorResponseDto> => {
    const response = await http.post<ApiResponseDto<ValidatorResponseDto>>(
      "/api/v1/validators/apply",
      data,
    );
    return response.data.data!;
  },
};

// Coach API
export const coachApi = {
  getCoaches: async (params?: PageRequestDto): Promise<PageResponseDto<CoachResponseDto>> => {
    const response = await http.get<ApiResponseDto<PageResponseDto<CoachResponseDto>>>(
      "/api/v1/coaches",
      { params },
    );
    return response.data.data!;
  },

  getCoachById: async (id: number): Promise<CoachResponseDto> => {
    const response = await http.get<ApiResponseDto<CoachResponseDto>>(`/api/v1/coaches/${id}`);
    return response.data.data!;
  },

  applyAsCoach: async (data: {
    bio?: string;
    specialties: string[];
    hourlyRate?: number;
    currency?: string;
  }): Promise<CoachResponseDto> => {
    const response = await http.post<ApiResponseDto<CoachResponseDto>>(
      "/api/v1/coaches/apply",
      data,
    );
    return response.data.data!;
  },
};

// Rating API
export const ratingApi = {
  createRating: async (data: RatingCreateRequestDto): Promise<RatingResponseDto> => {
    const response = await http.post<ApiResponseDto<RatingResponseDto>>("/api/v1/ratings", data);
    return response.data.data!;
  },

  getRatings: async (
    targetId: number,
    targetType: string,
    params?: PageRequestDto,
  ): Promise<PageResponseDto<RatingResponseDto>> => {
    const response = await http.get<ApiResponseDto<PageResponseDto<RatingResponseDto>>>(
      `/api/v1/ratings/${targetType}/${targetId}`,
      { params },
    );
    return response.data.data!;
  },
};

// Notification API
export const notificationApi = {
  getNotifications: async (
    params?: PageRequestDto,
  ): Promise<PageResponseDto<NotificationResponseDto>> => {
    const response = await http.get<ApiResponseDto<PageResponseDto<NotificationResponseDto>>>(
      "/api/v1/notifications",
      { params },
    );
    return response.data.data!;
  },

  markAsRead: async (id: number): Promise<void> => {
    await http.put(`/api/v1/notifications/${id}/read`);
  },

  markAllAsRead: async (): Promise<void> => {
    await http.put("/api/v1/notifications/read-all");
  },
};

// Invitation API
export const invitationApi = {
  getInvitations: async (
    params?: PageRequestDto,
  ): Promise<PageResponseDto<InvitationResponseDto>> => {
    const response = await http.get<ApiResponseDto<PageResponseDto<InvitationResponseDto>>>(
      "/api/v1/invitations",
      { params },
    );
    return response.data.data!;
  },

  createInvitation: async (data: InvitationCreateRequestDto): Promise<InvitationResponseDto> => {
    const response = await http.post<ApiResponseDto<InvitationResponseDto>>(
      "/api/v1/invitations",
      data,
    );
    return response.data.data!;
  },

  respondToInvitation: async (id: number, accept: boolean): Promise<void> => {
    await http.post(`/api/v1/invitations/${id}/respond`, { accept });
  },
};

// Report API
export const reportApi = {
  createReport: async (data: ReportCreateRequestDto): Promise<ReportResponseDto> => {
    const response = await http.post<ApiResponseDto<ReportResponseDto>>("/api/v1/reports", data);
    return response.data.data!;
  },

  getReports: async (params?: PageRequestDto): Promise<PageResponseDto<ReportResponseDto>> => {
    const response = await http.get<ApiResponseDto<PageResponseDto<ReportResponseDto>>>(
      "/api/v1/reports",
      { params },
    );
    return response.data.data!;
  },
};

// Statistics API
export const statsApi = {
  getPlatformStats: async (): Promise<PlatformStatsDto> => {
    const response = await http.get<ApiResponseDto<PlatformStatsDto>>("/api/v1/stats/platform");
    return response.data.data!;
  },
};

// Webhook API (backend may expose endpoints like /api/v1/webhooks)
export const webhookApi = {
  listWebhooks: async (params?: PageRequestDto & { status?: string; q?: string }) => {
    const response = await http.get<ApiResponseDto<PageResponseDto<any>>>('/api/v1/webhooks', { params });
    return response.data.data!;
  },

  getWebhookById: async (id: string) => {
    const response = await http.get<ApiResponseDto<any>>(`/api/v1/webhooks/${id}`);
    return response.data.data!;
  },
  markWebhookProcessed: async (id: string) => {
    const response = await http.post<ApiResponseDto<any>>(`/api/v1/webhooks/${id}/processed`);
    return response.data.data!;
  },
};

// Legacy support - maintaining backward compatibility with existing code
export async function tryListChallenges() {
  try {
    const result = await challengeApi.getChallenges({ page: 0, size: 100 });
    return result.content;
  } catch (e) {
    console.warn("tryListChallenges failed, falling back to mock", e);
    return mockStore.listChallenges();
  }
}

export async function tryGetChallenge(id: string) {
  try {
    const result = await challengeApi.getChallengeById(parseInt(id));
    return result;
  } catch (e) {
    console.warn("tryGetChallenge failed, falling back to mock", e);
    return mockStore.getChallenge(id);
  }
}

export async function tryUploadEvidence(challengeId: string, file: File) {
  try {
    // Try to use backend presigned flow
    const pres = await http.post(`/api/v1/challenges/${challengeId}/evidences/presign`, {
      filename: file.name,
    });
    if (pres?.data?.uploadUrl) {
      // Upload to the presigned URL
      await axios.put(pres.data.uploadUrl, file, {
        headers: { "Content-Type": file.type || "application/octet-stream" },
      });
      // Notify backend
      let type: string;
      if (file.type.startsWith("image/")) {
        type = "IMAGE";
      } else if (file.type.startsWith("video/")) {
        type = "VIDEO";
      } else if (file.type.startsWith("audio/")) {
        type = "AUDIO";
      } else {
        type = "DOCUMENT";
      }
      await http.post(`/api/v1/challenges/${challengeId}/evidences`, {
        filename: file.name,
        type,
      });
      return {
        id: "real_" + Date.now(),
        filename: file.name,
        uploadedAt: new Date().toISOString(),
      };
    }
  } catch (err) {
    console.warn("presigned upload flow failed, falling back to mock", err);
  }
  // Fallback: simulate latency and store locally
  await new Promise((r) => setTimeout(r, 600));
  // Determine evidence type
  let evidenceType: EvidenceType;
  if (file.type.startsWith("image/")) {
    evidenceType = EvidenceType.IMAGE;
  } else if (file.type.startsWith("video/")) {
    evidenceType = EvidenceType.VIDEO;
  } else if (file.type.startsWith("audio/")) {
    evidenceType = EvidenceType.AUDIO;
  } else {
    evidenceType = EvidenceType.DOCUMENT;
  }

  return mockStore.addEvidence(challengeId, {
    id: Date.now(),
    uuid: "mock_" + Date.now(),
    challengeId: parseInt(challengeId),
    userId: 1,
    username: "current_user",
    type: evidenceType,
    filename: file.name,
    originalFilename: file.name,
    fileUrl: URL.createObjectURL(file),
    status: EvidenceStatus.PENDING,
    submittedAt: new Date().toISOString(),
  });
}

export default http;
