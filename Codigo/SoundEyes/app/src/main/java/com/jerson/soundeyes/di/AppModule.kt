package com.jerson.soundeyes.di

import android.content.Context
import com.jerson.soundeyes.data.bluetooth.BluetoothDataSource
import com.jerson.soundeyes.data.ml.YOLOClassifier
import com.jerson.soundeyes.data.repository.BluetoothRepositoryImpl
import com.jerson.soundeyes.data.repository.YoloRepositoryImpl
import com.jerson.soundeyes.domain.repository.BluetoothRepository
import com.jerson.soundeyes.domain.repository.YoloRepository
import com.jerson.soundeyes.domain.use_case.BluetoothUseCase
import com.jerson.soundeyes.domain.use_case.ClassifyImageUseCase
import com.jerson.soundeyes.domain.use_case.CloseYoloUseCase
import com.jerson.soundeyes.domain.use_case.IsBluetoothEnableUseCase
import com.jerson.soundeyes.domain.use_case.ReceiveImageUseCase
import com.jerson.soundeyes.domain.use_case.SendConfigCameraUseCase
import com.jerson.soundeyes.domain.use_case.YoloUseCases
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
    fun providesYoloClassifier(@ApplicationContext context: Context): YOLOClassifier {
        YOLOClassifier.init(context)
        return YOLOClassifier
    }


    @Provides
    @Singleton
    fun providesYoloRepository(yoloClassifier: YOLOClassifier): YoloRepository {
        return YoloRepositoryImpl(yoloClassifier)
    }

    @Provides
    @Singleton
    fun providesYoloUseCases(yoloRepository: YoloRepository) = YoloUseCases(
        classifyImageUseCase = ClassifyImageUseCase(yoloRepository),
        closeYoloUseCase = CloseYoloUseCase(yoloRepository)
    )
    @Provides
    @Singleton
    fun providesBluetoothDataSource(@ApplicationContext context: Context): BluetoothDataSource {
        return BluetoothDataSource(context)
    }

    @Provides
    @Singleton
    fun providesBluetoothRepository(bluetoothDataSource: BluetoothDataSource):BluetoothRepository{
        return BluetoothRepositoryImpl(bluetoothDataSource)
    }

    @Provides
    @Singleton
    fun providesBluetoothUseCases(bluetoothRepository: BluetoothRepository)= BluetoothUseCase(
        receiveImageUseCase = ReceiveImageUseCase(bluetoothRepository),
        sendConfigCameraUseCase = SendConfigCameraUseCase(bluetoothRepository),
        isBluetoothEnable = IsBluetoothEnableUseCase(bluetoothRepository)
    )

}