package com.example.foldtracker.core.logging.impl.timber

import android.util.Log
import com.example.foldtracker.core.logging.api.Logger
import com.example.foldtracker.core.logging.config.LogConfiguration
import timber.log.Timber

/**
 * Logger implementation that uses Timber for logging.
 */
class TimberLogger(
    private val defaultTag: String,
    private val configuration: LogConfiguration
) : Logger {
    
    // Verbose logging methods
    override fun v(message: String) {
        if (shouldLog(LogConfiguration.LogLevel.VERBOSE)) {
            Timber.tag(defaultTag).v(message)
        }
    }
    
    override fun v(message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.VERBOSE)) {
            Timber.tag(defaultTag).v(throwable, message)
        }
    }
    
    override fun v(tag: String, message: String) {
        if (shouldLog(LogConfiguration.LogLevel.VERBOSE)) {
            Timber.tag(tag).v(message)
        }
    }
    
    override fun v(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.VERBOSE)) {
            Timber.tag(tag).v(throwable, message)
        }
    }
    
    override fun v(tag: String, message: String, vararg args: Any) {
        if (shouldLog(LogConfiguration.LogLevel.VERBOSE)) {
            Timber.tag(tag).v(message, *args)
        }
    }
    
    // Debug logging methods
    override fun d(message: String) {
        if (shouldLog(LogConfiguration.LogLevel.DEBUG)) {
            Timber.tag(defaultTag).d(message)
        }
    }
    
    override fun d(message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.DEBUG)) {
            Timber.tag(defaultTag).d(throwable, message)
        }
    }
    
    override fun d(tag: String, message: String) {
        if (shouldLog(LogConfiguration.LogLevel.DEBUG)) {
            Timber.tag(tag).d(message)
        }
    }
    
    override fun d(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.DEBUG)) {
            Timber.tag(tag).d(throwable, message)
        }
    }
    
    override fun d(tag: String, message: String, vararg args: Any) {
        if (shouldLog(LogConfiguration.LogLevel.DEBUG)) {
            Timber.tag(tag).d(message, *args)
        }
    }
    
    // Info logging methods
    override fun i(message: String) {
        if (shouldLog(LogConfiguration.LogLevel.INFO)) {
            Timber.tag(defaultTag).i(message)
        }
    }
    
    override fun i(message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.INFO)) {
            Timber.tag(defaultTag).i(throwable, message)
        }
    }
    
    override fun i(tag: String, message: String) {
        if (shouldLog(LogConfiguration.LogLevel.INFO)) {
            Timber.tag(tag).i(message)
        }
    }
    
    override fun i(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.INFO)) {
            Timber.tag(tag).i(throwable, message)
        }
    }
    
    override fun i(tag: String, message: String, vararg args: Any) {
        if (shouldLog(LogConfiguration.LogLevel.INFO)) {
            Timber.tag(tag).i(message, *args)
        }
    }
    
    // Warning logging methods
    override fun w(message: String) {
        if (shouldLog(LogConfiguration.LogLevel.WARNING)) {
            Timber.tag(defaultTag).w(message)
        }
    }
    
    override fun w(message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.WARNING)) {
            Timber.tag(defaultTag).w(throwable, message)
        }
    }
    
    override fun w(tag: String, message: String) {
        if (shouldLog(LogConfiguration.LogLevel.WARNING)) {
            Timber.tag(tag).w(message)
        }
    }
    
    override fun w(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.WARNING)) {
            Timber.tag(tag).w(throwable, message)
        }
    }
    
    override fun w(tag: String, message: String, vararg args: Any) {
        if (shouldLog(LogConfiguration.LogLevel.WARNING)) {
            Timber.tag(tag).w(message, *args)
        }
    }
    
    // Error logging methods
    override fun e(message: String) {
        if (shouldLog(LogConfiguration.LogLevel.ERROR)) {
            Timber.tag(defaultTag).e(message)
        }
    }
    
    override fun e(message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.ERROR)) {
            Timber.tag(defaultTag).e(throwable, message)
        }
    }
    
    override fun e(tag: String, message: String) {
        if (shouldLog(LogConfiguration.LogLevel.ERROR)) {
            Timber.tag(tag).e(message)
        }
    }
    
    override fun e(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.ERROR)) {
            Timber.tag(tag).e(throwable, message)
        }
    }
    
    override fun e(tag: String, message: String, vararg args: Any) {
        if (shouldLog(LogConfiguration.LogLevel.ERROR)) {
            Timber.tag(tag).e(message, *args)
        }
    }
    
    // WTF logging methods
    override fun wtf(message: String) {
        if (shouldLog(LogConfiguration.LogLevel.WTF)) {
            Timber.tag(defaultTag).wtf(message)
        }
    }
    
    override fun wtf(message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.WTF)) {
            Timber.tag(defaultTag).wtf(throwable, message)
        }
    }
    
    override fun wtf(tag: String, message: String) {
        if (shouldLog(LogConfiguration.LogLevel.WTF)) {
            Timber.tag(tag).wtf(message)
        }
    }
    
    override fun wtf(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(LogConfiguration.LogLevel.WTF)) {
            Timber.tag(tag).wtf(throwable, message)
        }
    }
    
    override fun wtf(tag: String, message: String, vararg args: Any) {
        if (shouldLog(LogConfiguration.LogLevel.WTF)) {
            Timber.tag(tag).wtf(message, *args)
        }
    }
    
    /**
     * Checks if the given log level should be logged based on the configuration.
     */
    private fun shouldLog(level: LogConfiguration.LogLevel): Boolean {
        return level.ordinal >= configuration.minLogLevel.ordinal
    }
    
    companion object {
        /**
         * Initializes Timber with appropriate trees based on the configuration.
         */
        fun initTimber(config: LogConfiguration) {
            // Clear existing trees
            Timber.uprootAll()
            
            // Plant appropriate trees based on configuration
            if (config.debugMode) {
                Timber.plant(Timber.DebugTree())
            } else {
                // In production, we use a custom release tree
                Timber.plant(ReleaseTree(config))
            }
        }
    }
    
    /**
     * A release tree that only logs warnings and above,
     * and can send reports to crash reporting services.
     */
    private class ReleaseTree(private val config: LogConfiguration) : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            // Only log warnings and above in release mode
            if (priority < Log.WARN) {
                return
            }
            
            // Here we could add integration with crash reporting services
            if (config.crashReportingEnabled && 
                (priority == Log.ERROR || priority == Log.ASSERT)) {
                // Send to crash reporting service
                // For example: FirebaseCrashlytics.getInstance().log(message)
                // If there's a throwable: FirebaseCrashlytics.getInstance().recordException(t)
            }
            
            when (priority) {
                Log.WARN -> Timber.w(t, message)
                Log.ERROR -> Timber.e(t, message)
                Log.ASSERT -> Timber.wtf(t, message)
            }
        }
    }
} 