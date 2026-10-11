package ai.affiora.junior.di

import android.content.Context
import ai.affiora.junior.connectors.ConnectorManager
import ai.affiora.junior.tools.AlarmTimerTool
import ai.affiora.junior.tools.AndroidTool
import ai.affiora.junior.tools.AppLauncherTool
import ai.affiora.junior.tools.BrightnessTool
import ai.affiora.junior.tools.CalendarTool
import ai.affiora.junior.tools.ChannelTool
import ai.affiora.junior.channels.ChannelManager
import ai.affiora.junior.tools.CallLogTool
import ai.affiora.junior.tools.SubAgentTool
import ai.affiora.junior.tools.SessionHistoryTool
import ai.affiora.junior.agent.ClaudeApiClient
import ai.affiora.junior.data.db.ChatMessageDao
import ai.affiora.junior.data.db.ConversationDao
import ai.affiora.junior.tools.MemoryTool
import ai.affiora.junior.tools.ClipboardTool
import ai.affiora.junior.tools.ContactsTool
import ai.affiora.junior.tools.FileSystemTool
import ai.affiora.junior.tools.FlashlightTool
import ai.affiora.junior.tools.HttpTool
import ai.affiora.junior.tools.NavigationTool
import ai.affiora.junior.tools.OpenAiTool
import ai.affiora.junior.tools.PhotoTool
import ai.affiora.junior.tools.ScheduleTool
import ai.affiora.junior.tools.MediaControlTool
import ai.affiora.junior.tools.NotificationCache
import ai.affiora.junior.tools.NotificationTool
import ai.affiora.junior.tools.PhoneCallTool
import ai.affiora.junior.tools.ScreenCaptureTool
import ai.affiora.junior.tools.SkillAuthorTool
import ai.affiora.junior.tools.SmsTool
import ai.affiora.junior.tools.SystemInfoTool
import ai.affiora.junior.tools.TelegramTool
import ai.affiora.junior.tools.UIAutomationTool
import ai.affiora.junior.tools.VolumeTool
import ai.affiora.junior.tools.WebBrowserTool
import ai.affiora.junior.tools.ClawNotificationListener
import ai.affiora.junior.agent.ScheduleEngine
import ai.affiora.junior.data.prefs.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ToolsModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(io.ktor.client.plugins.HttpTimeout) {
                // Values live in HttpTimeouts so ToolsModule and the user-facing
                // error messages in AgentRuntime.formatNetworkError stay in sync.
                requestTimeoutMillis = ai.affiora.junior.agent.HttpTimeouts.REQUEST_MS
                connectTimeoutMillis = ai.affiora.junior.agent.HttpTimeouts.CONNECT_MS
                socketTimeoutMillis = ai.affiora.junior.agent.HttpTimeouts.SOCKET_MS
            }
            // NOTE: WebSockets plugin intentionally NOT installed here.
            // Ktor's WebSocket pipeline interceptor can interfere with regular
            // HTTP long-polling (Telegram getUpdates). SlackChannel, the only
            // consumer of webSocket(), creates its own client with the plugin.
        }
    }

    @Provides
    @Singleton
    fun provideSmsTool(@ApplicationContext context: Context): SmsTool {
        return SmsTool(context)
    }

    @Provides
    @Singleton
    fun provideCallLogTool(@ApplicationContext context: Context): CallLogTool {
        return CallLogTool(context)
    }

    @Provides
    @Singleton
    fun provideContactsTool(@ApplicationContext context: Context): ContactsTool {
        return ContactsTool(context)
    }

    @Provides
    @Singleton
    fun provideCalendarTool(@ApplicationContext context: Context): CalendarTool {
        return CalendarTool(context)
    }

    @Provides
    @Singleton
    fun provideNotificationCache(): NotificationCache {
        return ClawNotificationListener.Companion
    }

    @Provides
    @Singleton
    fun provideNotificationTool(notificationCache: NotificationCache): NotificationTool {
        return NotificationTool(notificationCache)
    }

    @Provides
    @Singleton
    fun provideWebBrowserTool(
        @ApplicationContext context: Context,
        httpClient: HttpClient,
    ): WebBrowserTool {
        return WebBrowserTool(context, httpClient)
    }

    @Provides
    @Singleton
    fun provideSkillAuthorTool(
        @ApplicationContext context: Context,
        userPreferences: UserPreferences,
        skillInstaller: ai.affiora.junior.skills.SkillInstaller,
    ): SkillAuthorTool {
        return SkillAuthorTool(context, userPreferences, skillInstaller)
    }

    @Provides
    @Singleton
    fun provideAppLauncherTool(@ApplicationContext context: Context): AppLauncherTool {
        return AppLauncherTool(context)
    }

    @Provides
    @Singleton
    fun provideClipboardTool(@ApplicationContext context: Context): ClipboardTool {
        return ClipboardTool(context)
    }

    @Provides
    @Singleton
    fun provideAlarmTimerTool(@ApplicationContext context: Context): AlarmTimerTool {
        return AlarmTimerTool(context)
    }

    @Provides
    @Singleton
    fun provideFlashlightTool(@ApplicationContext context: Context): FlashlightTool {
        return FlashlightTool(context)
    }

    @Provides
    @Singleton
    fun provideVolumeTool(@ApplicationContext context: Context): VolumeTool {
        return VolumeTool(context)
    }

    @Provides
    @Singleton
    fun provideBrightnessTool(@ApplicationContext context: Context): BrightnessTool {
        return BrightnessTool(context)
    }

    @Provides
    @Singleton
    fun provideMediaControlTool(@ApplicationContext context: Context): MediaControlTool {
        return MediaControlTool(context)
    }

    @Provides
    @Singleton
    fun provideSystemInfoTool(@ApplicationContext context: Context): SystemInfoTool {
        return SystemInfoTool(context)
    }

    @Provides
    @Singleton
    fun provideUIAutomationTool(@ApplicationContext context: Context): UIAutomationTool {
        return UIAutomationTool(context)
    }

    @Provides
    @Singleton
    fun provideScreenCaptureTool(@ApplicationContext context: Context): ScreenCaptureTool {
        return ScreenCaptureTool(context)
    }

    @Provides
    @Singleton
    fun provideFileSystemTool(@ApplicationContext context: Context): FileSystemTool {
        return FileSystemTool(context)
    }

    @Provides
    @Singleton
    fun providePhoneCallTool(@ApplicationContext context: Context): PhoneCallTool {
        return PhoneCallTool(context)
    }

    @Provides
    @Singleton
    fun provideHttpTool(
        @ApplicationContext context: Context,
        httpClient: HttpClient,
        connectorManager: ConnectorManager,
    ): HttpTool {
        return HttpTool(context, httpClient, connectorManager)
    }

    @Provides
    @Singleton
    fun provideNavigationTool(@ApplicationContext context: Context): NavigationTool {
        return NavigationTool(context)
    }

    @Provides
    @Singleton
    fun provideScheduleTool(
        @ApplicationContext context: Context,
        scheduleEngine: ScheduleEngine,
    ): ScheduleTool {
        return ScheduleTool(context, scheduleEngine)
    }

    @Provides
    @Singleton
    fun provideOpenAiTool(
        @ApplicationContext context: Context,
        httpClient: HttpClient,
        userPreferences: UserPreferences,
    ): OpenAiTool {
        return OpenAiTool(context, httpClient, userPreferences)
    }

    @Provides
    @Singleton
    fun providePhotoTool(@ApplicationContext context: Context): PhotoTool {
        return PhotoTool(context)
    }

    @Provides
    @Singleton
    fun provideTelegramTool(
        httpClient: HttpClient,
        connectorManager: ConnectorManager,
    ): TelegramTool {
        return TelegramTool(httpClient, connectorManager)
    }

    @Provides
    @Singleton
    fun provideMemoryTool(memoryStore: ai.affiora.junior.agent.MemoryStore): MemoryTool {
        return MemoryTool(memoryStore)
    }

    @Provides
    @Singleton
    fun provideChannelTool(
        channelManager: dagger.Lazy<ChannelManager>,
        @ApplicationContext context: Context,
    ): ChannelTool {
        return ChannelTool(channelManager, context)
    }

    @Provides
    @Singleton
    fun provideSubAgentTool(
        claudeApiClient: ClaudeApiClient,
        userPreferences: UserPreferences,
    ): SubAgentTool {
        return SubAgentTool(claudeApiClient, userPreferences)
    }

    @Provides
    @Singleton
    fun provideSessionHistoryTool(
        chatMessageDao: ChatMessageDao,
        conversationDao: ConversationDao,
    ): SessionHistoryTool {
        return SessionHistoryTool(chatMessageDao, conversationDao)
    }

    @Provides
    @Singleton
    fun provideToolRegistry(
        smsTool: SmsTool,
        callLogTool: CallLogTool,
        contactsTool: ContactsTool,
        calendarTool: CalendarTool,
        notificationTool: NotificationTool,
        webBrowserTool: WebBrowserTool,
        skillAuthorTool: SkillAuthorTool,
        appLauncherTool: AppLauncherTool,
        clipboardTool: ClipboardTool,
        alarmTimerTool: AlarmTimerTool,
        flashlightTool: FlashlightTool,
        volumeTool: VolumeTool,
        brightnessTool: BrightnessTool,
        mediaControlTool: MediaControlTool,
        systemInfoTool: SystemInfoTool,
        uiAutomationTool: UIAutomationTool,
        screenCaptureTool: ScreenCaptureTool,
        fileSystemTool: FileSystemTool,
        phoneCallTool: PhoneCallTool,
        httpTool: HttpTool,
        navigationTool: NavigationTool,
        scheduleTool: ScheduleTool,
        openAiTool: OpenAiTool,
        photoTool: PhotoTool,
        telegramTool: TelegramTool,
        memoryTool: MemoryTool,
        channelTool: ChannelTool,
        subAgentTool: SubAgentTool,
        sessionHistoryTool: SessionHistoryTool,
    ): Map<String, AndroidTool> {
        val tools: List<AndroidTool> = listOf(
            smsTool,
            callLogTool,
            contactsTool,
            calendarTool,
            notificationTool,
            webBrowserTool,
            skillAuthorTool,
            appLauncherTool,
            clipboardTool,
            alarmTimerTool,
            flashlightTool,
            volumeTool,
            brightnessTool,
            mediaControlTool,
            systemInfoTool,
            uiAutomationTool,
            screenCaptureTool,
            fileSystemTool,
            phoneCallTool,
            httpTool,
            navigationTool,
            scheduleTool,
            openAiTool,
            photoTool,
            telegramTool,
            memoryTool,
            channelTool,
            subAgentTool,
            sessionHistoryTool,
        )
        return tools.associateBy { it.name }
    }
}
