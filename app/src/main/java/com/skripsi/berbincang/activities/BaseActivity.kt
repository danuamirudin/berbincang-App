

package com.skripsi.berbincang.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.SslErrorHandler
import androidx.appcompat.app.AppCompatActivity
import autodagger.AutoInjector
import com.skripsi.berbincang.R
import com.skripsi.berbincang.application.NextcloudTalkApplication
import com.skripsi.berbincang.events.CertificateEvent
import com.skripsi.berbincang.utils.SecurityUtils
import com.skripsi.berbincang.utils.preferences.AppPreferences
import com.skripsi.berbincang.utils.ssl.MagicTrustManager
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.security.cert.CertificateParsingException
import java.security.cert.X509Certificate
import java.text.DateFormat
import javax.inject.Inject

@AutoInjector(NextcloudTalkApplication::class)
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        NextcloudTalkApplication.sharedApplication!!.componentApplication.inject(this)
        super.onCreate(savedInstanceState)
    }

    public override fun onResume() {
        super.onResume()
        if (appPreferences.isScreenSecured) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        if (appPreferences.isScreenLocked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                SecurityUtils.createKey(appPreferences.screenLockTimeout)
            }
        }
    }

    fun showCertificateDialog(cert: X509Certificate, magicTrustManager: MagicTrustManager,
                              sslErrorHandler: SslErrorHandler?) {
        val formatter = DateFormat.getDateInstance(DateFormat.LONG)
        val validFrom = formatter.format(cert.notBefore)
        val validUntil = formatter.format(cert.notAfter)

        val issuedBy = cert.issuerDN.toString()
        val issuedFor: String

        try {
            if (cert.subjectAlternativeNames != null) {
                val stringBuilder = StringBuilder()
                for (o in cert.subjectAlternativeNames) {
                    val list = o as List<*>
                    val type = list[0] as Int
                    if (type == 2) {
                        val name = list[1] as String
                        stringBuilder.append("[").append(type).append("]").append(name).append(" ")
                    }
                }
                issuedFor = stringBuilder.toString()
            } else {
                issuedFor = cert.subjectDN.name
            }

            @SuppressLint("StringFormatMatches") val dialogText = String.format(resources
                    .getString(R.string.nc_certificate_dialog_text),
                    issuedBy, issuedFor, validFrom, validUntil)

            LovelyStandardDialog(this)
                    .setTopColorRes(R.color.nc_darkRed)
                    .setNegativeButtonColorRes(R.color.nc_darkRed)
                    .setPositiveButtonColorRes(R.color.colorPrimaryDark)
                    .setIcon(R.drawable.ic_security_white_24dp)
                    .setTitle(R.string.nc_certificate_dialog_title)
                    .setMessage(dialogText)
                    .setPositiveButton(R.string.nc_yes) { v ->
                        magicTrustManager.addCertInTrustStore(cert)
                        sslErrorHandler?.proceed()
                    }
                    .setNegativeButton(R.string.nc_no) { view1 ->
                        sslErrorHandler?.cancel()
                    }
                    .show()

        } catch (e: CertificateParsingException) {
            Log.d(TAG, "Failed to parse the certificate")
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CertificateEvent) {
        showCertificateDialog(event.x509Certificate, event.magicTrustManager, event.sslErrorHandler)
    }

    public override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }

    public override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    companion object {
        private val TAG = "BaseActivity"
    }
}
