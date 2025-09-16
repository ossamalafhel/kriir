package com.cyberisk.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@SpringBootApplication
@EnableR2dbcRepositories
@EnableTransactionManagement
public class CyberiskPlatformApplication {

	public static void main(String[] args) {
		log.info("Starting COP - CyberRisk Open Platform...");
		log.info("🛡️  Open-Source Security Intelligence Platform with Geospatial Analytics");
		log.info("🌍  Real-time IT Asset & Security Incident Monitoring");
		SpringApplication.run(CyberiskPlatformApplication.class, args);
		log.info("✅ COP Platform started successfully");
	}
}
