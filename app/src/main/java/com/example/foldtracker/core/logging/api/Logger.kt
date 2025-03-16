package com.example.foldtracker.core.logging.api

/**
 * Core logging interface that abstracts away the underlying logging implementation.
 * This allows for easy switching between different logging libraries.
 */
interface Logger {
    
    // Verbose logging methods
    fun v(message: String)
    fun v(message: String, throwable: Throwable)
    fun v(tag: String, message: String)
    fun v(tag: String, message: String, throwable: Throwable)
    fun v(tag: String, message: String, vararg args: Any)
    
    // Debug logging methods
    fun d(message: String)
    fun d(message: String, throwable: Throwable)
    fun d(tag: String, message: String)
    fun d(tag: String, message: String, throwable: Throwable)
    fun d(tag: String, message: String, vararg args: Any)
    
    // Info logging methods
    fun i(message: String)
    fun i(message: String, throwable: Throwable)
    fun i(tag: String, message: String)
    fun i(tag: String, message: String, throwable: Throwable)
    fun i(tag: String, message: String, vararg args: Any)
    
    // Warning logging methods
    fun w(message: String)
    fun w(message: String, throwable: Throwable)
    fun w(tag: String, message: String)
    fun w(tag: String, message: String, throwable: Throwable)
    fun w(tag: String, message: String, vararg args: Any)
    
    // Error logging methods
    fun e(message: String)
    fun e(message: String, throwable: Throwable)
    fun e(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable)
    fun e(tag: String, message: String, vararg args: Any)
    
    // What a Terrible Failure logging methods
    fun wtf(message: String)
    fun wtf(message: String, throwable: Throwable)
    fun wtf(tag: String, message: String)
    fun wtf(tag: String, message: String, throwable: Throwable)
    fun wtf(tag: String, message: String, vararg args: Any)
} 