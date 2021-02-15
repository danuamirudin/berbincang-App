

package com.skripsi.berbincang.activities

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import autodagger.AutoInjector
import butterknife.BindView
import butterknife.ButterKnife
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.skripsi.berbincang.R
import com.skripsi.berbincang.application.NextcloudTalkApplication
import com.skripsi.berbincang.controllers.CallController
import com.skripsi.berbincang.controllers.CallNotificationController
import com.skripsi.berbincang.events.ConfigurationChangeEvent
import com.skripsi.berbincang.utils.bundle.BundleKeys

@AutoInjector(NextcloudTalkApplication::class)
class MagicCallActivity : BaseActivity() {

    @BindView(R.id.controller_container)
    lateinit var container: ViewGroup

    private var router: Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NextcloudTalkApplication.sharedApplication!!.componentApplication.inject(this)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.decorView.systemUiVisibility = systemUiVisibility

        setContentView(R.layout.activity_magic_call)

        ButterKnife.bind(this)
        router = Conductor.attachRouter(this, container, savedInstanceState)
        router!!.setPopsLastView(false)

        if (!router!!.hasRootController()) {
            if (intent.getBooleanExtra(BundleKeys.KEY_FROM_NOTIFICATION_START_CALL, false)) {
                router!!.setRoot(RouterTransaction.with(CallNotificationController(intent.extras))
                        .pushChangeHandler(HorizontalChangeHandler())
                        .popChangeHandler(HorizontalChangeHandler()))
            } else {
                router!!.setRoot(RouterTransaction.with(CallController(intent.extras))
                        .pushChangeHandler(HorizontalChangeHandler())
                        .popChangeHandler(HorizontalChangeHandler()))
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        eventBus.post(ConfigurationChangeEvent())
    }

    companion object {
        private val TAG = "MagicCallActivity"

        private val systemUiVisibility: Int
            get() {
                var flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
                flags = flags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                return flags
            }
    }
}
