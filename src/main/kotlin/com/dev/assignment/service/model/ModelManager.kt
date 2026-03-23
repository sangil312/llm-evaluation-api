package com.dev.assignment.service.model

import com.dev.assignment.domain.model.Model
import com.dev.assignment.repository.model.ModelRepository
import com.dev.assignment.service.model.request.CreateModel
import com.dev.assignment.service.model.request.UpdateModel
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import com.dev.assignment.support.response.Page
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