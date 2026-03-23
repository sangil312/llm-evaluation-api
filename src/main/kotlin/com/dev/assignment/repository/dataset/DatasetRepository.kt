package com.dev.assignment.repository.dataset

import com.dev.assignment.domain.dataset.Dataset
import org.springframework.data.jpa.repository.JpaRepository

interface DatasetRepository : JpaRepository<Dataset, Long>
