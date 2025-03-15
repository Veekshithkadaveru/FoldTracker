package com.example.foldtracker.di

import android.app.Application
import com.example.foldtracker.BuildConfig
import com.example.foldtracker.logging.api.LoggerFactory
import com.example.foldtracker.logging.config.LogConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoldTrackerApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize the logging framework
        initLogging()
    }
    
    private fun initLogging() {
        // Create a configuration for the logging framework
        val config = LogConfiguration.builder()
            .setDebugMode(BuildConfig.DEBUG) // Use debug mode only in debug builds
            .setMinLogLevel(
                if (BuildConfig.DEBUG) 
                    LogConfiguration.LogLevel.VERBOSE
                else 
                    LogConfiguration.LogLevel.WARNING
            )
            .setDefaultTag("FoldTracker")
            .enableCrashReporting(!BuildConfig.DEBUG)
            .build()
        
        // Initialize the logging framework with the configuration
        LoggerFactory.init(config)
    }
}