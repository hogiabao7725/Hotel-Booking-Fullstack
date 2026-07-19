package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.dto.request.auth.RegisterRequest;
import com.hogiabao7725.hotelbooking.dto.response.auth.RegisterResponse;
import com.hogiabao7725.hotelbooking.entity.Account;
import com.hogiabao7725.hotelbooking.entity.Profile;
import com.hogiabao7725.hotelbooking.entity.Role;
import com.hogiabao7725.hotelbooking.enums.AccountStatus;
import com.hogiabao7725.hotelbooking.enums.UserRole;
import com.hogiabao7725.hotelbooking.exception.AppException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.mapper.AccountMapper;
import com.hogiabao7725.hotelbooking.mapper.ProfileMapper;
import com.hogiabao7725.hotelbooking.service.AccountService;
import com.hogiabao7725.hotelbooking.service.RoleService;
import com.hogiabao7725.hotelbooking.service.EmailService;
import com.hogiabao7725.hotelbooking.service.OneTimeTokenService;
import com.hogiabao7725.hotelbooking.service.RefreshTokenService;
import com.hogiabao7725.hotelbooking.service.TokenBlacklistService;
import com.hogiabao7725.hotelbooking.security.jwt.JwtTokenProvider;
import com.hogiabao7725.hotelbooking.dto.request.auth.RefreshTokenRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private OneTimeTokenService emailVerificationTokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private jakarta.servlet.http.HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthServiceImpl authService;

    // Helper Methods
    private RegisterRequest createRegisterRequest(String email, String fullName) {
        return new RegisterRequest(email, "password123", fullName);
    }

    private Profile createProfile(Long id, String fullName) {
        return Profile.builder()
                .id(id)
                .fullName(fullName)
                .build();
    }

    private Account createAccount(Long id, String email, AccountStatus status, Profile profile) {
        return Account.builder()
                .id(id)
                .email(email)
                .passwordHash("encodedPassword")
                .status(status)
                .profile(profile)
                .build();
    }

    // --- Test Cases ---
    @Test
    void register_shouldRegisterNewAccountSuccessfully_whenEmailDoesNotExist() {
        // Arrange
        RegisterRequest request = createRegisterRequest("newuser@example.com", "New User");
        Role customerRole = mock(Role.class);

        Account accountMock = Account.builder().email(request.email()).build();
        Profile profileMock = createProfile(null, request.fullName());
        Account savedAccount = createAccount(1L, request.email(), AccountStatus.INACTIVE, profileMock);
        savedAccount.setRole(customerRole);

        RegisterResponse expectedResponse = new RegisterResponse(1L, "newuser@example.com", "New User", "ROLE_CUSTOMER");

        when(accountService.findByEmail(request.email())).thenReturn(Optional.empty());
        when(roleService.getByName(UserRole.ROLE_CUSTOMER)).thenReturn(customerRole);
        when(accountMapper.toEntity(request)).thenReturn(accountMock);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(profileMapper.toEntity(request)).thenReturn(profileMock);
        when(accountService.save(any(Account.class))).thenReturn(savedAccount);

        when(emailVerificationTokenService.createToken("newuser@example.com")).thenReturn("token123");
        when(accountMapper.toResponse(savedAccount)).thenReturn(expectedResponse);

        // Act
        RegisterResponse result = authService.register(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.accountId()).isEqualTo(expectedResponse.accountId());
        assertThat(result.email()).isEqualTo(expectedResponse.email());
        assertThat(result.fullName()).isEqualTo(expectedResponse.fullName());
        assertThat(result.role()).isEqualTo(expectedResponse.role());

        verify(accountService).findByEmail(request.email());
        verify(roleService).getByName(UserRole.ROLE_CUSTOMER);
        verify(accountMapper).toEntity(request);
        verify(passwordEncoder).encode(request.password());
        verify(profileMapper).toEntity(request);
        verify(emailVerificationTokenService).createToken("newuser@example.com");
        verify(emailService).sendVerification("newuser@example.com", "New User", "token123");
        verify(accountMapper).toResponse(savedAccount);
    }

    @Test
    void register_shouldThrowConflictException_whenEmailAlreadyExistsAndActive() {
        // Arrange
        RegisterRequest request = createRegisterRequest("activeuser@example.com", "Active User");
        Account activeAccount = createAccount(1L, request.email(), AccountStatus.ACTIVE, null);

        when(accountService.findByEmail(request.email())).thenReturn(Optional.of(activeAccount));

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCOUNT_EMAIL_ALREADY_EXISTS);
        verify(accountService).findByEmail(request.email());
        verifyNoMoreInteractions(accountService, roleService, passwordEncoder, accountMapper, profileMapper, emailVerificationTokenService, emailService);
    }

    @Test
    void register_shouldUpdateExistingInactiveAccountSuccessfully_whenEmailExistsButInactive() {
        // Arrange
        RegisterRequest request = createRegisterRequest("inactiveuser@example.com", "Updated Name");
        Profile oldProfile = createProfile(5L, "Old Name");
        Account existingInactiveAccount = createAccount(1L, "inactiveuser@example.com", AccountStatus.INACTIVE, oldProfile);
        Account savedAccount = createAccount(1L, "inactiveuser@example.com", AccountStatus.INACTIVE, oldProfile);
        RegisterResponse expectedResponse = new RegisterResponse(1L, "inactiveuser@example.com", "Updated Name", "ROLE_CUSTOMER");

        when(accountService.findByEmail(request.email())).thenReturn(Optional.of(existingInactiveAccount));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedNewPassword");
        when(accountService.save(existingInactiveAccount)).thenReturn(savedAccount);

        when(emailVerificationTokenService.createToken("inactiveuser@example.com")).thenReturn("newToken123");
        when(accountMapper.toResponse(savedAccount)).thenReturn(expectedResponse);

        // Act
        RegisterResponse result = authService.register(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.accountId()).isEqualTo(expectedResponse.accountId());
        assertThat(result.email()).isEqualTo(expectedResponse.email());
        assertThat(result.fullName()).isEqualTo(expectedResponse.fullName());
        assertThat(result.role()).isEqualTo(expectedResponse.role());

        assertThat(existingInactiveAccount.getPasswordHash()).isEqualTo("encodedNewPassword");
        assertThat(existingInactiveAccount.getProfile().getFullName()).isEqualTo("Updated Name");

        verify(accountService).findByEmail(request.email());
        verify(passwordEncoder).encode(request.password());
        verify(emailVerificationTokenService).createToken("inactiveuser@example.com");
        verify(emailService).sendVerification("inactiveuser@example.com", "Updated Name", "newToken123");
        verify(accountMapper).toResponse(savedAccount);

        verifyNoInteractions(roleService, profileMapper);
    }

    @Test
    void logout_shouldRevokeRefreshTokenAndBlacklistAccessToken_whenAccessTokenIsValid() {
        // Arrange
        String refreshToken = "mock-refresh-token";
        String accessToken = "mock-access-token";
        String authHeader = "Bearer " + accessToken;
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        java.time.Duration remainingTtl = java.time.Duration.ofMinutes(10);

        when(httpServletRequest.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtTokenProvider.getRemainingTtl(accessToken)).thenReturn(remainingTtl);

        // Act
        authService.logout(request);

        // Assert
        verify(refreshTokenService).revoke(refreshToken);
        verify(tokenBlacklistService).add(accessToken, remainingTtl);
    }
}
