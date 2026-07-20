package com.hogiabao7725.hotelbooking.config;

import com.cloudinary.Cloudinary;
import com.hogiabao7725.hotelbooking.config.properties.CloudinaryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

    private final CloudinaryProperties props;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, Object> config = new HashMap<>();

        config.put("cloud_name", props.cloudName());
        config.put("api_key", props.apiKey());
        config.put("api_secret", props.apiSecret());
        config.put("secure", true);

        return new Cloudinary(config);
    }
}
