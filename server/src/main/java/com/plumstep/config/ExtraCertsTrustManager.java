package com.plumstep.config;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class ExtraCertsTrustManager implements X509TrustManager {

    private final X509TrustManager defaultX509TrustManager;
    private final X509TrustManager extraX509TrustManager;

    public ExtraCertsTrustManager(Collection<Certificate> certificates) throws GeneralSecurityException {
        defaultX509TrustManager = createX509TrustManager(null);

        KeyStore extraKeyStore = createKeyStore(certificates);
        extraX509TrustManager = createX509TrustManager(extraKeyStore);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        CertificateException ex1 = null;
        if (defaultX509TrustManager != null) {
            try {
                defaultX509TrustManager.checkClientTrusted(chain, authType);
                // Success
                return;
            } catch (CertificateException ex) {
                ex1 = ex;
            }
        }

        CertificateException ex2 = null;
        if (extraX509TrustManager != null) {
            try {
                extraX509TrustManager.checkClientTrusted(chain, authType);
                // Success
                return;
            } catch (CertificateException ex) {
                ex2 = ex;
            }
        }

        if (ex1 != null) {
            throw ex1;
        }
        if (ex2 != null) {
            throw ex2;
        }

        throw new CertificateException("No trust managers");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        CertificateException ex1 = null;
        if (defaultX509TrustManager != null) {
            try {
                defaultX509TrustManager.checkServerTrusted(chain, authType);
                // Success
                return;
            } catch (CertificateException ex) {
                ex1 = ex;
            }
        }

        CertificateException ex2 = null;
        if (extraX509TrustManager != null) {
            try {
                extraX509TrustManager.checkServerTrusted(chain, authType);
                // Success
                return;
            } catch (CertificateException ex) {
                ex2 = ex;
            }
        }

        if (ex1 != null) {
            throw ex1;
        }
        if (ex2 != null) {
            throw ex2;
        }

        throw new CertificateException("No trust managers");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        Set<X509Certificate> acceptedIssuers = new HashSet<X509Certificate>();

        if (defaultX509TrustManager != null) {
            Collections.addAll(acceptedIssuers, defaultX509TrustManager.getAcceptedIssuers());
        }

        if (extraX509TrustManager != null) {
            Collections.addAll(acceptedIssuers, extraX509TrustManager.getAcceptedIssuers());
        }

        return acceptedIssuers.toArray(new X509Certificate[acceptedIssuers.size()]);
    }

    private KeyStore createKeyStore(Collection<Certificate> certificates) throws KeyStoreException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

        try {
            keystore.load(null, null);
        } catch (IOException ex) {
            // Should never happen
            throw new RuntimeException(ex);
        } catch (GeneralSecurityException ex) {
            // Should never happen
            throw new RuntimeException(ex);
        }

        for (Certificate certificate : certificates) {
            String alias = certificate.toString();
            keystore.setCertificateEntry(alias, certificate);
        }

        return keystore;
    }

    private X509TrustManager createX509TrustManager(KeyStore keystore) throws GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = //
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);

        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        if (trustManagers.length == 0) {
            return null;
        }

        if (trustManagers.length > 1) {
            throw new GeneralSecurityException(String.format( //
                    "Expected 1 TrustManager from TrustManagerFactory(%s), got %s", //
                    trustManagerFactory, trustManagers.length));
        }

        TrustManager trustManager = trustManagers[0];
        if (!(trustManager instanceof X509TrustManager)) {
            throw new GeneralSecurityException(String.format( //
                    "Expected %s from TrustManagerFactory(%s), got %s", //
                    X509TrustManager.class.getCanonicalName(), trustManagerFactory,
                    trustManager.getClass().getCanonicalName()));
        }

        return (X509TrustManager) trustManager;
    }

}
