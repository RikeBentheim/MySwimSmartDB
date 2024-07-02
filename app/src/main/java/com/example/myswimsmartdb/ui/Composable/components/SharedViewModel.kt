package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Training
import com.example.myswimsmartdb.db.entities.Mitglied

class SharedViewModel : ViewModel() {
    var selectedCourse by mutableStateOf<Kurs?>(null)
    var selectedTraining by mutableStateOf<Training?>(null)
    var selectedMembers by mutableStateOf<List<Mitglied>>(emptyList())

    fun selectCourse(course: Kurs) {
        selectedCourse = course
    }

    fun selectTraining(training: Training) {
        selectedTraining = training
    }

    fun selectMembers(members: List<Mitglied>) {
        selectedMembers = members
    }

    fun fetchSelectedMembers(): List<Mitglied> {
        return selectedMembers
    }

    fun clearSelectedMembers() {
        selectedMembers = emptyList()
    }
}
