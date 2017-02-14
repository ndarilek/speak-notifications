package info.thewordnerd.speaknotifications

import android.app.*
import android.content.*
import android.media.*
import android.os.*
import android.provider.*
import android.service.notification.*
import android.speech.tts.*
import android.util.Log

import org.jetbrains.anko.*

class LaunchActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(intentFor<SpeakNotificationsService>())
        if(Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners").contains(getApplicationContext().getPackageName()))
            finish()
        else
            alert("You'll need to grant access to notifications before they can be spoken. Do you wish to do so?") {
                yesButton { startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) }
                noButton { finish() }
            }.show()
    }
}

class SpeakNotificationsService: NotificationListenerService(), TextToSpeech.OnInitListener, AudioManager.OnAudioFocusChangeListener {

    private var tts: TextToSpeech? = null
    private var ttsInitialized = false

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
        Log.d("notificationtest", "Starting service")
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.ERROR)
            return
        tts?.setOnUtteranceProgressListener(MyUtteranceProgressListener(audioManager, this))
        ttsInitialized = true
    }

    override fun onDestroy() {
        if(tts != null && ttsInitialized)
            tts?.shutdown()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.getNotification()
        val extras = notification.extras
        Log.d("notificationtest", notification.extras.toString())
        if(extras.getCharSequence(Notification.EXTRA_TEMPLATE) == "android.app.Notification\$InboxStyle")
            return
        var text: String = extras.getCharSequence(Notification.EXTRA_TITLE) as String
        if(extras.getCharSequence(Notification.EXTRA_TEXT) != null)
            text += ": ${extras.getCharSequence(Notification.EXTRA_TEXT)}"
        Log.d("notificationtest", text.toString())
        if(ttsInitialized && text != "" && getCurrentInterruptionFilter() == NotificationListenerService.INTERRUPTION_FILTER_ALL && getCurrentListenerHints() == 0)
            tts?.speak(text, TextToSpeech.QUEUE_ADD, null, System.currentTimeMillis().toString())
    }

    override fun onAudioFocusChange(change: Int) {
    }

}

class MyUtteranceProgressListener(val audioManager: AudioManager, val focusListener: AudioManager.OnAudioFocusChangeListener): UtteranceProgressListener() {

    override fun onStart(utteranceId: String) {
        audioManager.requestAudioFocus(focusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
    }

    override fun onDone(utteranceId: String) {
        abandonFocus()
    }

    override fun onError(utteranceId: String) {
        abandonFocus()
    }

    override fun onError(utteranceId: String, errorCode: Int) {
        abandonFocus()
    }

    private fun abandonFocus() {
        audioManager.abandonAudioFocus(focusListener)
    }

}

class BootCompleted: BroadcastReceiver() {
    override fun onReceive(context: Context, Intent: Intent) {
        context.startService(Intent(context, SpeakNotificationsService::class.java))
    }
}
