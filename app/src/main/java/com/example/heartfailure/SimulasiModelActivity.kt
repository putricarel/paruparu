package com.example.heartfailure

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "predikparu.tflite"

    private lateinit var resultText: TextView
    private lateinit var Usia: EditText
    private lateinit var Jenis_Kelamin: EditText
    private lateinit var Merokok: EditText
    private lateinit var Bekerja: EditText
    private lateinit var Rumah_Tangga: EditText
    private lateinit var Aktivitas_Begadang: EditText
    private lateinit var Aktivitas_Olahraga: EditText
    private lateinit var Asuransi: EditText
    private lateinit var Penyakit_Bawaan: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtResult)
        Usia = findViewById(R.id.Usia)
        Jenis_Kelamin = findViewById(R.id.Jenis_Kelamin)
        Merokok = findViewById(R.id.Merokok)
        Bekerja = findViewById(R.id.Bekerja)
        Rumah_Tangga = findViewById(R.id.Rumah_Tangga)
        Aktivitas_Begadang = findViewById(R.id.Aktivitas_Begadang)
        Aktivitas_Olahraga = findViewById(R.id.Aktivitas_Olahraga)
        Asuransi = findViewById(R.id.Asuransi)
        Penyakit_Bawaan = findViewById(R.id.Penyakit_Bawaan)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                Usia.text.toString(),
                Jenis_Kelamin.text.toString(),
                Merokok.text.toString(),
                Bekerja.text.toString(),
                Rumah_Tangga.text.toString(),
                Aktivitas_Begadang.text.toString(),
                Aktivitas_Olahraga.text.toString(),
                Asuransi.text.toString(),
                Penyakit_Bawaan.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Negatif"
                }else if (result == 1){
                    resultText.text = "Positif"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(10)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String, input9: String): Int{
        val inputVal = FloatArray(10)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        inputVal[8] = input9.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}