package com.tsunaze.wiggle

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.tsunaze.wiggle.views.WiggleActivity


class Wiggle : SensorEventListener {
    private val mSensorBundles: ArrayList<SensorBundle>
    private val mLock: Any
    private var mThresholdAcceleration: Float
    private var mThresholdShakeNumber: Int
    private var isOpeningWiggle: Boolean = false

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val sensorBundle = SensorBundle(
            sensorEvent.values[0],
            sensorEvent.values[1],
            sensorEvent.values[2],
            sensorEvent.timestamp
        )
        synchronized(mLock) {
            when {
                mSensorBundles.size == 0 -> {
                    mSensorBundles.add(sensorBundle)
                }
                sensorBundle.timestamp - mSensorBundles[mSensorBundles.size - 1].timestamp > INTERVAL -> {
                    mSensorBundles.add(sensorBundle)
                }
                else -> {
                    // Nothing
                }
            }
        }
        performCheck()
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {
        // The accuracy is not likely to change on a real device. Just ignore it.
    }

    private fun setConfiguration(sensibility: Float, shakeNumber: Int) {
        mThresholdAcceleration = sensibility
        mThresholdShakeNumber = shakeNumber
        synchronized(mLock) { mSensorBundles.clear() }
    }

    private fun performCheck() {
        synchronized(mLock) {
            val vector = intArrayOf(0, 0, 0)
            val matrix =
                arrayOf(intArrayOf(0, 0), intArrayOf(0, 0), intArrayOf(0, 0))
            for (sensorBundle in mSensorBundles) {
                if (sensorBundle.xAcc > mThresholdAcceleration && vector[0] < 1) {
                    vector[0] = 1
                    matrix[0][0]++
                }
                if (sensorBundle.xAcc < -mThresholdAcceleration && vector[0] > -1) {
                    vector[0] = -1
                    matrix[0][1]++
                }
                if (sensorBundle.yAcc > mThresholdAcceleration && vector[1] < 1) {
                    vector[1] = 1
                    matrix[1][0]++
                }
                if (sensorBundle.yAcc < -mThresholdAcceleration && vector[1] > -1) {
                    vector[1] = -1
                    matrix[1][1]++
                }
                if (sensorBundle.zAcc > mThresholdAcceleration && vector[2] < 1) {
                    vector[2] = 1
                    matrix[2][0]++
                }
                if (sensorBundle.zAcc < -mThresholdAcceleration && vector[2] > -1) {
                    vector[2] = -1
                    matrix[2][1]++
                }
            }
            for (axis in matrix) {
                for (direction in axis) {
                    if (direction < mThresholdShakeNumber) {
                        return
                    }
                }
            }
            mSensorBundles.clear()
            openWiggle()
        }
    }

    private fun openWiggle() {
        context?.let {
            if (it is WiggleActivity) {
                // Do Nothing
            } else {
                if (isOpeningWiggle) {
                    return
                }
                val bundle = ActivityOptionsCompat.makeCustomAnimation(
                    it,
                    android.R.anim.fade_in, android.R.anim.fade_out
                ).toBundle()
                val intent = Intent(it, WiggleActivity::class.java)
                it.startActivity(intent, bundle)
                isOpeningWiggle = true
                val handler = Handler()
                handler.postDelayed(
                    { isOpeningWiggle = false },
                    500
                )
            }
        }
    }

    /**
     * Convenient object used to store the 3 axis accelerations of the device as well as the current
     * captured time.
     */
    private inner class SensorBundle(
        /**
         * The acceleration on X axis.
         */
        val xAcc: Float,
        /**
         * The acceleration on Y axis.
         */
        val yAcc: Float,
        /**
         * The acceleration on Z axis.
         */
        val zAcc: Float,
        /**
         * The timestamp when to record was captured.
         */
        val timestamp: Long
    ) {

        override fun toString(): String {
            return "SensorBundle{" +
                    "mXAcc=" + xAcc +
                    ", mYAcc=" + yAcc +
                    ", mZAcc=" + zAcc +
                    ", mTimestamp=" + timestamp +
                    '}'
        }

    }

    companion object {
        private const val DEFAULT_THRESHOLD_ACCELERATION = 2.0f
        private const val DEFAULT_THRESHOLD_SHAKE_NUMBER = 3
        private const val INTERVAL = 200
        private var mSensorManager: SensorManager? = null
        private var mSensorEventListener: Wiggle? = null
        var fragments: MutableList<Fragment>? = null
        private var context: Context? = null

        /**
         * Creates a shake detector and starts listening for device shakes. Neither `context` nor
         * `listener` can be null. In that case, a [IllegalArgumentException] will be thrown.
         *
         * @param context  The current Android context.
         * @param listener The callback triggered when the device is shaken.
         * @return true if the shake detector has been created and started correctly, false otherwise.
         */
        fun create(context: Context?): Boolean {
            requireNotNull(context) { "Context must not be null" }
            this.context = context
            if (mSensorManager == null) {
                mSensorManager =
                    context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            }
            mSensorEventListener = Wiggle()
            return mSensorManager?.registerListener(
                mSensorEventListener,
                mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME
            ) ?: false
        }

        /**
         * Starts a previously created shake detector. If no detector has been created before, the method
         * won't create one and will return false.
         *
         * @return true if the shake detector has been started correctly, false otherwise.
         */
        fun resume(context: Context?): Boolean {
            this.context = context
            return if (mSensorManager != null && mSensorEventListener != null) {
                mSensorManager?.registerListener(
                    mSensorEventListener,
                    mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME
                ) ?: false
            } else false
        }

        /**
         * Stops a previously created shake detector. If no detector has been created before, the method
         * will do anything.
         */
        fun stop() {
            mSensorManager?.unregisterListener(mSensorEventListener)
        }

        /**
         * Releases all resources previously created.
         */
        fun destroy() {
            mSensorManager = null
            mSensorEventListener = null
        }

        /**
         * You can update the configuration of the shake detector based on your usage but the default settings
         * should work for the majority of cases. It uses [Wiggle.DEFAULT_THRESHOLD_ACCELERATION]
         * for the sensibility and [Wiggle.DEFAULT_THRESHOLD_SHAKE_NUMBER] for the number
         * of shake required.
         *
         * @param sensibility The sensibility, in G, is the minimum acceleration need to be considered
         * as a shake. The higher number you go, the harder you have to shake your
         * device to trigger a shake.
         * @param shakeNumber The number of shake (roughly) required to trigger a shake.
         */
        fun updateConfiguration(sensibility: Float, shakeNumber: Int) {
            mSensorEventListener?.setConfiguration(sensibility, shakeNumber)
        }
    }

    init {
        mSensorBundles = ArrayList()
        mLock = Any()
        mThresholdAcceleration = DEFAULT_THRESHOLD_ACCELERATION
        mThresholdShakeNumber = DEFAULT_THRESHOLD_SHAKE_NUMBER
    }
}