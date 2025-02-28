package com.example.foldtracker.widget

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class WidgetRefreshCallback : ActionCallback {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("WidgetRefresh", "Refresh button clicked. Updating widget.")
        FoldCountWidget.updateWidget(context)
    }
}