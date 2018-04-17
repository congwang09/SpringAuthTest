package com.plumstep.config;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

@Component
//@Scope("request")
public class SslConfig {
	/*
    @Context 
    private MessageContext context;
    
    protected X509Certificate[] getActorCerts() {
    	
    		HttpServletRequest req = context.getHttpServletRequest();
    		HttpServletResponse res = context.getHttpServletResponse();
        	Object certChain = req.getAttribute("javax.servlet.request.X509Certificate");
    		if (certChain != null) {
    			for (X509Certificate x : (X509Certificate[])certChain) {
    				System.out.println(x);
    			}
    			
    			return (X509Certificate[])certChain;
    		}
    		else {
    			return null;
    		}
    	}
    
	@PostConstruct
	public void sslContextConfiguration() {
        try {
        		SSLContext sslContext = this.createSSLContext();
        		SSLContext.setDefault(sslContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@PostConstruct
	private SSLContext createSSLContext() {
		try {
			
			KeyStore keyStore = KeyStore.getInstance("JKS");
			//FileInputStream fis = new FileInputStream("/Users/congwang/Desktop/CAMP/keys/JKS/cometserver.jks");
			
            //KeyStore keyStore = KeyStore.getInstance("PKCS12");
			FileInputStream fis = new FileInputStream("/Users/congwang/Dropbox/2Renci-work/Projects/CAMP/UNDER_DEVELOPMENT/Clean-COMET/spring/mtls-springboot/client/src/main/resources/client-nonprod.jks");
            //FileInputStream fis = new FileInputStream("/Users/congwang/Desktop/CAMP/keys/p12s/cometclient.p12");
			keyStore.load(fis, "changeme".toCharArray());
			//keyStore.load(fis, "".toCharArray());
			fis.close();

			PKIXParameters params = new PKIXParameters(keyStore);
			Set<X509Certificate> caCertificates = params.getTrustAnchors().parallelStream()
					.map(TrustAnchor::getTrustedCert).collect(Collectors.toSet());
			for (X509Certificate x : caCertificates) {
				System.out.println("\n\n\n\n");
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
				System.out.println(x);
			}
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("\n\n\n\n");

			// Create key manager
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			//keyManagerFactory.init(keyStore, "".toCharArray());
			keyManagerFactory.init(keyStore, "changeme".toCharArray());
			KeyManager[] km = keyManagerFactory.getKeyManagers();

			// Create trust manager
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			// TrustManager[] tm = trustManagerFactory.getTrustManagers();

			System.out.println("Before Trust manager");
			TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					// X509Certificate[] certs = new X509Certificate[0];
					// return certs;
					return new X509Certificate[0];
					// return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
					for (X509Certificate x : certs) {
						System.out.println("~~~~~~~~~~~CERTS:::~~~~~~~~~~~~~~");
						System.out.println(x);
					}
					System.out.println("~~~~~~~~~~~CERTS:::DONE~~~~~~~~~~~~~~");
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
					for (X509Certificate x : certs) {
						System.out.println("~~~~~~~~~~~CERTS:::~~~~~~~~~~~~~~");
						System.out.println(x);
					}
					System.out.println("~~~~~~~~~~~CERTS:::DONE~~~~~~~~~~~~~~");

				}
			};

			// TrustManager xtm = tm.get()[0];
			// for (X509Certificate cert : xtm.getAcceptedIssuers()) {
			// String certStr = "S:" + cert.getSubjectDN().getName() + "\nI:"
			// + cert.getIssuerDN().getName();
			// System.out.println(certStr);
			// }

			// Initialize SSLContext
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(km, new TrustManager[] { tm }, null);
			// sslContext.init(km, tm, null);

			return sslContext;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	*/
}