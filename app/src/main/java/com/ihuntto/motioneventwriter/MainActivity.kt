package com.ihuntto.motioneventwriter

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.ihuntto.motioneventwriter.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        binding.saveBtn.setOnClickListener {
            if (!binding.pointerView.isPointersEmpty()) {
                createFileLauncher.launch(getCurrentTimeFileName("pointer_data"))
            } else {
                Toast.makeText(this, R.string.empty_pointers, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val createFileLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
            if (uri == null) {
                Toast.makeText(this, R.string.no_file_selected, Toast.LENGTH_SHORT).show()
            } else {
                val pointers = binding.pointerView.getPointers()
                contentResolver.openOutputStream(uri)?.let {
                    write(it, pointers)
                }
            }
        }

    fun getCurrentTimeFileName(prefix: String = "file", extension: String = "txt"): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "${prefix}_${timeStamp}.$extension"
    }
}
