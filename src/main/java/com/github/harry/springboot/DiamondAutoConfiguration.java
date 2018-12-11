package com.github.harry.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiamondAutoConfiguration{

    @Bean
    public DiamondConfigurer initDiamond(){
        DiamondConfigurer diamondConfigurer = new DiamondConfigurer();
//        diamondConfigurer.setOrder(2);
//        diamondConfigurer.setIgnoreUnresolvablePlaceholders(true);
        return diamondConfigurer;

    }

}
