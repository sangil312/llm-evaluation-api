package com.dev.assignment

import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@ActiveProfiles("local-test")
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
abstract class IntegrationTestSupport {
    @Autowired
    protected lateinit var entityManager: EntityManager

    protected fun flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }
}