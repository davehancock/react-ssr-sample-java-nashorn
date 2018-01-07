package com.djh.postcode;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author David Hancock
 */
@EnableCaching
@Configuration
public class PostcodeConfiguration {

    @Bean
    public PostcodeService postcodeService(PostcodeAPIClient postcodeAPIClient) {
        return new PostcodeService(postcodeAPIClient);
    }

    @Bean
    public PostcodeAPIClient postcodeAPIClient(RestTemplate restTemplate) {
        return new PostcodeAPIClient(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    public Cache postcodeCache() {
        return new GuavaCache("postcodes", CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build());
    }

}
