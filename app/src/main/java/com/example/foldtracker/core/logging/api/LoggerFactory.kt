package com.example.foldtracker.core.logging.api

import com.example.foldtracker.core.logging.config.LogConfiguration
import com.example.foldtracker.core.logging.impl.timber.TimberLogger

/**
 * Factory class for obtaining Logger instances.
 * Handles the creation and management of loggers with different implementations.
 */
object LoggerFactory {
    private var implementation: LoggerImplementation = LoggerImplementation.TIMBER
    private var configuration: LogConfiguration = LogConfiguration.default()
    private val loggers = mutableMapOf<String, Logger>()
    private var initialized = false
    
    /**
     * Available logger implementations
     * This can be expanded in the future when new implementations are added
     */
    enum class LoggerImplementation {
        TIMBER
    }
    
    /**
     * Initializes the logging system with default configuration
     */
    fun init() {
        init(configuration)
    }
    
    /**
     * Initializes the logging system with the specified configuration
     */
    fun init(config: LogConfiguration) {
        configuration = config
        
        when (implementation) {
            LoggerImplementation.TIMBER -> {
                TimberLogger.initTimber(config)
            }
        }
        
        initialized = true
        
        // Clear logger cache to force recreation with new configuration
        loggers.clear()
    }
    
    /**
     * Sets the active logger implementation
     * Currently only supports Timber, but design allows for future expansion
     */
    fun setImplementation(impl: LoggerImplementation) {
        implementation = impl
        
        if (initialized) {
            // Reinitialize with the new implementation
            init(configuration)
        }
        
        // Clear logger cache to force recreation with new implementation
        loggers.clear()
    }
    
    /**
     * Configures the logging system
     */
    fun configure(config: LogConfiguration) {
        if (initialized) {
            // Reinitialize with the new configuration
            init(config)
        } else {
            configuration = config
        }
    }
    
    /**
     * Returns the default logger
     */
    fun getLogger(): Logger {
        ensureInitialized()
        return getLogger(configuration.defaultTag)
    }
    
    /**
     * Returns a logger with the specified tag
     */
    fun getLogger(tag: String): Logger {
        ensureInitialized()
        return loggers.getOrPut(tag) {
            createLoggerInstance(tag)
        }
    }
    
    /**
     * Ensures the logging system is initialized before use
     */
    private fun ensureInitialized() {
        if (!initialized) {
            init()
        }
    }
    
    /**
     * Creates a new logger instance based on the current implementation
     */
    private fun createLoggerInstance(tag: String): Logger {
        return when (implementation) {
            LoggerImplementation.TIMBER -> TimberLogger(tag, configuration)
        }
    }
} 