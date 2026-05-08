package com.dev.evaluation.service.model

import com.dev.evaluation.domain.model.Model
import com.dev.evaluation.repository.model.ModelRepository
import com.dev.evaluation.service.model.request.CreateModel
import com.dev.evaluation.service.model.request.UpdateModel
import com.dev.evaluation.support.error.CoreException
import com.dev.evaluation.support.error.ErrorType
import com.dev.evaluation.support.response.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ModelManager(
    private val modelRepository: ModelRepository
) {
    @Transactional
    fun createModel(createModel: CreateModel): Model {
        if (modelRepository.existsByName(createModel.name)) throw CoreException(ErrorType.DUPLICATED_MODEL)

        return modelRepository.save(
            Model(
                name = createModel.name,
                description = createModel.description,
                apiUrl = createModel.apiUrl
            )
        )
    }

    fun findModel(modelId: Long): Model {
        return modelRepository.findById(modelId)
            .orElseThrow { CoreException(ErrorType.NOT_FOUND_DATA) }
    }

    fun findModels(pageable: Pageable): Page<Model> {
        val models = modelRepository.findAllBy(pageable)

        return Page(models.content, models.hasNext())
    }

    fun validateModel(modelId: Long) {
        if (!modelRepository.existsById(modelId)) throw CoreException(ErrorType.INVALID_MODEL)
    }

    @Transactional
    fun updateModel(updateModel: UpdateModel) {
        val model = modelRepository.findById(updateModel.modelId)
            .orElseThrow { CoreException(ErrorType.NOT_FOUND_DATA) }

        model.update(
            name = updateModel.name,
            description = updateModel.description,
            apiUrl = updateModel.apiUrl
        )
    }

    @Transactional
    fun deleteModel(modelId: Long) {
        modelRepository.deleteById(modelId)
    }
}