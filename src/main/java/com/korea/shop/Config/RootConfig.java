package com.korea.shop.Config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static javax.security.auth.login.Configuration.getConfiguration;

@Configuration
public class RootConfig {
    @Bean
    public ModelMapper modelMapper() {
        
        // 도구만들기
        ModelMapper modelMapper = new ModelMapper();

        // 옵션 셋팅
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.LOOSE);

        // 내보내기
        return modelMapper;
    }
}
