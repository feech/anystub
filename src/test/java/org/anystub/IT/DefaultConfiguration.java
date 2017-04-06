package org.anystub.IT;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Kirill on 9/4/2016.
 */
@Configuration
public class DefaultConfiguration {

    @Bean
    public String externalSystemUrl() {
        return "http://localhost:8080";
    }
}
