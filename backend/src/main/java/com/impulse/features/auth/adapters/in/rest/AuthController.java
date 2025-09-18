package com.impulse.features.auth.adapters.in.rest;

import com.impulse.features.auth.adapters.in.rest.dto.*;
import com.impulse.features.auth.application.dto.*;
import com.impulse.features.auth.application.port.in.*;
import com.impulse.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUserUseCase logoutUserUseCase;

    public AuthController(ForgotPasswordUseCase forgotPasswordUseCase,
                         ResetPasswordUseCase resetPasswordUseCase,
                         VerifyEmailUseCase verifyEmailUseCase,
                         RefreshTokenUseCase refreshTokenUseCase,
                         LogoutUserUseCase logoutUserUseCase) {
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.verifyEmailUseCase = verifyEmailUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUserUseCase = logoutUserUseCase;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth module is operational");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {

        ForgotPasswordCommand command = new ForgotPasswordCommand(
            request.getEmail(),
            getUserAgent(httpRequest),
            getClientIpAddress(httpRequest)
        );

        ForgotPasswordResponse response = forgotPasswordUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {

        ResetPasswordCommand command = new ResetPasswordCommand(
            request.getToken(),
            request.getNewPassword(),
            getUserAgent(httpRequest),
            getClientIpAddress(httpRequest)
        );

        ResetPasswordResponse response = resetPasswordUseCase.execute(command);

        if (response.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<VerifyEmailResponse>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request,
            HttpServletRequest httpRequest) {

        VerifyEmailCommand command = new VerifyEmailCommand(
            request.getToken(),
            getUserAgent(httpRequest),
            getClientIpAddress(httpRequest)
        );

        VerifyEmailResponse response = verifyEmailUseCase.execute(command);

        if (response.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {

        RefreshTokenCommand command = new RefreshTokenCommand(
            request.getRefreshToken(),
            getUserAgent(httpRequest),
            getClientIpAddress(httpRequest)
        );

        RefreshTokenResponse response = refreshTokenUseCase.execute(command);

        if (response.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutUserResponse>> logout(
            @Valid @RequestBody LogoutRequest request,
            HttpServletRequest httpRequest) {

        LogoutUserCommand command = new LogoutUserCommand(
            request.getRefreshToken(),
            getUserAgent(httpRequest),
            getClientIpAddress(httpRequest),
            request.isLogoutFromAllDevices()
        );

        LogoutUserResponse response = logoutUserUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Helper methods
    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
