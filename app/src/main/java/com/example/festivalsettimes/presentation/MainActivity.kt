/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

@file:OptIn(ExperimentalFoundationApi::class)

package com.example.festivalsettimes.presentation

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.InputDeviceCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewConfigurationCompat
import androidx.lifecycle.ViewModel
import androidx.wear.compose.material.*
import com.example.festivalsettimes.presentation.theme.FestivalSetTimesTheme
import kotlinx.coroutines.*
import java.security.AccessController.getContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val focusRequester = remember { FocusRequester() }
            WearApp(object : AppViewModel() {
                override val focusRequester: FocusRequester
                    get() = focusRequester
            })
        }
    }
}

/**
 * ScrollableState integration for Horizontal Pager.
 */
public class PagerScrollHandler @OptIn(ExperimentalFoundationApi::class) constructor(
    private val numPages: Int,
    private val pagerState: PagerState,
    private val coroutineScope: CoroutineScope
) : ScrollableState {
    override val isScrollInProgress: Boolean
        get() = totalDelta != 0f

    override fun dispatchRawDelta(delta: Float): Float = scrollableState.dispatchRawDelta(delta)

    private var totalDelta = 0f

    private val scrollableState = ScrollableState { delta ->
        totalDelta += delta

        val offset = when {
            // tune to match device
            totalDelta > 40f -> {
                1
            }
            totalDelta < -40f -> {
                -1
            }
            else -> null
        }

        if (offset != null) {
            totalDelta = 0f
            val newTargetPage = pagerState.targetPage + offset
            if (newTargetPage in (0 until numPages)) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(newTargetPage, 0f)
                }
            }
        }

        delta
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        scrollableState.scroll(block = block)
    }
}

val dateOnlyFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
val timeOnlyFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

abstract class AppViewModel() : ViewModel() {

    abstract val focusRequester: FocusRequester
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WearApp(viewModel: AppViewModel) {
    val days = listOf(friday, saturday, sunday)
    val currentDate = remember { mutableStateOf(LocalDateTime.now()) }
    val currentTime = remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(key1 = Unit) {
        while (isActive) {
            currentTime.value = LocalTime.now()
            delay(1000)
        }
    }

    val day = days.firstOrNull { d ->
        val today = if (currentTime.value.hour > 6) {
            currentDate.value
        } else {
            currentDate.value.minusDays(1)
        }
        d.date.dayOfYear == today.dayOfYear
    }

    FestivalSetTimesTheme {
        if (day != null) {

            val horiState = rememberPagerState()
            val focusRequester = remember { FocusRequester() }
            val coroutineScope = rememberCoroutineScope()
            val pagerScrollHandler = remember {
                PagerScrollHandler(day.stages.size, horiState, coroutineScope)
            }
            var vertStatesMut = emptyList<PagerState>()
            day.stages.forEach {
                vertStatesMut = vertStatesMut.plus(rememberPagerState())
            }
            val vertPagerStates = vertStatesMut

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            HorizontalPager(
                pageCount = day.stages.size,
                state = horiState,
                modifier = Modifier
                    .fillMaxSize()
                    .onRotaryScrollEvent {
                        coroutineScope.launch {
                            pagerScrollHandler.scrollBy(it.verticalScrollPixels)
                        }
                        true
                    }
                    .focusRequester(focusRequester)
                    .focusable()
            ) { x ->
                val stage = day.stages[x]
                val stageColor = stageColor(stage.name)
                VerticalPager(
                    pageCount = stage.setTimes.size,
                    state = vertPagerStates[x],
                ) { y ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.background),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val set = stage.setTimes[y]

                        val startTime = LocalTime.parse(set.start, timeOnlyFormat)
                        val setStartDay = if (startTime.hour > 6) {
                            day.date
                        } else {
                            day.date.plusDays(1)
                        }
                        val start = LocalDateTime.parse(
                            "${setStartDay.format(dateOnlyFormat)} ${set.start}",
                            timeFormat
                        )

                        // Everything is over at 6am
                        val nextSetStart = stage
                            .setTimes
                            .map { s -> s.start }
                            .getOrElse(y + 1) { "06:00" }
                        val endTime = LocalTime.parse(nextSetStart, timeOnlyFormat)
                        val setEndDay = if (endTime.hour > 6) {
                            day.date
                        } else {
                            day.date.plusDays(1)
                        }
                        val end = LocalDateTime.parse(
                            "${setEndDay.format(dateOnlyFormat)} $nextSetStart",
                            timeFormat
                        )

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = stageColor,
                            text = set.artist
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = stageColor,
                            text = start.format(gridTimeFormat) + " - " + end.format(gridTimeFormat)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(25.dp)
                ) {
                    Column {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = stageColor,
                            text = stageName(stage.name)
                        )
                    }
                }
            }

            Box(contentAlignment = Alignment.BottomCenter) {
                Column {
                    Button(
                        modifier = Modifier
                            .size(width = 90.dp, height = 30.dp),
                        onClick = {
                            val page = day.stages[horiState.currentPage].setTimes
                                .indexOfLast {
                                    val now = LocalTime.now()
                                    val start = LocalTime.parse(it.start, timeOnlyFormat)
                                    now.isAfter(start)
                                }
                            coroutineScope.launch {
                                vertPagerStates[horiState.currentPage].scrollToPage(page)
                            }
                        }
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            text = currentTime.value.format(timeOnlyFormat),
                            fontSize = 20.sp,
                        )
                    }
                    Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                }
            }

            HorizontalPageIndicator(
                pageIndicatorState = object : PageIndicatorState {
                    override val pageCount: Int
                        get() = 9
                    override val pageOffset: Float
                        get() = horiState.currentPageOffsetFraction
                    override val selectedPage: Int
                        get() = horiState.currentPage

                })
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val today = LocalDate.now().dayOfYear
                val start = days[0].date.dayOfYear
                if (today < start) {
                    Text("EDC is ${start - today} days away!")
                } else {
                    Text("Hope you had a fun EDC!")
                }
            }
        }
    }

}

val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
val gridTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    val focusRequester = remember { FocusRequester() }
    WearApp(object : AppViewModel() {
        override val focusRequester: FocusRequester
            get() = focusRequester
    })
}