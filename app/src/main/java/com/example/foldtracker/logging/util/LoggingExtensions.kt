package com.example.foldtracker.logging.util

import com.example.foldtracker.logging.api.Logger
import com.example.foldtracker.logging.api.LoggerFactory

/**
 * Extension functions to simplify logging in the application.
 */

/**
 * Gets a logger for the class.
 */
inline fun <reified T : Any> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java.simpleName)
}

/**
 * Gets a logger with the specified tag.
 */
fun Any.loggerWithTag(tag: String): Logger {
    return LoggerFactory.getLogger(tag)
}

// Verbose log extensions
inline fun <reified T : Any> T.logv(message: String) {
    logger().v(message)
}

inline fun <reified T : Any> T.logv(message: String, throwable: Throwable) {
    logger().v(message, throwable)
}

inline fun <reified T : Any> T.logv(message: String, vararg args: Any) {
    logger().v(T::class.java.simpleName, message, *args)
}

// Debug log extensions
inline fun <reified T : Any> T.logd(message: String) {
    logger().d(message)
}

inline fun <reified T : Any> T.logd(message: String, throwable: Throwable) {
    logger().d(message, throwable)
}

inline fun <reified T : Any> T.logd(message: String, vararg args: Any) {
    logger().d(T::class.java.simpleName, message, *args)
}

// Info log extensions
inline fun <reified T : Any> T.logi(message: String) {
    logger().i(message)
}

inline fun <reified T : Any> T.logi(message: String, throwable: Throwable) {
    logger().i(message, throwable)
}

inline fun <reified T : Any> T.logi(message: String, vararg args: Any) {
    logger().i(T::class.java.simpleName, message, *args)
}

// Warning log extensions
inline fun <reified T : Any> T.logw(message: String) {
    logger().w(message)
}

inline fun <reified T : Any> T.logw(message: String, throwable: Throwable) {
    logger().w(message, throwable)
}

inline fun <reified T : Any> T.logw(message: String, vararg args: Any) {
    logger().w(T::class.java.simpleName, message, *args)
}

// Error log extensions
inline fun <reified T : Any> T.loge(message: String) {
    logger().e(message)
}

inline fun <reified T : Any> T.loge(message: String, throwable: Throwable) {
    logger().e(message, throwable)
}

inline fun <reified T : Any> T.loge(message: String, vararg args: Any) {
    logger().e(T::class.java.simpleName, message, *args)
}

// WTF log extensions
inline fun <reified T : Any> T.logwtf(message: String) {
    logger().wtf(message)
}

inline fun <reified T : Any> T.logwtf(message: String, throwable: Throwable) {
    logger().wtf(message, throwable)
}

inline fun <reified T : Any> T.logwtf(message: String, vararg args: Any) {
    logger().wtf(T::class.java.simpleName, message, *args)
} 