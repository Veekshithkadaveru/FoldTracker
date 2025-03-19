package com.example.foldtracker.feature.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Callback interface for widget refresh events
 */
interface WidgetRefreshCallback {
    fun onWidgetRefresh()
    
    companion object {
        private val listeners = mutableListOf<WidgetRefreshCallback>()
        
        fun register(callback: WidgetRefreshCallback) {
            listeners.add(callback)
        }
        
        fun unregister(callback: WidgetRefreshCallback) {
            listeners.remove(callback)
        }
        
        fun notifyRefresh() {
            listeners.forEach { it.onWidgetRefresh() }
        }
    }
}

/**
 * Action class to handle widget refresh from Glance widgets
 */
class WidgetRefreshAction : ActionCallback {
    
    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("WidgetRefresh", "Refresh button clicked. Updating widget.")
        // Use GlobalScope to avoid any context issues
        GlobalScope.launch {
            FoldCountWidget.refreshWidgets(context)
        }
    }
}