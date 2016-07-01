package com.parse.DiabetsApplication;

import java.security.AccessController;
import java.security.Provider;


//JSSEProvider is used to link the modified versions of SSLContext,
// KeyManagerFactory for X509,TrustManagerFactory for X509 and the AndroidCAstore.
// Hence, you will not be able to remove if it was meant for Android.


public class JSSEProvider extends Provider
{

    public JSSEProvider() {
        super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
        AccessController.doPrivileged(new java.security.PrivilegedAction<Void>()
        {
            public Void run()
            {
                put("SSLContext.TLS", "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
                put("Alg.Alias.SSLContext.TLSv1", "TLS");
                put("KeyManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
                put("TrustManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
                return null;
            }
        });
    }
}