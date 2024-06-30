package com.example.myswimsmartdb.ui.Composable.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.db.entities.Training
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class SharedViewModel : ViewModel() {
    private val _selectedCourse = MutableStateFlow<Kurs?>(null)
    val selectedCourse: StateFlow<Kurs?> = _selectedCourse

    private val _selectedTraining = MutableStateFlow<Training?>(null)
    val selectedTraining: StateFlow<Training?> = _selectedTraining

    private val _selectedMembers = MutableStateFlow<List<Mitglied>>(emptyList())
    val selectedMembers: StateFlow<List<Mitglied>> = _selectedMembers

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState

    fun selectCourse(course: Kurs) {
        _selectedCourse.value = course
    }

    fun selectTraining(training: Training) {
        _selectedTraining.value = training
    }

    fun selectMembers(members: List<Mitglied>) {
        _selectedMembers.value = members
    }

    fun startTimer() {
        viewModelScope.launch {
            _timerState.value = _timerState.value.copy(isRunning = true)
            while (_timerState.value.isRunning) {
                delay(10L)
                _timerState.value = _timerState.value.copy(
                    time = _timerState.value.time + 10.toDuration(DurationUnit.MILLISECONDS)
                )
            }
        }
    }

    fun stopTimer() {
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resetTimer() {
        _timerState.value = TimerState()
    }

    data class TimerState(
        val isRunning: Boolean = false,
        val time: Duration = 0.toDuration(DurationUnit.MILLISECONDS)
    )
}
