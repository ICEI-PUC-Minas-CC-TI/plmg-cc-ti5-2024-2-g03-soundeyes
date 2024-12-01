package com.jerson.soundeyes.di

import android.content.Context
import com.jerson.soundeyes.feature_app.data.api.YOLOClassifier
import com.jerson.soundeyes.feature_app.data.repository.YoloRepositoryImpl
import com.jerson.soundeyes.feature_app.domain.repository.YoloRepository
import com.jerson.soundeyes.feature_app.domain.use_case.YoloClassifierUseCase
import com.jerson.soundeyes.feature_app.domain.use_case.CloseClassifierUseCase
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
    fun providesYoloClassifier(@ApplicationContext context: Context): YOLOClassifier{
        YOLOClassifier.init(context)
        return YOLOClassifier

    }


    @Provides
    @Singleton
    fun providesYoloRepository(yoloClassifier: YOLOClassifier): YoloRepository{
        return YoloRepositoryImpl(yoloClassifier)
    }


    @Provides
    @Singleton
    fun providesYoloClassifierUseCase(yoloRepository: YoloRepository): YoloClassifierUseCase{
        return YoloClassifierUseCase(yoloRepository)
    }

    @Provides
    @Singleton
    fun providesCloseClassifyUseCase(yoloRepository: YoloRepository): CloseClassifierUseCase {
        return CloseClassifierUseCase(yoloRepository)
    }

}