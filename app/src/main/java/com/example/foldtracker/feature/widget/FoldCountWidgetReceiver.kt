package com.example.foldtracker.feature.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

private const val TAG = "FoldCountWidgetReceiver"

class FoldCountWidgetReceiver : GlanceAppWidgetReceiver() {
    // Use a dedicated scope with immediate dispatcher for faster responses
    private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    override val glanceAppWidget: GlanceAppWidget = FoldCountWidget()
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(TAG, "onUpdate called for ${appWidgetIds.size} widgets")
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        
        // Force immediate update in multiple ways
        
        // 1. Use our scope with immediate dispatcher
        widgetScope.launch(Dispatchers.Main.immediate) {
            Log.d(TAG, "Launching immediate coroutine update")
            FoldCountWidget.updateWidget(context)
        }
        
        // 2. Also use traditional update method with an executor for immediate execution
        Executors.newSingleThreadExecutor().execute {
            Log.d(TAG, "Executing traditional update on executor thread")
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, android.R.id.widget_frame)
        }
    }
    
    // Handle all intent actions to ensure widget updates
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: action=${intent.action}")
        super.onReceive(context, intent)
        
        // Force update on any broadcast received, especially:
        // - ACTION_APPWIDGET_UPDATE
        // - ACTION_APPWIDGET_OPTIONS_CHANGED
        // - Custom refresh actions
        widgetScope.launch(Dispatchers.Main.immediate) {
            Log.d(TAG, "Force updating widget from onReceive")
            FoldCountWidget.updateWidget(context)
            
            // Also force AppWidgetManager update
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, this@FoldCountWidgetReceiver::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            
            if (appWidgetIds.isNotEmpty()) {
                Log.d(TAG, "Also notifying widget data changed for ${appWidgetIds.size} widgets")
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, android.R.id.widget_frame)
            }
        }
    }
}