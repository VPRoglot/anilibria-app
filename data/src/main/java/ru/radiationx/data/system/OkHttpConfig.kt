package ru.radiationx.data.system

import android.os.Build
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import timber.log.Timber
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager

object OkHttpConfig {

    class DummyHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String?, session: SSLSession?): Boolean {
            return true
        }
    }

    class DummyTrustManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return emptyArray()
        }
    }
}

fun OkHttpClient.Builder.appendConnectionSpecs(): OkHttpClient.Builder {
    val cipherSuites = mutableListOf<CipherSuite>()
    val suites = ConnectionSpec.MODERN_TLS.cipherSuites
    suites?.also { cipherSuites.addAll(it) }

    if (!cipherSuites.contains(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)) {
        cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
    }

    if (!cipherSuites.contains(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA)) {
        cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA)
    }

    val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .cipherSuites(*cipherSuites.toTypedArray())
        .build()
    connectionSpecs(listOf(spec, ConnectionSpec.CLEARTEXT))
    return this
}

fun OkHttpClient.Builder.appendSocketFactoryIfNeeded(): OkHttpClient.Builder {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        try {
            val trustManager = OkHttpConfig.DummyTrustManager()
            sslSocketFactory(TLSSocketFactory(), trustManager)
            hostnameVerifier(OkHttpConfig.DummyHostnameVerifier())
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }
    return this
}

fun OkHttpClient.Builder.appendTimeouts(): OkHttpClient.Builder {
    callTimeout(25, TimeUnit.SECONDS)
    connectTimeout(15, TimeUnit.SECONDS)
    readTimeout(15, TimeUnit.SECONDS)
    writeTimeout(15, TimeUnit.SECONDS)
    return this
}
