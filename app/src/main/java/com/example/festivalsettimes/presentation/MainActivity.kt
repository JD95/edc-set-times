/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
)

package com.example.festivalsettimes.presentation

import android.os.Bundle
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
import androidx.wear.compose.material.*
import com.example.festivalsettimes.R
import com.example.festivalsettimes.presentation.theme.FestivalSetTimesTheme
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val setTimes = this.applicationContext.getString(R.string.edc_set_times)
        setContent {
            WearApp(getFestivalDaysFromString(setTimes))
        }
    }
}

/**
 * ScrollableState integration for Horizontal Pager.
 */
@OptIn(ExperimentalFoundationApi::class)
class PagerScrollHandler @OptIn(ExperimentalFoundationApi::class) constructor(
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
val timeOnlyFormatParse: DateTimeFormatter = DateTimeFormatter.ofPattern("[H][HH]:mm")
val timeOnlyFormatDisplay: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WearApp(days: List<FestivalDay>) {
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

            val stageColors = stageColors(day.stages.size)
            val horiState = rememberPagerState()
            val focusRequester = remember { FocusRequester() }
            val coroutineScope = rememberCoroutineScope()
            val pagerScrollHandler = remember {
                PagerScrollHandler(day.stages.size, horiState, coroutineScope)
            }
            var vertStatesMut = emptyList<PagerState>()
            repeat(day.stages.size) {
                vertStatesMut = vertStatesMut.plus(rememberPagerState())
            }
            val vertPagerStates = vertStatesMut

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            Box(contentAlignment = Alignment.Center) {
                StagePager(
                    day,
                    horiState,
                    coroutineScope,
                    pagerScrollHandler,
                    focusRequester,
                    stageColors,
                    vertPagerStates
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column {
                        TimeButton(day, horiState, coroutineScope, vertPagerStates, currentTime)
                        Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                    }
                }

                HorizontalPageIndicator(
                    pageIndicatorState = object : PageIndicatorState {
                        override val pageCount: Int
                            get() = day.stages.size
                        override val pageOffset: Float
                            get() = horiState.currentPageOffsetFraction
                        override val selectedPage: Int
                            get() = horiState.currentPage

                    }
                )
            }
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

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun StagePager(
    day: FestivalDay,
    horiState: PagerState,
    coroutineScope: CoroutineScope,
    pagerScrollHandler: PagerScrollHandler,
    focusRequester: FocusRequester,
    stageColors: List<Color>,
    vertPagerStates: List<PagerState>
) {
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
        val stageColor = stageColors[x]
        SetTimePager(stage, vertPagerStates, x, day, stageColor)

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
                    text = stage.name
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun SetTimePager(
    stage: Stage,
    vertPagerStates: List<PagerState>,
    x: Int,
    day: FestivalDay,
    stageColor: Color
) {
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

            val startTime = set.start
            val setStartDay = if (startTime.hour > 6) {
                day.date
            } else {
                day.date.plusDays(1)
            }
            val start = LocalDateTime.parse(
                "${setStartDay.format(dateOnlyFormat)} ${set.start.format(timeOnlyFormatDisplay)}",
                timeFormat
            )

            // Everything is over at 6am
            val nextSetStart = stage
                .setTimes
                .map { s -> s.start }
                .getOrElse(y + 1) { LocalTime.parse("06:00", timeOnlyFormatParse) }
            val setEndDay = if (nextSetStart.hour > 6) {
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
                text = set.artist,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = stageColor,
                text = start.format(gridTimeFormat) + " - " + end.format(gridTimeFormat)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimeButton(
    day: FestivalDay,
    horiState: PagerState,
    coroutineScope: CoroutineScope,
    vertPagerStates: List<PagerState>,
    currentTime: MutableState<LocalTime>
) {
    Button(
        modifier = Modifier
            .size(width = 90.dp, height = 30.dp),
        onClick = {
            val page = day.stages[horiState.currentPage].setTimes
                .indexOfLast {
                    val now = LocalTime.now()
                    val start = it.start
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
            text = currentTime.value.format(timeOnlyFormatDisplay),
            fontSize = 20.sp,
        )
    }
}

val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
val gridTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(
        getFestivalDaysFromString(
            """
2024-05-17,Bionic Jungle,Baggi B2B Matt Denuzzo,17:00
2024-05-17,Cosmic Meadow,Friendly Fire,17:00
2024-05-17,Cosmic Meadow,Noizu B2B Westend B2B Mele,17:30
2024-05-17,Quantum Valley,Alchimyst,19:00
2024-05-17,Circuit Grounds,Brina Knauss,19:00
2024-05-17,Neon Garden,Heidi Lawden,19:00
2024-05-18,Kinetic Field,Matroda,22:00
2024-05-18,Wasteland,Atmozfears,22:30
2024-05-18,Quantum Valley,Bryan Kearney,22:30
2024-05-18,Bionic Jungle,DJ Heartstring,22:30
2024-05-18,Basspod,Wilkinson,22:30
2024-05-18,Stereo Bloom,Eli Brown,23:15
2024-05-18,Kinetic Field,DJ Snake,23:18
2024-05-18,Wasteland,Adrenalize B2B Wasted Penguinz,23:30
2024-05-18,Quantum Valley,Andrew Bayer,23:30
2024-05-18,Circuit Grounds,Special Guest,23:30
2024-05-18,Basspod,Wooli,23:30
2024-05-19,Neon Garden,Peggy Gou,0:00
2024-05-19,Cosmic Meadow,Deorro,0:15
2024-05-19,Quantum Valley,Ferry Corsten,0:30
2024-05-19,Basspod,Hedex,0:30
2024-05-19,Stereo Bloom,Kevin de Vries,0:30
2024-05-19,Bionic Jungle,Skin On Skin,0:30
2024-05-19,Bionic Jungle,Ranger Trucco,23:00
2024-05-19,Cosmic Meadow,Sofi Tukker (DJ Set),23:00
2024-05-19,Circuit Grounds,Seven Lions,23:02
2024-05-19,Kinetic Field,Alesso,23:18
2024-05-19,Basspod,Dimension,23:30
2024-05-19,Wasteland,Lady Faith,23:30
2024-05-19,Stereo Bloom,Shiba San,23:30
2024-05-20,Quantum Valley,Yotto,0:00
2024-05-20,Cosmic Meadow,Diplo,0:15
2024-05-20,Circuit Grounds,Martin Garrix,0:15
2024-05-20,Basspod,ATLiens,0:30
2024-05-20,Wasteland,Devin Wild B2B Keltek,0:30
2024-05-20,Stereo Bloom,HUGEL x Friends,0:30
            """.trimIndent()
        )
    )
}