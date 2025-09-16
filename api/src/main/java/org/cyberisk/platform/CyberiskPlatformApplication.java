package org.cyberisk.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * COP - CyberRisk Open Platform
 * 
 * An open-source geospatial security intelligence platform for cyberrisk management,
 * providing real-time monitoring of IT assets and security incidents
 * with advanced geospatial analytics capabilities.
 * 
 * @author Ossama Lafhel
 * @since 1.0.0
 */
@SpringBootApplication
public class CyberiskPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CyberiskPlatformApplication.class, args);
    }
}