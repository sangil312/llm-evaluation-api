package com.dev.assignment.service.model

import com.dev.assignment.domain.model.Model
import com.dev.assignment.service.model.request.CreateModel
import com.dev.assignment.service.model.request.UpdateModel
import com.dev.assignment.support.response.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ModelService(
    private val modelManager: ModelManager
) {
    fun createModel(createModel: CreateModel): Model {
        return modelManager.createModel(createModel)
    }

    fun findModel(modelId: Long): Model {
        return modelManager.findModel(modelId)
    }

    fun findModels(pageable: Pageable): Page<Model> {
        return modelManager.findModels(pageable)
    }

    fun updateModel(updateModel: UpdateModel) {
        modelManager.updateModel(updateModel)
    }

    fun deleteModel(modelId: Long) {
        modelManager.deleteModel(modelId)
    }
}
