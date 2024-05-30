package com.example.myswimsmartdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myswimsmartdb.db.KursRepository
import com.example.myswimsmartdb.db.LevelRepository
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.ui.Composable.KursScreen

class MainActivity : ComponentActivity() {
    private lateinit var kursRepository: KursRepository
    private lateinit var levelRepository: LevelRepository
    private lateinit var mitgliedRepository: MitgliedRepository
    private lateinit var trainingRepository: TrainingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisieren Sie Ihre Repositories hier
        kursRepository = KursRepository(this)
        levelRepository = LevelRepository(this)
        mitgliedRepository = MitgliedRepository(this)
        trainingRepository = TrainingRepository(this)

        setContent {
            KursScreen(kursRepository, levelRepository, mitgliedRepository, trainingRepository)
        }
    }
}
