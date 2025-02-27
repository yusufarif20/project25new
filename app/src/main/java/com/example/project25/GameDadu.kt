package com.example.project25

import Question
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GameDadu : AppCompatActivity() {
    // Previous properties remain the same
    private val questions = listOf(
        Question(
            backgroundImage = R.drawable.layoutdadu,
            answers = listOf(
                R.drawable.dadutiga,
                R.drawable.dadusatu,
                R.drawable.daduempat,
                R.drawable.daduenam
            ),
            correctAnswerIndex = 2
        ),
        Question(
            backgroundImage = R.drawable.layoutdadudua,
            answers = listOf(
                R.drawable.dadulima,
                R.drawable.dadusatu,
                R.drawable.dadudua,
                R.drawable.daduempat
            ),
            correctAnswerIndex = 1
        ),
        Question(
            backgroundImage = R.drawable.layoutdadutiga,
            answers = listOf(
                R.drawable.dadulima,
                R.drawable.dadutiga,
                R.drawable.dadusatu,
                R.drawable.dadudua
            ),
            correctAnswerIndex = 0
        ),
        Question(
            backgroundImage = R.drawable.layoutdaduempat,
            answers = listOf(
                R.drawable.dadutiga,
                R.drawable.daduempat,
                R.drawable.dadudua,
                R.drawable.dadusatu
            ),
            correctAnswerIndex = 0
        ),
        Question(
            backgroundImage = R.drawable.layoutdadulima,
            answers = listOf(
                R.drawable.dadutiga,
                R.drawable.dadudua,
                R.drawable.daduenam,
                R.drawable.daduempat
            ),
            correctAnswerIndex = 1
        )
    )
    private var currentQuestionIndex = 0
    private var currentScore: Int = 0
    private var True: Int = 0
    private var False: Int = 0
    private var star: Int = 0
    private var selectedAnswerIndex: Int? = null
    private var lastPlayerX = 0f
    private var lastPlayerY = 0f

    // Tambahan untuk sistem hadiah
    private var currentHadiah = 0
    private var completedHadiah = mutableSetOf<Int>()

    private lateinit var auth: FirebaseAuth
    private lateinit var questionBackground: ImageView
    private lateinit var answerImageViews: List<ImageView>
    private lateinit var framesWithBorders: List<Pair<FrameLayout, ImageView>>
    private lateinit var submitButton: ImageView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_dadu)

        // Initialize views
        questionBackground = findViewById(R.id.splashBackground)
        submitButton = findViewById(R.id.submit)
        backButton = findViewById(R.id.back)

        // Get data from intent
        lastPlayerX = intent.getFloatExtra("lastX", 0f)
        lastPlayerY = intent.getFloatExtra("lastY", 0f)
        var monster = intent.getIntExtra("monster", 0)
        star = intent.getIntExtra("star", 0)
        Log.d("GameDadu", "Nilai star yang diterima: $star")

        // Ambil data hadiah
        currentHadiah = intent.getIntExtra("currentHadiah", 0)
        val completedHadiahString = intent.getStringExtra("completedHadiah") ?: ""
        completedHadiah = if (completedHadiahString.isEmpty()) {
            mutableSetOf()
        } else {
            completedHadiahString.split(",").map { it.toInt() }.toMutableSet()
        }

        answerImageViews = listOf(
            findViewById(R.id.dadu1),
            findViewById(R.id.dadu2),
            findViewById(R.id.dadu3),
            findViewById(R.id.dadu4)
        )

        framesWithBorders = listOf(
            Pair(findViewById(R.id.frame_dadu1), findViewById(R.id.border_dadu1)),
            Pair(findViewById(R.id.frame_dadu2), findViewById(R.id.border_dadu2)),
            Pair(findViewById(R.id.frame_dadu3), findViewById(R.id.border_dadu3)),
            Pair(findViewById(R.id.frame_dadu4), findViewById(R.id.border_dadu4))
        )

        updateBackButtonVisibility()

        backButton.setOnClickListener {
            if (currentQuestionIndex > 0) {
                selectedAnswerIndex = null
                resetAllBorders()
                currentQuestionIndex--
                loadQuestion(currentQuestionIndex)
                updateBackButtonVisibility()
            }
        }

        submitButton.setOnClickListener {
            // Check final answer before submitting
            checkAnswerAndUpdateScore()

            // Tambahkan hadiah yang baru diselesaikan
            completedHadiah.add(currentHadiah)

            // Create intent for returning to route
            val intent = Intent(this, rute::class.java)
            // Add all necessary data to intent
            intent.putExtra("lastX", lastPlayerX)
            intent.putExtra("lastY", lastPlayerY)
            intent.putExtra("monster", monster)
            intent.putExtra("star", star)
            intent.putExtra("completedHadiah", completedHadiah.joinToString(","))

            startActivity(intent)
            finish()

        }

        loadQuestion(currentQuestionIndex)
    }

    private fun addStarsValue() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            val uid = user.uid
            val db: DatabaseReference = FirebaseDatabase.getInstance().reference
            val useraData = db.child("users").child(uid).child("stars")

            useraData.get().addOnSuccessListener { snapshot ->
                val currentStars = snapshot.getValue(Int::class.java) ?:0
                useraData.setValue(currentStars + 1)

                    .addOnSuccessListener {
                        Toast.makeText(this, "menerima point", Toast.LENGTH_SHORT).show()
                    }
            }


        }

    }

    private fun checkAnswerAndUpdateScore() {
        star = intent.getIntExtra("star", 0)
        selectedAnswerIndex?.let { selected ->
            val isCorrect = selected == questions[currentQuestionIndex].correctAnswerIndex
            if (isCorrect) {
                star++
                True += 1
                currentScore += 20
                addStarsValue()
            } else {
                False += 1
            }
        }
    }

    private fun updateBackButtonVisibility() {
        backButton.visibility = if (currentQuestionIndex > 0) View.VISIBLE else View.GONE
    }

    private fun resetAllBorders() {
        framesWithBorders.forEach { (frame, border) ->
            border.visibility = View.GONE
            frame.setBackgroundResource(0)
        }
    }

    private fun loadQuestion(index: Int) {
        val question = questions[index]
        questionBackground.setImageResource(question.backgroundImage)

        answerImageViews.forEachIndexed { i, imageView ->
            imageView.setImageResource(question.answers[i])
        }

        framesWithBorders.forEachIndexed { i, pair ->
            val frame = pair.first
            val border = pair.second

            border.visibility = View.GONE
            frame.setBackgroundResource(0)

            frame.setOnClickListener {
                resetAllBorders()
                border.visibility = View.VISIBLE
                selectedAnswerIndex = i
            }
        }
    }
}