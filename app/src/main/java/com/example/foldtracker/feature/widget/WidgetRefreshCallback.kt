package com.example.foldtracker.feature.widget

import android.content.Context
import android.util.Log
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.foldtracker.core.di.dataStore
import com.example.foldtracker.repository.datastore.DataStoreKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate

class WidgetRefreshCallback : ActionCallback {
    private val TAG = "WidgetRefreshCallback"

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d(TAG, "Refresh button clicked - starting immediate update")
        
        // Force immediate update with both Glance and traditional methods
        try {
            // 1. Update using GlanceAppWidget method
            FoldCountWidget().update(context, glanceId)
            Log.d(TAG, "First update method completed")
            
            // 2. Also force AppWidgetManager to update
            withContext(Dispatchers.Main.immediate) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, FoldCountWidgetReceiver::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                
                // Force native widget update
                if (appWidgetIds.isNotEmpty()) {
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, android.R.id.widget_frame)
                    Log.d(TAG, "Native widget update requested for ${appWidgetIds.size} widgets")
                }
                
                // Also update all Glance widgets
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(FoldCountWidget::class.java)
                glanceIds.forEach { id ->
                    // Update widget state to force refresh
                    updateAppWidgetState(context, id) { prefs ->
                        prefs.toMutablePreferences().apply {
                            this[DataStoreKeys.WIDGET_LAST_REFRESH_KEY] = System.currentTimeMillis()
                        }
                    }
                    FoldCountWidget().update(context, id)
                }
                Log.d(TAG, "All Glance widgets updated")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget: ${e.message}")
        }
    }
}