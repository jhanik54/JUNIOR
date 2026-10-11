package ai.affiora.junior

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import ai.affiora.junior.agent.ChannelHealthWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class JuniorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        scheduleHealthCheck()
    }

    private fun scheduleHealthCheck() {
        val healthWork = PeriodicWorkRequestBuilder<ChannelHealthWorker>(
            15, TimeUnit.MINUTES, // WorkManager minimum interval
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            ChannelHealthWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            healthWork,
        )
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        val agentChannel = NotificationChannel(
            CHANNEL_AGENT_SERVICE,
            "JUNIOR Agent Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "JUNIOR agent background service"
        }

        val alertChannel = NotificationChannel(
            CHANNEL_AGENT_ALERTS,
            "JUNIOR Agent Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Important JUNIOR agent notifications and results"
        }

        manager.createNotificationChannels(listOf(agentChannel, alertChannel))
    }

    companion object {
        const val CHANNEL_AGENT_SERVICE = "agent_service"
        const val CHANNEL_AGENT_ALERTS = "agent_alerts"
    }
}
