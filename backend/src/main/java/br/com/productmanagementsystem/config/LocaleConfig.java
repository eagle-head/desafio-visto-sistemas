package br.com.productmanagementsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

@Configuration
public class LocaleConfig {

    private static final Locale EN_US = Locale.of("en", "US");
    private static final Locale PT_BR = Locale.of("pt", "BR");
    private static final Locale ES_ES = Locale.of("es", "ES");

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();

        resolver.setDefaultLocale(EN_US);
        resolver.setSupportedLocales(Arrays.asList(EN_US, PT_BR, ES_ES));
        
        return resolver;
    }
}