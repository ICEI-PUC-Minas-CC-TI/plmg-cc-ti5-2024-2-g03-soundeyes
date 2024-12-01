package com.jerson.soundeyes.feature_app.domain.use_case

import com.jerson.soundeyes.feature_app.domain.repository.YoloRepository

class CloseClassifierUseCase(private val yoloRepository: YoloRepository) {
    operator fun invoke(){
        return yoloRepository.closeClassifier()
    }
}