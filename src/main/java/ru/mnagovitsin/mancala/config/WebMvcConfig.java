package ru.mnagovitsin.mancala.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.mnagovitsin.mancala.security.PlayerIdFilter;
import ru.mnagovitsin.mancala.security.PlayerIdHolder;
import ru.mnagovitsin.mancala.service.PlayerService;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // TODO: properly configure CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedHeaders("*")
            .allowedMethods("*")
            .allowedOriginPatterns("*")
            .allowCredentials(true);
    }

    @Bean
    public FilterRegistrationBean<PlayerIdFilter> playerIdFilter(PlayerService playerService,
                                                                 PlayerIdHolder playerIdHolder) {

        var filterRegistration = new FilterRegistrationBean<>(
            new PlayerIdFilter(playerService, playerIdHolder)
        );

        filterRegistration.setUrlPatterns(List.of(
            "/participations/*",
            "/games/*"
        ));

        return filterRegistration;
    }
}
