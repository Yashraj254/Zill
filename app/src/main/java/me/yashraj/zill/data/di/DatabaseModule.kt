package me.yashraj.zill.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.yashraj.zill.data.MediaSource
import me.yashraj.zill.data.repository.TrackRepositoryImpl
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
}