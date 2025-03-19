package com.example.foldtracker.feature.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.foldtracker.feature.widget.FoldCountWidget.Companion.WIDGET_REFRESH_ACTION
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FoldCountWidgetReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == WIDGET_REFRESH_ACTION) {
            // Handle immediate refresh of widget without delay
            // Use GlobalScope for simplicity in the BroadcastReceiver context
            GlobalScope.launch {
                FoldCountWidget.refreshWidgets(context)
            }
            
            // Also notify any callback listeners that might need to know about the refresh
            WidgetRefreshCallback.notifyRefresh()
        }
    }
}