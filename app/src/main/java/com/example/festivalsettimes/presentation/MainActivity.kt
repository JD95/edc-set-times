/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.festivalsettimes.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.*
import com.example.festivalsettimes.presentation.theme.FestivalSetTimesTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp("Android")
        }
    }
}

val dateOnlyFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
val timeOnlyFormat = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WearApp(greetingName: String) {
    val pagerState = rememberPagerState()
    val days = listOf(friday)
    val day = days[0]
    val currentTime = remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(key1 = Unit) {
        while(isActive) {
            currentTime.value = LocalTime.now()
            delay(1000)
        }
    }
    
    FestivalSetTimesTheme {
        HorizontalPager(pageCount = day.stages.size, state = pagerState) { x ->
            val stage = day.stages[x]
            val stageColor = stageColor(stage.name)
            VerticalPager(pageCount = stage.setTimes.size) { y ->
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

        Box {
            Column {
                Spacer(modifier = Modifier.fillMaxHeight(0.7f))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    text = currentTime.value.format(timeOnlyFormat),
                    fontSize = 20.sp
                )
            }
        }

        HorizontalPageIndicator(
            pageIndicatorState = object : PageIndicatorState {
                override val pageCount: Int
                    get() = 9
                override val pageOffset: Float
                    get() = pagerState.currentPageOffsetFraction
                override val selectedPage: Int
                    get() = pagerState.currentPage

            })
    }
}

// internal fun tileLayout(
//     state: Unit,
//     context: Context,
//     deviceParameters: DeviceParametersBuilders.DeviceParameters
// ) = PrimaryLayout.Builder(deviceParameters)
//     .setContent(
//     )
//     .setPrimaryChipContent(
//         CompactChip.Builder(
//             context,
//             "someText",
//             ModifiersBuilders.Clickable.Builder().build(),
//             deviceParameters
//         )
//             .build()
//     )


data class Set(
    val stage: String,
    val artist: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)

val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
val gridTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun Greeting(
    set: Set = Set(
        "Kinetic Field",
        "Biscits B2B Martin Ikin",
        LocalDateTime.parse("2024-05-17 19:00", timeFormat),
        LocalDateTime.parse("2024-05-17 20:05", timeFormat)
    )
) {
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}