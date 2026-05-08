package com.dev.evaluation.repository.dataset

import com.dev.evaluation.domain.dataset.Dataset
import org.springframework.data.jpa.repository.JpaRepository

interface DatasetRepository : JpaRepository<Dataset, Long>
