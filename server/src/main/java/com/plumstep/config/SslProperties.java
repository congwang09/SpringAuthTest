package com.plumstep.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.ssl")
public class SslProperties {

    /**
     * Location of an X.509 certificate file. Can use classpath: prefix to use
     * certificate file from resources.
     */
    private String trustedCertificateLocation;

    public String getTrustedCertificateLocation() {
    		System.out.println("~~~~~~~~~~~~~~~~~");
    		System.out.println("trustedCertificateLocation:" + trustedCertificateLocation);
    		System.out.println("~~~~~~~~~~~~~~~~~");
        return trustedCertificateLocation;
    }

    public void setTrustedCertificateLocation(String trustedCertificateLocation) {
        this.trustedCertificateLocation = trustedCertificateLocation;
    }
}
