package com.example.better_edt.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.better_edt.model.EdtEvent
import com.example.better_edt.ui.theme.CmBlueContainerDark
import com.example.better_edt.ui.theme.CmBlueContainerLight
import com.example.better_edt.ui.theme.CmBlueDark
import com.example.better_edt.ui.theme.CmBlueLight
import com.example.better_edt.ui.theme.DeepDarkSurfaceVariant
import com.example.better_edt.ui.theme.LightSurfaceVariant
import com.example.better_edt.ui.theme.ReunionPurpleContainerDark
import com.example.better_edt.ui.theme.ReunionPurpleContainerLight
import com.example.better_edt.ui.theme.ReunionPurpleDark
import com.example.better_edt.ui.theme.ReunionPurpleLight
import com.example.better_edt.ui.theme.TdGreenContainerDark
import com.example.better_edt.ui.theme.TdGreenContainerLight
import com.example.better_edt.ui.theme.TdGreenDark
import com.example.better_edt.ui.theme.TdGreenLight
import com.example.better_edt.ui.theme.TextHighEmphasisDark
import com.example.better_edt.ui.theme.TextHighEmphasisLight
import com.example.better_edt.ui.theme.TpOrangeContainerDark
import com.example.better_edt.ui.theme.TpOrangeContainerLight
import com.example.better_edt.ui.theme.TpOrangeDark
import com.example.better_edt.ui.theme.TpOrangeLight
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun TimetableScreen(viewModel: EdtViewModel) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val context = LocalContext.current

    var selectedEvent by remember { mutableStateOf<EdtEvent?>(null) }

    val monday = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val friday = monday.plusDays(4)

    val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.FRENCH)
    val dateRangeText = "${monday.format(dateFormatter)} - ${friday.format(dateFormatter)}"

    Column(modifier = Modifier.fillMaxSize()) {
        // Asgard/Yggdrasil Inspired Header Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { viewModel.previousWeek() }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Semaine précédente")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dateRangeText.uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        OutlinedButton(
                            onClick = { viewModel.selectToday() },
                            modifier = Modifier.height(28.dp),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Aujourd'hui", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(onClick = { viewModel.nextWeek() }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Semaine suivante")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.refreshSchedule() },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Télécharger", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Button between Download and Fullscreen to select a previously downloaded version
                        VersionSelectionMenu(viewModel = viewModel)

                        Spacer(modifier = Modifier.width(2.dp))

                        // Fullscreen Landscape Toggle Button
                        IconButton(
                            onClick = { viewModel.toggleFullscreenLandscape(context) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Plein Écran Paysage",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // ViewMode Selector Toggle
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.background,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                modifier = Modifier.padding(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { viewModel.setViewMode(ViewMode.WEEK_GRID) },
                                    color = if (viewMode == ViewMode.WEEK_GRID) MaterialTheme.colorScheme.primary else Color.Transparent
                                ) {
                                    Text(
                                        text = "Grille",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false,
                                        color = if (viewMode == ViewMode.WEEK_GRID) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { viewModel.setViewMode(ViewMode.DAY_LIST) },
                                    color = if (viewMode == ViewMode.DAY_LIST) MaterialTheme.colorScheme.primary else Color.Transparent
                                ) {
                                    Text(
                                        text = "Liste",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false,
                                        color = if (viewMode == ViewMode.DAY_LIST) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp
                )
            }
        }

        // Main Schedule View
        if (viewMode == ViewMode.WEEK_GRID) {
            WeekGridView(
                mondayDate = monday,
                events = events,
                onEventClick = { selectedEvent = it }
            )
        } else {
            DayListView(
                mondayDate = monday,
                events = events,
                onEventClick = { selectedEvent = it }
            )
        }
    }

    // Event Detail Modal Dialog
    selectedEvent?.let { event ->
        EventDetailDialog(
            event = event,
            onDismiss = { selectedEvent = null }
        )
    }
}

/**
 * Dedicated Sleek Fullscreen Landscape Timetable View
 */
@Composable
fun FullscreenLandscapeTimetableScreen(viewModel: EdtViewModel) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val context = LocalContext.current

    var selectedEvent by remember { mutableStateOf<EdtEvent?>(null) }

    val monday = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val friday = monday.plusDays(4)

    val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.FRENCH)
    val dateRangeText = "${monday.format(dateFormatter)} - ${friday.format(dateFormatter)}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Minimalist Landscape Overlay Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "EDT PAYSAGE",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = dateRangeText.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.previousWeek() }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Précédent", modifier = Modifier.size(20.dp))
                    }

                    OutlinedButton(
                        onClick = { viewModel.selectToday() },
                        modifier = Modifier.height(28.dp),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Aujourd'hui", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = { viewModel.nextWeek() }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Suivant", modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { viewModel.refreshSchedule() },
                        enabled = !isLoading,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Rafraîchir", modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    VersionSelectionMenu(viewModel = viewModel)

                    Spacer(modifier = Modifier.width(4.dp))

                    // Exit Fullscreen Button
                    IconButton(
                        onClick = { viewModel.exitFullscreenLandscape(context) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = "Quitter Plein Écran",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
        }

        // Full Screen Landscape Week Grid
        WeekGridView(
            mondayDate = monday,
            events = events,
            onEventClick = { selectedEvent = it }
        )
    }

    selectedEvent?.let { event ->
        EventDetailDialog(
            event = event,
            onDismiss = { selectedEvent = null }
        )
    }
}

