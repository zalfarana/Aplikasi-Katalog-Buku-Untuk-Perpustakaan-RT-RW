package com.example.katalogku

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID // Untuk membuat nama file unik

class TambahBuku : AppCompatActivity() {

    private lateinit var inputNamaBuku: EditText
    private lateinit var inputKategori: EditText
    private lateinit var inputTahun: EditText
    private lateinit var inputPenulis: EditText
    private lateinit var inputDeskripsi: EditText
    private lateinit var imgPreviewCover: ImageView
    private lateinit var txtChooseFile: TextView
    private lateinit var frameLayoutImagePicker: View

    private lateinit var btnSubmit: Button
    private lateinit var btnCancel: Button

    private var selectedImageUri: Uri? = null
    // Variabel untuk menyimpan jalur file gambar yang telah disimpan
    private var savedImagePath: String? = null

    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                imgPreviewCover.setImageURI(selectedImageUri)
                imgPreviewCover.visibility = View.VISIBLE
                txtChooseFile.visibility = View.GONE

                // *********** PERUBAHAN PENTING DI SINI ***********
                // Panggil fungsi untuk menyimpan gambar ke penyimpanan internal
                saveImageToInternalStorage(selectedImageUri!!)?.let { path ->
                    savedImagePath = path
                    Toast.makeText(this, "Gambar telah ditambahkan", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show()
                    savedImagePath = null // Pastikan path menjadi null jika gagal
                }
                // **************************************************
            } else {
                Toast.makeText(this, "Tidak ada gambar yang dipilih.", Toast.LENGTH_SHORT).show()
                savedImagePath = null // Reset path jika tidak ada gambar dipilih
            }
        }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                pickImageLauncher.launch("image/*")
            } else {
                Toast.makeText(this, "Izin penyimpanan ditolak. Tidak dapat memilih gambar.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_buku)

        inputNamaBuku = findViewById(R.id.inputNamaBuku)
        inputKategori = findViewById(R.id.inputKategori)
        inputTahun = findViewById(R.id.inputTahun)
        inputPenulis = findViewById(R.id.inputPenulis)
        inputDeskripsi = findViewById(R.id.inputDeskripsi)
        imgPreviewCover = findViewById(R.id.imgPreviewCover)
        txtChooseFile = findViewById(R.id.txtChooseFile)
        frameLayoutImagePicker = findViewById(R.id.frameLayoutImagePicker)

        btnSubmit = findViewById(R.id.btnSubmit)
        btnCancel = findViewById(R.id.btnCancel)

        frameLayoutImagePicker.setOnClickListener {
            checkAndRequestPermission()
        }

        btnSubmit.setOnClickListener {
            val namaBuku = inputNamaBuku.text.toString()
            val kategori = inputKategori.text.toString()
            val tahun = inputTahun.text.toString()
            val penulis = inputPenulis.text.toString()
            val deskripsi = inputDeskripsi.text.toString()

            // *********** PERUBAHAN PENTING DI SINI ***********
            // Pastikan savedImagePath tidak null sebelum menyimpan
            if (namaBuku.isEmpty() || kategori.isEmpty() || tahun.isEmpty() || penulis.isEmpty() || deskripsi.isEmpty() || savedImagePath.isNullOrEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom dan pilih gambar!", Toast.LENGTH_SHORT).show()
            } else {
                val db = DBHelper(this, null)
                // Gunakan savedImagePath (jalur file absolut) untuk disimpan ke database
                db.AddData(namaBuku, kategori, tahun, penulis, deskripsi, savedImagePath!!)
                Toast.makeText(this, "Buku berhasil ditambahkan!", Toast.LENGTH_SHORT).show()

                inputNamaBuku.text.clear()
                inputKategori.text.clear()
                inputTahun.text.clear()
                inputPenulis.text.clear()
                inputDeskripsi.text.clear()
                imgPreviewCover.visibility = View.GONE
                txtChooseFile.visibility = View.VISIBLE
                selectedImageUri = null
                savedImagePath = null // Reset savedImagePath setelah berhasil ditambahkan
            }
            // **************************************************
        }

        btnCancel.setOnClickListener {
            inputNamaBuku.text.clear()
            inputKategori.text.clear()
            inputTahun.text.clear()
            inputPenulis.text.clear()
            inputDeskripsi.text.clear()
            imgPreviewCover.visibility = View.GONE
            txtChooseFile.visibility = View.VISIBLE
            selectedImageUri = null
            savedImagePath = null // Reset savedImagePath
            Toast.makeText(this, "Input dibersihkan.", Toast.LENGTH_SHORT).show()
        }

        // NAV BAR
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.action_tambah

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, Homepage::class.java))
                    true
                }
                R.id.action_tambah -> {
                    true
                }
                R.id.action_books -> {
                    startActivity(Intent(this, DaftarBuku::class.java))
                    true
                }
                else -> false
            }
        }
        // END NAV BAR
    }

    // *********** FUNGSI BARU UNTUK MENYIMPAN GAMBAR ***********
    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "book_cover_${UUID.randomUUID()}.jpg" // Nama file unik
            // Menyimpan di direktori files internal aplikasi
            val outputFile = File(applicationContext.filesDir, fileName)
            val outputStream = FileOutputStream(outputFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            outputFile.absolutePath // Mengembalikan jalur absolut file yang disimpan
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    // **************************************************

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pickImageLauncher.launch("image/*")
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch("image/*")
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}