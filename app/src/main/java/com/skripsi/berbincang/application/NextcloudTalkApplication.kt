
package com.skripsi.berbincang.application

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import autodagger.AutoComponent
import autodagger.AutoInjector
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.skripsi.berbincang.BuildConfig
import com.skripsi.berbincang.components.filebrowser.webdav.DavUtils
import com.skripsi.berbincang.dagger.modules.BusModule
import com.skripsi.berbincang.dagger.modules.ContextModule
import com.skripsi.berbincang.dagger.modules.DatabaseModule
import com.skripsi.berbincang.dagger.modules.RestModule
import com.skripsi.berbincang.jobs.AccountRemovalWorker
import com.skripsi.berbincang.jobs.CapabilitiesWorker
import com.skripsi.berbincang.jobs.PushRegistrationWorker
import com.skripsi.berbincang.jobs.SignalingSettingsWorker
import com.skripsi.berbincang.utils.ClosedInterfaceImpl
import com.skripsi.berbincang.utils.DeviceUtils
import com.skripsi.berbincang.utils.DisplayUtils
import com.skripsi.berbincang.utils.OkHttpNetworkFetcherWithCache
import com.skripsi.berbincang.utils.database.arbitrarystorage.ArbitraryStorageModule
import com.skripsi.berbincang.utils.database.user.UserModule
import com.skripsi.berbincang.utils.preferences.AppPreferences
import com.skripsi.berbincang.webrtc.MagicWebRTCUtils
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.googlecompat.GoogleCompatEmojiProvider

import de.cotech.hw.SecurityKeyManager
import de.cotech.hw.SecurityKeyManagerConfig
import okhttp3.OkHttpClient
import org.conscrypt.Conscrypt
import org.webrtc.PeerConnectionFactory
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioUtils

import javax.inject.Inject
import javax.inject.Singleton
import java.security.Security
import java.util.concurrent.TimeUnit

@AutoComponent(modules = [BusModule::class, ContextModule::class, DatabaseModule::class, RestModule::class, UserModule::class, ArbitraryStorageModule::class])
@Singleton
@AutoInjector(NextcloudTalkApplication::class)
class NextcloudTalkApplication : MultiDexApplication(), LifecycleObserver {
    //region Fields (components)
    lateinit var componentApplication: NextcloudTalkApplicationComponent
        private set
    //endregion

    //region Getters

    @Inject
    lateinit var appPreferences: AppPreferences
    @Inject
    lateinit var okHttpClient: OkHttpClient
    //endregion

    //region private methods
    private fun initializeWebRtc() {
        try {
            if (MagicWebRTCUtils.HARDWARE_AEC_BLACKLIST.contains(Build.MODEL)) {
                WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
            }

            if (!MagicWebRTCUtils.OPEN_SL_ES_WHITELIST.contains(Build.MODEL)) {
                WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true)
            }


            PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(this)
                    .setEnableVideoHwAcceleration(MagicWebRTCUtils.shouldEnableVideoHardwareAcceleration())
                    .createInitializationOptions())
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, e)
        }

    }

    //endregion

    //region Overridden methods
    override fun onCreate() {
        sharedApplication = this

        val securityKeyManager = SecurityKeyManager.getInstance()
        val securityKeyConfig = SecurityKeyManagerConfig.Builder()
                .setEnableDebugLogging(BuildConfig.DEBUG)
                .build()
        securityKeyManager.init(this, securityKeyConfig)

        initializeWebRtc()
        DisplayUtils.useCompatVectorIfNeeded()
        buildComponent()
        DavUtils.registerCustomFactories()

        componentApplication.inject(this)

        setAppTheme(appPreferences.theme)
        super.onCreate()

        val imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setNetworkFetcher(OkHttpNetworkFetcherWithCache(okHttpClient))
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(this)
                        .setMaxCacheSize(0)
                        .setMaxCacheSizeOnLowDiskSpace(0)
                        .setMaxCacheSizeOnVeryLowDiskSpace(0)
                        .build())
                .build()

        Fresco.initialize(this, imagePipelineConfig)
        Security.insertProviderAt(Conscrypt.newProvider(), 1)

        ClosedInterfaceImpl().providerInstallerInstallIfNeededAsync()
        DeviceUtils.ignoreSpecialBatteryFeatures()

        val pushRegistrationWork = OneTimeWorkRequest.Builder(PushRegistrationWorker::class.java).build()
        val accountRemovalWork = OneTimeWorkRequest.Builder(AccountRemovalWorker::class.java).build()
        val periodicCapabilitiesUpdateWork = PeriodicWorkRequest.Builder(CapabilitiesWorker::class.java,
                12, TimeUnit.HOURS).build()
        val capabilitiesUpdateWork = OneTimeWorkRequest.Builder(CapabilitiesWorker::class.java).build()
        val signalingSettingsWork = OneTimeWorkRequest.Builder(SignalingSettingsWorker::class.java).build()

        WorkManager.getInstance().enqueue(pushRegistrationWork)
        WorkManager.getInstance().enqueue(accountRemovalWork)
        WorkManager.getInstance().enqueue(capabilitiesUpdateWork)
        WorkManager.getInstance().enqueue(signalingSettingsWork)
        WorkManager.getInstance().enqueueUniquePeriodicWork("DailyCapabilitiesUpdateWork", ExistingPeriodicWorkPolicy.REPLACE, periodicCapabilitiesUpdateWork)

        val config = BundledEmojiCompatConfig(this)
        config.setReplaceAll(true)
        val emojiCompat = EmojiCompat.init(config)

        EmojiManager.install(GoogleCompatEmojiProvider(emojiCompat))
    }

    override fun onTerminate() {
        super.onTerminate()
        sharedApplication = null
    }
    //endregion


    //region Protected methods
    protected fun buildComponent() {
        componentApplication = DaggerNextcloudTalkApplicationComponent.builder()
                .busModule(BusModule())
                .contextModule(ContextModule(applicationContext))
                .databaseModule(DatabaseModule())
                .restModule(RestModule(applicationContext))
                .userModule(UserModule())
                .arbitraryStorageModule(ArbitraryStorageModule())
                .build()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        private val TAG = NextcloudTalkApplication::class.java.simpleName
        //region Singleton
        //endregion

        var sharedApplication: NextcloudTalkApplication? = null
            protected set
        //endregion

        //region Setters
        fun setAppTheme(theme: String) {
            when (theme) {
                "night_no" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "night_yes" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "battery_saver" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                else ->
                    // will be "follow_system" only for now
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
    //endregion
}
