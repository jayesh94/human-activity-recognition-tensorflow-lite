package com.ascetx.har

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.math.BigDecimal


class MainActivity : AppCompatActivity(), SensorEventListener {

    companion object {
        private const val TIME_STAMP = 100
        private const val TAG = "MainActivity"

        private val ax = mutableListOf<Float>()
        private val ay = mutableListOf<Float>()
        private val az = mutableListOf<Float>()
        private val gx = mutableListOf<Float>()
        private val gy = mutableListOf<Float>()
        private val gz = mutableListOf<Float>()
        private val lx = mutableListOf<Float>()
        private val ly = mutableListOf<Float>()
        private val lz = mutableListOf<Float>()
    }

    private lateinit var mSensorManager: SensorManager
    private var mAccelerometer: Sensor? = null
    private var mGyroscope: Sensor? = null
    private var mLinearAcceleration: Sensor? = null

    private lateinit var results: FloatArray
    private lateinit var classifier: ActivityClassifier

    private lateinit var bikingTextView: TextView
    private lateinit var downstairsTextView: TextView
    private lateinit var joggingTextView: TextView
    private lateinit var sittingTextView: TextView
    private lateinit var standingTextView: TextView
    private lateinit var upstairsTextView: TextView
    private lateinit var walkingTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initLayoutItems()

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        classifier = ActivityClassifier(applicationContext)

        if (mAccelerometer == null)
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        if (mGyroscope == null)
            mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST)
        if (mLinearAcceleration == null)
            mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST)


    }

    private fun initLayoutItems() {
        bikingTextView = findViewById(R.id.biking_TextView)
        downstairsTextView = findViewById(R.id.downstairs_TextView)
        joggingTextView = findViewById(R.id.jogging_TextView)
        sittingTextView = findViewById(R.id.sitting_TextView)
        standingTextView = findViewById(R.id.standing_TextView)
        upstairsTextView = findViewById(R.id.upstairs_TextView)
        walkingTextView = findViewById(R.id.walking_TextView)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                ax.add(event.values[0])
                ay.add(event.values[1])
                az.add(event.values[2])
            }
            Sensor.TYPE_GYROSCOPE -> {
                gx.add(event.values[0])
                gy.add(event.values[1])
                gz.add(event.values[2])
            }
            else -> {
                lx.add(event.values[0])
                ly.add(event.values[1])
                lz.add(event.values[2])
            }
        }

        predictActivity()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    private fun predictActivity() {
        val data = mutableListOf<Float>()
        if (ax.size >= TIME_STAMP && ay.size >= TIME_STAMP && az.size >= TIME_STAMP
            && gx.size >= TIME_STAMP && gy.size >= TIME_STAMP && gz.size >= TIME_STAMP
            && lx.size >= TIME_STAMP && ly.size >= TIME_STAMP && lz.size >= TIME_STAMP
        ) {
            data.addAll(ax.subList(0, TIME_STAMP))
            data.addAll(ay.subList(0, TIME_STAMP))
            data.addAll(az.subList(0, TIME_STAMP))

//            data.addAll(gx.subList(0, TIME_STAMP))
//            data.addAll(gy.subList(0, TIME_STAMP))
//            data.addAll(gz.subList(0, TIME_STAMP))
////
//            data.addAll(lx.subList(0, TIME_STAMP))
//            data.addAll(ly.subList(0, TIME_STAMP))
//            data.addAll(lz.subList(0, TIME_STAMP))

            results = classifier.predictProbabilities(toFloatArray(data))
            Log.i(TAG, "ax: $ax")
            Log.i(TAG, "predictActivity: ${results.contentToString()}")

            bikingTextView.text = "Biking: \t${round(results[0], 2)}"
            downstairsTextView.text = "DownStairs: \t${round(results[1], 2)}"
            joggingTextView.text = "Jogging: \t${round(results[2], 2)}"
            sittingTextView.text = "Sitting: \t${round(results[3], 2)}"
            standingTextView.text = "Standing: \t${round(results[4], 2)}"
            upstairsTextView.text = "Upstairs: \t${round(results[5], 2)}"
            walkingTextView.text = "Walking: \t${round(results[6], 2)}"

            data.clear()
            ax.clear()
            ay.clear()
            az.clear()
            gx.clear()
            gy.clear()
            gz.clear()
            lx.clear()
            ly.clear()
            lz.clear()
        }
    }

    private fun round(value: Float, decimalPlaces: Int): Float {
        val bigDecimal = BigDecimal(value.toString())
        return bigDecimal.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP).toFloat()
    }

    private fun toFloatArray(data: List<Float>): FloatArray {
        return data.map { it ?: Float.NaN }.toFloatArray()
    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST)
        mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSensorManager.unregisterListener(this)
    }
}