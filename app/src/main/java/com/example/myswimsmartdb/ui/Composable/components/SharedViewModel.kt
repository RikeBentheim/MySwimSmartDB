package com.example.myswimsmartdb.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Training

class SharedViewModel : ViewModel() {
    var selectedCourse by mutableStateOf<Kurs?>(null)
    var selectedTraining by mutableStateOf<Training?>(null)

    fun selectCourse(course: Kurs) {
        selectedCourse = course
    }

    fun selectTraining(training: Training) {
        selectedTraining = training
    }
}
