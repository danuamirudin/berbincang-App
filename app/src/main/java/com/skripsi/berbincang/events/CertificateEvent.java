

package com.skripsi.berbincang.events;

import android.webkit.SslErrorHandler;
import androidx.annotation.Nullable;
import com.skripsi.berbincang.utils.ssl.MagicTrustManager;

import java.security.cert.X509Certificate;

public class CertificateEvent {
    private final X509Certificate x509Certificate;
    private final MagicTrustManager magicTrustManager;
    @Nullable
    private final SslErrorHandler sslErrorHandler;

    public CertificateEvent(X509Certificate x509Certificate, MagicTrustManager magicTrustManager,
                            @Nullable SslErrorHandler sslErrorHandler) {
        this.x509Certificate = x509Certificate;
        this.magicTrustManager = magicTrustManager;
        this.sslErrorHandler = sslErrorHandler;
    }

    @Nullable
    public SslErrorHandler getSslErrorHandler() {
        return sslErrorHandler;
    }

    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }

    public MagicTrustManager getMagicTrustManager() {
        return magicTrustManager;
    }
}
