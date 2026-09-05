package com.example.better_edt.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.better_edt.data.ConfigRepository
import com.example.better_edt.data.CourseConfig
import com.example.better_edt.model.EdtEvent
import com.example.better_edt.network.EdtFilterEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class ViewMode {
    WEEK_GRID,
    DAY_LIST
}

class EdtViewModel(application: Application) : AndroidViewModel(application) {

    private val configRepository = ConfigRepository(application)
    private val filterEngine = EdtFilterEngine()

    private val _config = MutableStateFlow(CourseConfig())
    val config: StateFlow<CourseConfig> = _config.asStateFlow()

    private val _events = MutableStateFlow<List<EdtEvent>>(emptyList())
    val events: StateFlow<List<EdtEvent>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.WEEK_GRID)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Timetable, 1: Config
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _isFullscreenLandscape = MutableStateFlow(false)
    val isFullscreenLandscape: StateFlow<Boolean> = _isFullscreenLandscape.asStateFlow()

    init {
        loadConfig()
        refreshSchedule()
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun toggleFullscreenLandscape(context: Context) {
        val newFullscreenState = !_isFullscreenLandscape.value
        _isFullscreenLandscape.value = newFullscreenState

        val activity = context.findActivity()
        activity?.let {
            if (newFullscreenState) {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    fun exitFullscreenLandscape(context: Context) {
        if (_isFullscreenLandscape.value) {
            _isFullscreenLandscape.value = false
            val activity = context.findActivity()
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    fun loadConfig() {
        val loaded = configRepository.loadConfig()
        _config.value = loaded
    }

    fun updateConfig(newConfig: CourseConfig) {
        _config.value = newConfig
        configRepository.saveConfig(newConfig)
        refreshSchedule()
    }

    fun toggleCourseGroup(course: String, group: String) {
        val updated = _config.value.toggleCourseGroup(course, group)
        updateConfig(updated)
    }

    fun addCourse(course: String, groups: Set<String>) {
        val updated = _config.value.addCourse(course, groups)
        updateConfig(updated)
    }

    fun removeCourse(course: String) {
        val updated = _config.value.removeCourse(course)
        updateConfig(updated)
    }

    fun updateResource(group: String, resourceId: String) {
        val updated = _config.value.updateGroupResource(group, resourceId)
        updateConfig(updated)
    }

    fun updateCredentials(username: String, password: String) {
        val updated = _config.value.updateCredentials(username, password)
        updateConfig(updated)
    }

    fun resetToDefault() {
        val defaultConfig = configRepository.resetToDefault()
        _config.value = defaultConfig
        refreshSchedule()
    }

    fun refreshSchedule() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val current = _selectedDate.value
            val monday = current.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val sunday = monday.plusDays(6)

            try {
                val fetchedEvents = filterEngine.fetchAndFilterSchedule(
                    config = _config.value,
                    startDate = monday,
                    endDate = sunday
                )
                _events.value = fetchedEvents
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Erreur lors du téléchargement de l'emploi du temps: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun previousWeek() {
        _selectedDate.value = _selectedDate.value.minusWeeks(1)
        refreshSchedule()
    }

    fun nextWeek() {
        _selectedDate.value = _selectedDate.value.plusWeeks(1)
        refreshSchedule()
    }

    fun selectToday() {
        _selectedDate.value = LocalDate.now()
        refreshSchedule()
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        refreshSchedule()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
