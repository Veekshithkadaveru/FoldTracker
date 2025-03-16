package com.example.foldtracker.core.logging.config

/**
 * Configuration class for the logging system.
 * Uses a builder pattern for a fluent API.
 */
class LogConfiguration private constructor(
    val minLogLevel: LogLevel,
    val debugMode: Boolean,
    val crashReportingEnabled: Boolean,
    val defaultTag: String
) {
    
    /**
     * LogLevel enum representing different logging levels
     */
    enum class LogLevel {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, WTF, NONE
    }
    
    /**
     * Builder class for creating LogConfiguration instances
     */
    class Builder {
        private var minLogLevel: LogLevel = LogLevel.VERBOSE
        private var debugMode: Boolean = false
        private var crashReportingEnabled: Boolean = false
        private var defaultTag: String = "App"
        
        fun setMinLogLevel(level: LogLevel): Builder {
            minLogLevel = level
            return this
        }
        
        fun setDebugMode(debug: Boolean): Builder {
            debugMode = debug
            return this
        }
        
        fun enableCrashReporting(enable: Boolean): Builder {
            crashReportingEnabled = enable
            return this
        }
        
        fun setDefaultTag(tag: String): Builder {
            defaultTag = tag
            return this
        }
        
        fun build(): LogConfiguration {
            return LogConfiguration(
                minLogLevel = minLogLevel,
                debugMode = debugMode,
                crashReportingEnabled = crashReportingEnabled,
                defaultTag = defaultTag
            )
        }
    }
    
    companion object {
        /**
         * Returns a new Builder instance
         */
        fun builder(): Builder = Builder()
        
        /**
         * Returns a default configuration
         */
        fun default(): LogConfiguration = Builder().build()
    }
} 