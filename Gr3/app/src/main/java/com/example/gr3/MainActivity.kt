package com.example.gr3

import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.log10
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    lateinit var photoUri: Uri
    lateinit var picTakerLauncher: ActivityResultLauncher<Uri>
    lateinit var picPickerLauncher: ActivityResultLauncher<Intent>
    lateinit var picActivity: PicActivity
    val imageUris = mutableListOf<Uri>()

    lateinit var audioRecord: AudioRecord
    lateinit var startStop: Button
    var isRecording = false

    val messwertList = mutableListOf<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val settingsButton = findViewById<Button>(R.id.settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        startStop = findViewById<Button>(R.id.startStop)
        val messung = findViewById<TextView>(R.id.messung)
        val list = findViewById<Button>(R.id.list)

        val mapButtonPH = findViewById<Button>(R.id.mapButtonPH)
        mapButtonPH.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        val requestPermissionLauncherCamera =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Toast.makeText(this, "Kameraberechtigung erhalten!", Toast.LENGTH_SHORT).show()
                    Log.d("PermissionRequest", "Kameraberechtigung erhalten!")
                } else {
                    Toast.makeText(this, "Keine Kameraberechtigung!", Toast.LENGTH_SHORT).show()
                    Log.d("PermissionRequest", "Keine Kameraberechtigung erhalten!")
                }
            }
        requestPermissionLauncherCamera.launch(CAMERA)

        val requestPermissionLauncher =
            this.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Toast.makeText(this, "Berechtigung erhalten!", Toast.LENGTH_LONG).show()
                    Log.d("PermissionRequest", "Berechtigung erhalten!")
                } else {
                    Toast.makeText(this, "Keine Berechtigung!", Toast.LENGTH_LONG).show()
                    Log.d("PermissionRequest", "Keine Berechtigung!")
                }
            }
        requestPermissionLauncher.launch(RECORD_AUDIO)



        picTakerLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageUris.add(photoUri)
                picActivity.notifyItemInserted(imageUris.size - 1)
            }
        }

        val picTaker = findViewById<Button>(R.id.picTaker)
        picTaker.setOnClickListener {
            val imageFile = File.createTempFile("JPEG${SimpleDateFormat("yyyyMMddHHmmss").format(Date())}", ".jpg")
            photoUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)
            picTakerLauncher.launch(photoUri)
        }

        val picPicker = findViewById<Button>(R.id.picPicker)
        picPicker.setOnClickListener {}

        startStop.setOnClickListener {
            if (isRecording) {
                audioRecord.stop()
                audioRecord.release()
                isRecording = false
                startStop.text = "Messung starten"
            } else {

                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    val sampleRate = 44100
                    val bufferSize = AudioRecord.getMinBufferSize(
                        sampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT
                    )
                    audioRecord = AudioRecord(
                        MediaRecorder.AudioSource.DEFAULT,
                        sampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize
                    )
                    audioRecord.startRecording()


                    while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                        val buffer = ShortArray(bufferSize)
                        val result = audioRecord.read(buffer, 0, bufferSize)

                        var totalSquared = 0.0
                        var samplesCount = 0

                        for (i in 0 until result) {
                            totalSquared += buffer[i].toDouble() * buffer[i]
                            samplesCount++
                        }

                        val rms = sqrt(totalSquared / samplesCount)
                        val db = 20 * log10(rms)


                        messwertList.add(db)

                        if (db > 80) {
                            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                        }

                        withContext(Dispatchers.Main) {
                            messung.text = db.toInt().toString()

                            when {
                                db < 50 -> messung.setBackgroundColor(getColor(R.color.green))
                                db in 50.0..70.0 -> messung.setBackgroundColor(getColor(R.color.yellow))
                                db > 70 -> messung.setBackgroundColor(getColor(R.color.red))
                            }
                        }
                        delay(500)
                    }
                }


                isRecording = true
                startStop.text = "Messung stoppen"
            }
        }

        list.setOnClickListener {
            val intent = Intent(this@MainActivity, ListActivity::class.java)
            intent.putExtra("Messwerte", ArrayList(messwertList))
            startActivity(intent)
        }
    }
}