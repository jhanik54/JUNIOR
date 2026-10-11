package ai.affiora.junior.di

import android.content.Context
import ai.affiora.junior.data.db.AppDatabase
import ai.affiora.junior.data.db.ChatMessageDao
import ai.affiora.junior.data.db.ConversationDao
import ai.affiora.junior.data.db.PairedDeviceDao
import ai.affiora.junior.data.db.ToolExecutionDao
import ai.affiora.junior.data.prefs.UserPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "junior.db"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideChatMessageDao(db: AppDatabase): ChatMessageDao {
        return db.chatMessageDao()
    }

    @Provides
    fun provideConversationDao(db: AppDatabase): ConversationDao {
        return db.conversationDao()
    }

    @Provides
    fun provideToolExecutionDao(db: AppDatabase): ToolExecutionDao {
        return db.toolExecutionDao()
    }

    @Provides
    fun providePairedDeviceDao(db: AppDatabase): PairedDeviceDao {
        return db.pairedDeviceDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}
