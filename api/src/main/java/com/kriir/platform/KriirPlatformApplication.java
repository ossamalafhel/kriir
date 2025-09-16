package com.kriir.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * KRIIR - CyberRisk Open Platform
 * 
 * An open-source geospatial security intelligence platform for ransomware prediction and prevention,
 * providing real-time monitoring of IT assets and security incidents
 * with advanced geospatial analytics capabilities.
 * 
 * @author Ossama Lafhel
 * @since 1.0.0
 */
@SpringBootApplication
public class KriirPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(KriirPlatformApplication.class, args);
    }
}