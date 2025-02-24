package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Rect

class GameNomor : AppCompatActivity(), View.OnTouchListener {
    private var dX = 0f
    private var dY = 0f
    private var counter = 0
    private lateinit var keranjang: ImageView
    private lateinit var submitButton: ImageView
    private var droppedViews = mutableSetOf<ImageView>()
    private var viewValues = mutableMapOf<Int, Int>()
    private val maxObjects = 1
    private var isDraggable = true
    private val targetValue = 6
    private var lastPlayerX = 0f
    private var lastPlayerY = 0f

    // Tambahan untuk sistem hadiah
    private var currentHadiah = 0
    private var completedHadiah = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_nomor)

        val satu = findViewById<ImageView>(R.id.satu)
        val dua = findViewById<ImageView>(R.id.dua)
        val tiga = findViewById<ImageView>(R.id.tiga)
        val empat = findViewById<ImageView>(R.id.empat)
        val lima = findViewById<ImageView>(R.id.lima)
        val enam = findViewById<ImageView>(R.id.enam)
        val tuju = findViewById<ImageView>(R.id.tuju)
        keranjang = findViewById(R.id.kotak)
        submitButton = findViewById(R.id.submit)

        // Ambil data dari intent
        lastPlayerX = intent.getFloatExtra("lastX", 0f)
        lastPlayerY = intent.getFloatExtra("lastY", 0f)
        var monster = intent.getIntExtra("monster", 0)

        // Ambil data hadiah
        currentHadiah = intent.getIntExtra("currentHadiah", 0)
        val completedHadiahString = intent.getStringExtra("completedHadiah") ?: ""
        completedHadiah = if (completedHadiahString.isEmpty()) {
            mutableSetOf()
        } else {
            completedHadiahString.split(",").map { it.toInt() }.toMutableSet()
        }

        // Set touch listeners
        satu.setOnTouchListener(this)
        dua.setOnTouchListener(this)
        tiga.setOnTouchListener(this)
        empat.setOnTouchListener(this)
        lima.setOnTouchListener(this)
        enam.setOnTouchListener(this)
        tuju.setOnTouchListener(this)

        // Initialize values for each view
        viewValues[R.id.satu] = 1
        viewValues[R.id.dua] = 2
        viewValues[R.id.tiga] = 3
        viewValues[R.id.empat] = 4
        viewValues[R.id.lima] = 5
        viewValues[R.id.enam] = 6
        viewValues[R.id.tuju] = 7

        submitButton.setOnClickListener {
            if (counter == targetValue) {
                // Tambahkan hadiah yang baru diselesaikan
                completedHadiah.add(currentHadiah)

                // Buat intent untuk kembali ke rute
                val intent = Intent(this, rute::class.java)

                // Masukkan semua data yang diperlukan
                intent.putExtra("monster", monster)
                intent.putExtra("lastX", lastPlayerX)
                intent.putExtra("lastY", lastPlayerY)
                intent.putExtra("completedHadiah", completedHadiah.joinToString(","))

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Jawaban salah!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val imageView = view as ImageView

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (droppedViews.size >= maxObjects && !droppedViews.contains(imageView)) {
                    Toast.makeText(
                        this,
                        "Kotak jawaban sudah penuh! Kurangi objek terlebih dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                dX = view.x - event.rawX
                dY = view.y - event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (droppedViews.contains(imageView) || droppedViews.size < maxObjects) {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
            }
            MotionEvent.ACTION_UP -> {
                val value = viewValues[view.id] ?: 0

                if (isViewOverlapping(imageView, keranjang)) {
                    if (!droppedViews.contains(imageView) && droppedViews.size < maxObjects) {
                        counter += value
                        droppedViews.add(imageView)
                    }
                } else if (!isViewOverlapping(imageView, keranjang) && droppedViews.contains(imageView)) {
                    counter -= value
                    droppedViews.remove(imageView)
                }
            }
        }
        return true
    }

    private fun isViewOverlapping(view1: ImageView, view2: ImageView): Boolean {
        val rect1 = Rect()
        val rect2 = Rect()

        view1.getGlobalVisibleRect(rect1)
        view2.getGlobalVisibleRect(rect2)

        return Rect.intersects(rect1, rect2)
    }
}