package com.innowise.userservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for integration tests.
 * Uses H2 in-memory database and disables external dependencies like Redis and
 * Liquibase
 * to ensure tests can run without a Docker environment.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
}
