package com.example.myswimsmartdb.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Training

// ViewModel zur gemeinsamen Nutzung von Kurs- und Training-Daten zwischen verschiedenen Bildschirmen
class SharedViewModel : ViewModel() {
    var selectedCourse: Kurs? = null
    var selectedTraining: Training? = null

    // Funktion zur Auswahl eines Kurses
    fun selectCourse(course: Kurs) {
        selectedCourse = course
    }

    // Funktion zur Auswahl eines Trainings
    fun selectTraining(training: Training) {
        selectedTraining = training
    }
}
