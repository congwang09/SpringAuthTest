package com.plumstep;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import javax.net.ssl.SSLContext;

@SpringBootApplication
@EnableSwagger2
public class ClientApplication {
    @Value("${server.ssl.trust-store-password}")
    private String trustStorePassword;
    @Value("${server.ssl.trust-store}")
    private Resource trustStore;
    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;
    @Value("${server.ssl.key-password}")
    private String keyPassword;
    @Value("${server.ssl.key-store}")
    private Resource keyStore;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
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
        
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

        restTemplate.setErrorHandler(
                new DefaultResponseErrorHandler() {
                    @Override
                    protected boolean hasError(HttpStatus statusCode) {
                        return false;
                    }
                });

        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() throws Exception {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    private HttpClient httpClient() throws Exception {
    	
        // Load our keystore and truststore containing certificates that we trust.
        SSLContext sslcontext = this.createSSLContext();
        //SSLContext sslcontext =
        //        SSLContexts.custom().loadTrustMaterial(trustStore.getFile(), trustStorePassword.toCharArray())
        //                .loadKeyMaterial(keyStore.getFile(), keyStorePassword.toCharArray(),
        //                        keyPassword.toCharArray()).build();
        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier());
        
        
        return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
    }

    private SSLContext createSSLContext(){
    		try{
    			//KeyStore keyStore = KeyStore.getInstance("JKS");
    			//FileInputStream fis = new FileInputStream("/Users/congwang/Dropbox/2Renci-work/Projects/CAMP/UNDER_DEVELOPMENT/springboot-handshake-test/client/src/main/resources/cometclient.jks");
    			//keyStore.load(fis, "changeme".toCharArray());  
            //KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //FileInputStream fis = new FileInputStream("/Users/congwang/Desktop/CAMP/keys/p12s/cometclient-wrong-CA.p12");
            //keyStore.load(fis, "".toCharArray());
            
            KeyStore keyStore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("/Users/congwang/Dropbox/2Renci-work/Projects/CAMP/UNDER_DEVELOPMENT/springboot-handshake-test/client/src/main/resources/cometclient-self.jks");
    			keyStore.load(fis, "changeme".toCharArray());            
            fis.close();
            
            // Create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            //keyManagerFactory.init(keyStore, "changeme".toCharArray());
            keyManagerFactory.init(keyStore, "changeme".toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();
             
            System.out.println("\n\n\n key manager created\n\n\n");
            
            // Create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            /*TrustManager tm = new X509ExtendedTrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    //return null;
                		return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {

                }

                public void checkClientTrusted(X509Certificate[] certs, String authType, javax.net.ssl.SSLEngine sslE) {
                	
                }
                
                public void checkClientTrusted(X509Certificate[] certs, String authType,  java.net.Socket sock) {
                	
                }
                
                public void checkServerTrusted(X509Certificate[] certs, String authType,  javax.net.ssl.SSLEngine sslE) {
                	
                }
                
                public void checkServerTrusted(X509Certificate[] certs, String authType,  java.net.Socket sock) {
                	
                }
                
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            };*/
            
            TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    //return null;
                		return new X509Certificate[0];
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
             
            System.out.println("\n\n\n trust manager created\n\n\n");
            
            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(km, new TrustManager[] { tm }, null);
            
            System.out.println("\n\n\n SSL context created\n\n\n");
            
            
            
            
            
            
            
             
            return sslContext;
        } catch (Exception ex){
            ex.printStackTrace();
        }
         
        return null;
    }
}
