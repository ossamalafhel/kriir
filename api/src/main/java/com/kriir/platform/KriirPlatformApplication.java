package com.kriir.platform;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

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
@QuarkusMain
public class KriirPlatformApplication implements QuarkusApplication {

    public static void main(String[] args) {
        Quarkus.run(KriirPlatformApplication.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        Quarkus.waitForExit();
        return 0;
    }
}