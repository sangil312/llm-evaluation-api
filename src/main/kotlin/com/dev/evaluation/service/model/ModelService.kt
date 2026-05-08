package com.dev.evaluation.service.model

import com.dev.evaluation.domain.model.Model
import com.dev.evaluation.service.model.request.CreateModel
import com.dev.evaluation.service.model.request.UpdateModel
import com.dev.evaluation.support.response.Page
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
