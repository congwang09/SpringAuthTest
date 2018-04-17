package com.plumstep.controller;

import io.swagger.annotations.ApiOperation;

import java.security.cert.X509Certificate;

import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.plumstep.config.SslConfig;
import certutils.*;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.login.CredentialException;

@RestController
@RequestMapping("server")
public class ServerController {
	@Autowired
	private SslConfig sslConfig;
	private static boolean isCertValid = true;
	final static String KEYSTOREPATH = "/Users/congwang/Dropbox/2Renci-work/Projects/CAMP/UNDER_DEVELOPMENT/springboot-handshake-test/server/src/main/resources/cometserver.jks";
	final static String KEYSTOREPASS = "changeme";
	
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
    
	
    @ApiOperation(value = "Return text message to show off successful call")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    ResponseEntity<?> getMessage(HttpServletRequest request,
            HttpServletResponse response, String userPassword) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
    		System.out.println("https://localhost:8111/");
	    	X509Certificate[] certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
			//Object certChain = request.getAttribute("javax.servlet.request.X509Certificate");
			//X509Certificate[] certs = (X509Certificate[])certChain;
		for (X509Certificate x : certs) {
			System.out.println(x);
		}
		try {
	        for (int i = 0; i < certs.length; i++)
	            certs[i].checkValidity();
	        		System.out.println("cert validated");
	        } catch (Exception e) {
	            try {
					throw new CredentialException("Certificate invalid: " + e);
				} catch (CredentialException e1) {
					System.out.println("____________________________\n____________________________");
					System.out.println("Unable to validate certificate!");
					isCertValid = false;
					System.out.println("____________________________\n____________________________");
				}
	    }
			
			if (isCertValid) {
	    		try {
	    			if (certutils.CertificateUtil.isSelfSigned(certs[0])) {
	    				System.out.println("____________________________\n____________________________");
	    				System.out.println("Cert is self signed!");
	    				System.out.println("____________________________\n____________________________");
	    			} else {
	    				CertificateUtil.verifyCertChain((List<X509Certificate>) Arrays.asList(certs),
	                    KEYSTOREPATH, KEYSTOREPASS);
	    			}
	        } catch (CertPathValidatorException e) {
	            try {
	    				throw new CredentialException("Unable to validate trust root: " + e);
	    			} catch (CredentialException e1) {
	    				System.out.println("____________________________\n____________________________");
	    				System.out.println("Unable to validate signed CA!");
	    				System.out.println("____________________________\n____________________________");
	    			}
	        }
	    }
		
    		return ResponseEntity.ok("Server successfully called!");
    }
    
    @RequestMapping(value = "/authenticateUser", method = RequestMethod.GET)
    public ResponseEntity<String> authenticateUser(HttpServletRequest request,
            HttpServletResponse response, String userPassword) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
    		System.out.println("https://localhost:8111/authenticateUser");
    		X509Certificate[] certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
    		if (certs == null) {
    			System.out.print("Cert is NULL!!!");
    		}
    		//Object certChain = request.getAttribute("javax.servlet.request.X509Certificate");
    		//X509Certificate[] certs = (X509Certificate[])certChain;
    		for (X509Certificate x : certs) {
			System.out.println(x);
		}
    		try {
            for (int i = 0; i < certs.length; i++)
                certs[i].checkValidity();
            		System.out.println("cert validated");
            } catch (Exception e) {
                try {
					throw new CredentialException("Certificate invalid: " + e);
				} catch (CredentialException e1) {
					System.out.println("____________________________\n____________________________");
    				System.out.println("Unable to validate certificate!");
    				isCertValid = false;
    				System.out.println("____________________________\n____________________________");
				}
        }
    		
    		if (isCertValid) {
        		try {
        			if (certutils.CertificateUtil.isSelfSigned(certs[0])) {
        				System.out.println("____________________________\n____________________________");
	    				System.out.println("Cert is self signed!");
	    				System.out.println("____________________________\n____________________________");
        			} else {
        				CertificateUtil.verifyCertChain((List<X509Certificate>) Arrays.asList(certs),
                        KEYSTOREPATH, KEYSTOREPASS);
        			}
            } catch (CertPathValidatorException e) {
                try {
	    				throw new CredentialException("Unable to validate trust root: " + e);
	    			} catch (CredentialException e1) {
	    				System.out.println("____________________________\n____________________________");
	    				System.out.println("Unable to validate signed CA!");
	    				System.out.println("____________________________\n____________________________");
	    			}
            }
        }
    		
        //return "redirect:loginPage?error=true";
    		return ResponseEntity.ok("Client is authenticated!");
    }
}
