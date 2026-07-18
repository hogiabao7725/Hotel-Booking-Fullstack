package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.config.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private TokenBlacklistServiceImpl tokenBlacklistService;

    private static final String BLACKLIST_PREFIX = "jwt_blacklist:";

    @BeforeEach
    void setUp() {
        lenient().when(jwtProperties.accessBlackListPrefix()).thenReturn(BLACKLIST_PREFIX);
    }

    @Test
    void add_shouldSaveTokenInRedis_whenRemainingTtlIsPositive() {
        // Arrange
        String token = "valid-token";
        Duration ttl = Duration.ofMinutes(5);
        String expectedRedisKey = BLACKLIST_PREFIX + token;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        tokenBlacklistService.add(token, ttl);

        // Assert
        verify(valueOperations).set(expectedRedisKey, "1", ttl);
    }

    @Test
    void add_shouldNotSaveTokenInRedis_whenRemainingTtlIsZeroOrNegative() {
        // Arrange
        String token = "valid-token";

        // Act
        tokenBlacklistService.add(token, Duration.ZERO);
        tokenBlacklistService.add(token, Duration.ofMinutes(-5));

        // Assert
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void contains_shouldReturnTrue_whenTokenExistsInRedis() {
        // Arrange
        String token = "blacklisted-token";
        String redisKey = BLACKLIST_PREFIX + token;

        when(redisTemplate.hasKey(redisKey)).thenReturn(true);

        // Act
        boolean result = tokenBlacklistService.contains(token);

        // Assert
        assertThat(result).isTrue();
        verify(redisTemplate).hasKey(redisKey);
    }

    @Test
    void contains_shouldReturnFalse_whenTokenDoesNotExistInRedis() {
        // Arrange
        String token = "active-token";
        String redisKey = BLACKLIST_PREFIX + token;

        when(redisTemplate.hasKey(redisKey)).thenReturn(false);

        // Act
        boolean result = tokenBlacklistService.contains(token);

        // Assert
        assertThat(result).isFalse();
        verify(redisTemplate).hasKey(redisKey);
    }
}
