package com.example.foldtracker.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoldCountWidgetReceiver : GlanceAppWidgetReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override val glanceAppWidget = FoldCountWidget()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        CoroutineScope(Dispatchers.Main).launch {
            FoldCountWidget.updateWidget(context)
        }
    }
}