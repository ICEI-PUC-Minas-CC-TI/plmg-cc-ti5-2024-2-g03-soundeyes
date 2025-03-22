package com.jerson.soundeyes.domain.use_case

import com.jerson.soundeyes.domain.repository.YoloRepository

class CloseYoloUseCase(private val yoloRepository: YoloRepository) {
    operator fun invoke(){
        yoloRepository.closeClassifier()
    }
}