@Composable
fun WeekGridView(
    mondayDate: LocalDate,
    events: List<EdtEvent>,
    onEventClick: (EdtEvent) -> Unit
) {
    val daysOfWeek = (0..4).map { mondayDate.plusDays(it.toLong()) }
    val dayNames = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi")

    val startHour = 8
    val endHour = 20
    val totalHours = endHour - startHour
    val hourHeightDp = 64.dp

    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
        ) {
            // Hours Column Header
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(44.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("HEURE", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Days Columns Header
            daysOfWeek.forEachIndexed { index, date ->
                val dayFormatter = DateTimeFormatter.ofPattern("d/MM")
                val isToday = date == LocalDate.now()
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(44.dp)
                        .background(
                            if (isToday) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayNames[index].uppercase(),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = date.format(dayFormatter),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        // Grid Content Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
        ) {
            // Hours Time Scale Column
            Column(modifier = Modifier.width(48.dp)) {
                for (hour in startHour until endHour) {
                    Box(
                        modifier = Modifier
                            .height(hourHeightDp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "%02d:00".format(hour),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Day Columns
            daysOfWeek.forEach { date ->
                val dayEvents = events.filter {
                    it.startTime.toLocalDate() == date
                }

                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(hourHeightDp * totalHours)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Background grid line for each hour
                    for (hour in startHour until endHour) {
                        HorizontalDivider(
                            modifier = Modifier.padding(top = hourHeightDp * (hour - startHour)),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        )
                    }

                    // Render events for this day
                    dayEvents.forEach { event ->
                        val eventStartHour = event.startTime.hour + (event.startTime.minute / 60.0)
                        val eventEndHour = event.endTime.hour + (event.endTime.minute / 60.0)

                        val topOffsetDp = hourHeightDp * (eventStartHour - startHour).toFloat()
                        val durationHours = (eventEndHour - eventStartHour).coerceAtLeast(0.5)
                        val heightDp = hourHeightDp * durationHours.toFloat()

                        val (cardColor, textColor, borderColor) = getEventColors(event.courseType)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = topOffsetDp.coerceAtLeast(0.dp))
                                .height(heightDp)
                                .padding(horizontal = 2.dp, vertical = 1.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onEventClick(event) },
                                color = cardColor,
                                border = BorderStroke(1.dp, borderColor)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = event.displayTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = textColor,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 13.sp
                                        )
                                        if (event.teacher.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = event.teacher,
                                                fontSize = 9.sp,
                                                color = textColor.copy(alpha = 0.85f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Column {
                                        if (event.location.isNotBlank()) {
                                            Text(
                                                text = event.location,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = textColor,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Text(
                                            text = event.formattedTimeSpan,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textColor.copy(alpha = 0.75f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayListView(
    mondayDate: LocalDate,
    events: List<EdtEvent>,
    onEventClick: (EdtEvent) -> Unit
) {
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    val daysOfWeek = (0..4).map { mondayDate.plusDays(it.toLong()) }
    val dayNames = listOf("LUN", "MAR", "MER", "JEU", "VEN")

    val selectedDate = daysOfWeek[selectedDayIndex]
    val dayEvents = events.filter { it.startTime.toLocalDate() == selectedDate }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedDayIndex,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            daysOfWeek.forEachIndexed { index, date ->
                val formatter = DateTimeFormatter.ofPattern("dd/MM")
                val isToday = date == LocalDate.now()
                Tab(
                    selected = selectedDayIndex == index,
                    onClick = { selectedDayIndex = index },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayNames[index],
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = date.format(formatter),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (dayEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aucun cours ce jour.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                items(dayEvents) { event ->
                    val (cardColor, textColor, borderColor) = getEventColors(event.courseType)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onEventClick(event) },
                        color = cardColor,
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = event.displayTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textColor,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = textColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = event.formattedTimeSpan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = textColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (event.teacher.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = textColor)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(event.teacher, fontSize = 13.sp, color = textColor.copy(alpha = 0.9f))
                                    }
                                }

                                if (event.location.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = textColor)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(event.location, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventDetailDialog(
    event: EdtEvent,
    onDismiss: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = event.displayTitle,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                DetailRow(icon = Icons.Default.Schedule, label = "Horaires", value = "${event.startTime.format(dateFormatter)}\n${event.formattedTimeSpan}")

                if (event.teacher.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(icon = Icons.Default.Person, label = "Enseignant", value = event.teacher)
                }

                if (event.location.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(icon = Icons.Default.LocationOn, label = "Salle / Bâtiment", value = event.location)
                }

                if (event.rawDescription.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Description brute :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.rawDescription,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp).padding(top = 2.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun getEventColors(courseType: String): Triple<Color, Color, Color> { // (Background, Text, Border)
    val isDark = isSystemInDarkTheme()
    return when (courseType.uppercase()) {
        "CM" -> if (isDark) Triple(CmBlueContainerDark, TextHighEmphasisDark, CmBlueLight) else Triple(CmBlueContainerLight, CmBlueDark, CmBlueLight.copy(alpha = 0.5f))
        "TD" -> if (isDark) Triple(TdGreenContainerDark, TextHighEmphasisDark, TdGreenLight) else Triple(TdGreenContainerLight, TdGreenDark, TdGreenLight.copy(alpha = 0.5f))
        "TP" -> if (isDark) Triple(TpOrangeContainerDark, TextHighEmphasisDark, TpOrangeLight) else Triple(TpOrangeContainerLight, TpOrangeDark, TpOrangeLight.copy(alpha = 0.5f))
        "REUNION" -> if (isDark) Triple(ReunionPurpleContainerDark, TextHighEmphasisDark, ReunionPurpleLight) else Triple(ReunionPurpleContainerLight, ReunionPurpleDark, ReunionPurpleLight.copy(alpha = 0.5f))
        else -> if (isDark) Triple(DeepDarkSurfaceVariant, TextHighEmphasisDark, MaterialTheme.colorScheme.outline) else Triple(LightSurfaceVariant, TextHighEmphasisLight, MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun VersionSelectionMenu(
    viewModel: EdtViewModel,
    modifier: Modifier = Modifier
) {
    val downloadedVersions by viewModel.downloadedVersions.collectAsState()
    val selectedVersionId by viewModel.selectedVersionId.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { showMenu = true },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Sélectionner une version antérieure",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            Text(
                text = "VERSIONS TÉLÉCHARGÉES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
            HorizontalDivider()

            if (downloadedVersions.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Aucune version enregistrée",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    },
                    onClick = { showMenu = false },
                    enabled = false
                )
            } else {
                downloadedVersions.forEachIndexed { index, version ->
                    val isSelected = version.id == selectedVersionId
                    val tag = if (index == 0) "Dernière" else "Version ${index + 1}"

                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = tag,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = version.formattedDate,
                                        fontSize = 12.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Sélectionné",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            viewModel.selectVersion(version.id)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}
