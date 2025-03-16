package com.example.foldtracker.feature.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class WidgetRefreshCallback : ActionCallback {
    
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("WidgetRefresh", "Refresh button clicked. Updating widget.")
        FoldCountWidget.updateWidget(context)
    }
}