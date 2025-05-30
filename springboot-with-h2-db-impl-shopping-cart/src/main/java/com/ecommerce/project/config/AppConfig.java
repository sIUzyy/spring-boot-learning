package com.ecommerce.project.config;

/*
 * This file is a Spring Boot configuration class that sets up a tool called ModelMapper.
 * This tool helps you automatically copy data from one object to another — usually between your DTOs and entities.
*/

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // This tells Spring: “Hey, this class contains setup/config code. Look inside and run it when the app starts.”
public class AppConfig {

    /*
    * This tells Spring: “Whenever someone needs a ModelMapper object, use this method to create it.”
    * It registers ModelMapper as a Spring Bean, which means you can now @Autowired it anywhere in your app!
    * */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
