package me.yashraj.zill.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.yashraj.zill.data.MediaSource
import me.yashraj.zill.data.local.ZillDatabase
import me.yashraj.zill.data.local.dao.PlaylistDao
import me.yashraj.zill.data.repository.PlaylistRepositoryImpl
import me.yashraj.zill.data.repository.TrackRepositoryImpl
import me.yashraj.zill.domain.repository.PlaylistRepository
import me.yashraj.zill.domain.repository.TrackRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTrackRepository(mediaSource: MediaSource): TrackRepository {
        return TrackRepositoryImpl(mediaSource)
    }

    @Provides
    @Singleton
    fun providePlaylistDatabase(@ApplicationContext context: Context): ZillDatabase {
        return Room.databaseBuilder(context, ZillDatabase::class.java, "zill_database.db")
            .build()
    }

    @Provides
    fun providePlaylistDao(database: ZillDatabase): PlaylistDao = database.playlistDao()

    @Provides
    @Singleton
    fun providePlaylistRepository(
        playlistDao: PlaylistDao,
        trackRepository: TrackRepository
    ): PlaylistRepository = PlaylistRepositoryImpl(playlistDao, trackRepository)
}
