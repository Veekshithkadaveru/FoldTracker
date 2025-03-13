package com.example.foldtracker.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.foldtracker.MainActivity
import com.example.foldtracker.R
import com.example.foldtracker.datastore.DataStoreKeys
import com.example.foldtracker.di.dataStore
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class FoldCountWidget : GlanceAppWidget() {

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val preferences = context.dataStore.data.first()
        val today = LocalDate.now().toString()
        val dailyCount = preferences[DataStoreKeys.dailyCountKey(today)] ?: 0
        val totalCount = preferences[DataStoreKeys.COUNTER_KEY] ?: 0

        provideContent {
            Column(
                modifier = GlanceModifier
                    .wrapContentSize()
                    .background(ColorProvider(R.color.light_gray))
                    .clickable(actionStartActivity<MainActivity>())
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📊 Fold Stats",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(R.color.dark_gray)
                        ),
                        modifier = GlanceModifier.padding(end = 8.dp)
                    )

                    Image(
                        provider = ImageProvider(R.drawable.ic_refresh),
                        contentDescription = "Refresh",
                        modifier = GlanceModifier
                            .size(24.dp)
                            .clickable(actionRunCallback<WidgetRefreshCallback>())
                    )
                }

                Spacer(modifier = GlanceModifier.height(16.dp))

                Box(
                    modifier = GlanceModifier
                        .wrapContentSize()
                        .background(ColorProvider(R.color.card_white))
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📅 Today's Folds",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(R.color.medium_gray)
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = "$dailyCount",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(R.color.blue)
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(16.dp))

                Box(
                    modifier = GlanceModifier
                        .wrapContentSize()
                        .background(ColorProvider(R.color.card_white))
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📈 Total Folds",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(R.color.medium_gray)
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = "$totalCount",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(R.color.purple)
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        suspend fun updateWidget(context: Context) {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(FoldCountWidget::class.java)

            glanceIds.forEach { glanceId ->
                FoldCountWidget().update(context, glanceId)
            }
        }
    }
}

