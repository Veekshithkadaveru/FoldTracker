package com.example.foldtracker.feature.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoldCountWidgetReceiver : GlanceAppWidgetReceiver() {
    
    override val glanceAppWidget = FoldCountWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        CoroutineScope(Dispatchers.Main).launch {
            FoldCountWidget.updateWidget(context)
        }
    }
}