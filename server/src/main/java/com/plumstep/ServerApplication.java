package com.plumstep;

import org.apache.http.client.HttpClient;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.cxf.jaxrs.ext.MessageContext;

@SpringBootApplication
@EnableSwagger2
public class ServerApplication {
    @Value("${server.ssl.trust-store-password}")
    private String trustStorePassword;
    @Value("${server.ssl.trust-store}")
    private Resource trustResource;
    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;
    @Value("${server.ssl.key-password}")
    private String keyPassword;
    @Value("${server.ssl.key-store}")
    private Resource keyStore;
    
    @Context 
    private MessageContext context;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public RestTemplate restTemplate() throws Exception {
    	
/*    		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                        .loadTrustMaterial(null, acceptingTrustStrategy)
                        .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                        .setSSLSocketFactory(csf)
                        .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                        new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;*/
    	
    	
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        };
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    	
        
    		//TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
        //    @Override
        //    public boolean isTrusted(X509Certificate[] x509Certificates, String s) {
        //        return true;
        //    }
        //};
        //SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        //SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        //CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        //HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        //requestFactory.setHttpClient(httpClient);
        /*RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setErrorHandler(
                new DefaultResponseErrorHandler() {
                    @Override
                    protected boolean hasError(HttpStatus statusCode) {
                        return false;
                    }
                });
        return restTemplate;*/
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() throws Exception {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    private HttpClient httpClient() throws Exception {
        // Load our keystore and truststore containing certificates that we trust.
        SSLContext sslcontext = this.createSSLContext();
        //SSLContext sslcontext =
        //        SSLContexts.custom().loadTrustMaterial(trustResource.getFile(), trustStorePassword.toCharArray())
        //                .loadKeyMaterial(keyStore.getFile(), keyStorePassword.toCharArray(),
        //                        keyPassword.toCharArray()).build();
        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier());
        return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
    }

    
    
    /**
     * Retrieve certificate chain if available (null if not)
     * @return
     */
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
    
    private SSLContext createSSLContext() {
		try {
			/*X509Certificate[] testChain = getActorCerts();
			for (X509Certificate x : testChain) {
				System.out.println("\n\n\n\n");
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
				System.out.println(x);
			}
			*/
			KeyStore keyStore = KeyStore.getInstance("JKS");
			FileInputStream fis = new FileInputStream("/Users/congwang/Desktop/CAMP/keys/JKS/cometserver.jks");
			
            //KeyStore keyStore = KeyStore.getInstance("PKCS12");
			//FileInputStream fis = new FileInputStream("/Users/congwang/Dropbox/2Renci-work/Projects/CAMP/UNDER_DEVELOPMENT/Clean-COMET/spring/mtls-springboot/client/src/main/resources/client-nonprod.jks");
            //FileInputStream fis = new FileInputStream("/Users/congwang/Desktop/CAMP/keys/p12s/cometclient.p12");
			keyStore.load(fis, "changeme".toCharArray());
			//keyStore.load(fis, "".toCharArray());
			fis.close();

			PKIXParameters params = new PKIXParameters(keyStore);
			Set<X509Certificate> caCertificates = params.getTrustAnchors().parallelStream()
					.map(TrustAnchor::getTrustedCert).collect(Collectors.toSet());
			

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
						System.out.println("~~~~~~~~~~~CERTS1:::~~~~~~~~~~~~~~");
						System.out.println(x);
					}
					System.out.println("~~~~~~~~~~~CERTS1:::DONE~~~~~~~~~~~~~~");
	            }

	            public void checkServerTrusted(X509Certificate[] certs, String authType) {
	            		for (X509Certificate x : certs) {
						System.out.println("~~~~~~~~~~~CERTS1:::~~~~~~~~~~~~~~");
						System.out.println(x);
					}
					System.out.println("~~~~~~~~~~~CERTS1:::DONE~~~~~~~~~~~~~~");
	            }
			};

			System.out.println("After Trust manager");

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
}
