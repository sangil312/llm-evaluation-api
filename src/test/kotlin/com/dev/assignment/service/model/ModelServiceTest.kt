package com.dev.assignment.service.model

import com.dev.assignment.IntegrationTestSupport
import com.dev.assignment.domain.model.Model
import com.dev.assignment.repository.model.ModelRepository
import com.dev.assignment.service.model.request.CreateModel
import com.dev.assignment.service.model.request.UpdateModel
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional

@Transactional
class ModelServiceTest(
    private val modelService: ModelService,
    private val modelRepository: ModelRepository
) : IntegrationTestSupport() {

    @Test
    fun createModel() {
        val createModel = CreateModel(
            name = "gpt-5",
            description = "test",
            apiUrl = "https://api.example.com/gpt"
        )

        val savedModel = modelService.createModel(createModel)

        val findModel = modelRepository.findById(savedModel.id).orElseThrow()

        assertThat(findModel.name).isEqualTo(createModel.name)
        assertThat(findModel.description).isEqualTo(createModel.description)
        assertThat(findModel.apiUrl).isEqualTo(createModel.apiUrl)
    }

    @Test
    fun createModelWithDuplicatedName() {
        modelRepository.save(
            Model(
                name = "gpt-5-mini",
                description = "old",
                apiUrl = "https://api.example.com/old"
            )
        )

        val createModel = CreateModel(
            name = "gpt-5-mini",
            description = "new",
            apiUrl = "https://api.example.com/new"
        )

        assertThatThrownBy { modelService.createModel(createModel) }
            .isInstanceOf(CoreException::class.java)
            .hasMessage(ErrorType.DUPLICATED_MODEL.message)
    }

    @Test
    fun findModel() {
        val savedModel = modelRepository.save(
            Model(
                name = "gpt-5",
                description = "test",
                apiUrl = "https://api.example.com/gpt"
            )
        )

        val findModel = modelService.findModel(savedModel.id)

        assertThat(findModel.id).isEqualTo(savedModel.id)
        assertThat(findModel.name).isEqualTo(savedModel.name)
        assertThat(findModel.description).isEqualTo(savedModel.description)
        assertThat(findModel.apiUrl).isEqualTo(savedModel.apiUrl)
    }

    @Test
    fun findModelWithNotFound() {
        assertThatThrownBy { modelService.findModel(9999L) }
            .isInstanceOf(CoreException::class.java)
            .hasMessage(ErrorType.NOT_FOUND_DATA.message)
    }

    @Test
    fun findModelsWithHasNext() {
        modelRepository.saveAll(
            listOf(
                Model(
                    name = "model-1",
                    description = "test1",
                    apiUrl = "https://api.example.com/1"
                ),
                Model(
                    name = "model-2",
                    description = "test2",
                    apiUrl = "https://api.example.com/2"
                ),
                Model(
                    name = "model-3",
                    description = "test3",
                    apiUrl = "https://api.example.com/3"
                )
            )
        )

        val pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "id"))

        val models = modelService.findModels(pageable)

        assertThat(models.content).hasSize(2)
        assertThat(models.hasNext).isTrue
        assertThat(models.content.map { it.name }).containsExactly("model-1", "model-2")
    }

    @Test
    fun findModelsWithoutHasNext() {
        modelRepository.saveAll(
            listOf(
                Model(
                    name = "model-1",
                    description = "test1",
                    apiUrl = "https://api.example.com/1"
                ),
                Model(
                    name = "model-2",
                    description = "test2",
                    apiUrl = "https://api.example.com/2"
                )
            )
        )

        val pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "id"))

        val models = modelService.findModels(pageable)

        assertThat(models.content).hasSize(2)
        assertThat(models.hasNext).isFalse
        assertThat(models.content.map { it.name }).containsExactly("model-1", "model-2")
    }

    @Test
    fun updateModel() {
        val savedModel = modelRepository.save(
            Model(
                name = "gpt-4",
                description = "old",
                apiUrl = "https://api.example.com/old"
            )
        )

        val updateModel = UpdateModel(
            modelId = savedModel.id,
            name = "gpt-5",
            description = "new",
            apiUrl = "https://api.example.com/new"
        )

        modelService.updateModel(updateModel)

        val updatedModel = modelRepository.findById(savedModel.id).orElseThrow()

        assertThat(updatedModel.name).isEqualTo(updateModel.name)
        assertThat(updatedModel.description).isEqualTo(updateModel.description)
        assertThat(updatedModel.apiUrl).isEqualTo(updateModel.apiUrl)
    }

    @Test
    fun updateModelWithNotFound() {
        val updateModel = UpdateModel(
            modelId = 9999L,
            name = "gpt-5",
            description = "test",
            apiUrl = "https://api.example.com/gpt"
        )

        assertThatThrownBy { modelService.updateModel(updateModel) }
            .isInstanceOf(CoreException::class.java)
            .hasMessage(ErrorType.NOT_FOUND_DATA.message)
    }

    @Test
    fun deleteModel() {
        val savedModel = modelRepository.save(
            Model(
                name = "gpt-5",
                description = "test",
                apiUrl = "https://api.example.com/gpt"
            )
        )

        modelService.deleteModel(savedModel.id)

        assertThat(modelRepository.existsById(savedModel.id)).isFalse
    }
}
