package com.example.katalogku

import android.Manifest
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

class EditBuku : AppCompatActivity() {
    companion object{
        const val NAMEBUKU = "namebuku"
        const val KATEGORIBUKU = "kategoribuku"
        const val TAHUNTERBIT = "tahunterbit"
        const val PENULIS = "penulis"
        const val DESKRIPSI = "deskripsi"
        const val FOTO = "foto"
    }

    private lateinit var inputNamaBuku: EditText
    private lateinit var inputKategori: EditText
    private lateinit var inputTahun: EditText
    private lateinit var inputPenulis: EditText
    private lateinit var inputDeskripsi: EditText
    private lateinit var imgPreviewCover: ImageView
    private lateinit var txtChooseFile: TextView
    private lateinit var frameLayoutImagePicker: View // Untuk area klik gambar

    private var selectedImageUri: Uri? = null // Untuk menyimpan URI gambar yang dipilih/lama

    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                imgPreviewCover.setImageURI(uri) // Menampilkan gambar yang dipilih
                imgPreviewCover.visibility = View.VISIBLE // ImageView dijadikan terlihat
                txtChooseFile.visibility = View.GONE // TextView disembunyikan
            } else {
                // Jika tidak ada gambar dipilih (dibatalkan), reset ke tampilan awal
                // Cek apakah ada gambar lama yang bisa ditampilkan kembali
                val originalFotoUriString = intent.getStringExtra(FOTO)
                if (!originalFotoUriString.isNullOrEmpty()) {
                    selectedImageUri = Uri.parse(originalFotoUriString)
                    imgPreviewCover.setImageURI(selectedImageUri)
                    imgPreviewCover.visibility = View.VISIBLE
                    txtChooseFile.visibility = View.GONE
                } else {
                    selectedImageUri = null
                    imgPreviewCover.visibility = View.GONE
                    txtChooseFile.visibility = View.VISIBLE
                    imgPreviewCover.setImageResource(0) // Hapus gambar dari ImageView
                }
            }
        }

    // ActivityResultLauncher untuk meminta izin READ_EXTERNAL_STORAGE
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Izin diberikan, luncurkan pemilih gambar
                pickImageLauncher.launch("image/*")
            } else {
                // Izin ditolak, beri tahu pengguna
                Toast.makeText(this, "Izin akses penyimpanan diperlukan untuk memilih gambar.", Toast.LENGTH_SHORT).show()
            }
        }

    private lateinit var originalName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_buku)

        val db = DBHelper(this, null)

        inputNamaBuku = findViewById(R.id.inputNamaBuku)
        inputKategori = findViewById(R.id.inputKategori)
        inputTahun = findViewById(R.id.inputTahun)
        inputPenulis = findViewById(R.id.inputPenulis)
        inputDeskripsi = findViewById(R.id.inputDeskripsi)
        imgPreviewCover = findViewById(R.id.imgPreviewCover)
        txtChooseFile = findViewById(R.id.txtChooseFile)
        frameLayoutImagePicker = findViewById(R.id.frameLayoutImagePicker)

        // Ambil data buku dari Intent
        originalName = intent.getStringExtra(NAMEBUKU) ?: ""
        val kategori = intent.getStringExtra(KATEGORIBUKU)
        val tahun = intent.getStringExtra(TAHUNTERBIT)
        val penulis = intent.getStringExtra(PENULIS)
        val deskripsi = intent.getStringExtra(DESKRIPSI)
        val fotoUriString = intent.getStringExtra(FOTO) // Ambil URI string foto

        // Isi EditText dengan data yang diterima
        inputNamaBuku.setText(originalName)
        inputKategori.setText(kategori)
        inputTahun.setText(tahun)
        inputPenulis.setText(penulis)
        inputDeskripsi.setText(deskripsi)

        // Tampilkan gambar jika ada URI
        if (!fotoUriString.isNullOrEmpty()) {
            selectedImageUri = Uri.parse(fotoUriString)
            imgPreviewCover.setImageURI(selectedImageUri)
            imgPreviewCover.visibility = View.VISIBLE
            txtChooseFile.visibility = View.GONE
        } else {
            imgPreviewCover.visibility = View.GONE
            txtChooseFile.visibility = View.VISIBLE
        }

        // Set listener untuk frameLayoutImagePicker (area klik untuk pilih gambar)
        frameLayoutImagePicker.setOnClickListener {
            checkAndRequestPermission()
        }

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        btnSubmit.setOnClickListener {
            val updatedNamaBuku = inputNamaBuku.text.toString()
            val updatedKategori = inputKategori.text.toString()
            val updatedTahun = inputTahun.text.toString()
            val updatedPenulis = inputPenulis.text.toString()
            val updatedDeskripsi = inputDeskripsi.text.toString()
            val updatedFotoPath = selectedImageUri?.toString()

            if (updatedNamaBuku.isEmpty() || updatedKategori.isEmpty() || updatedTahun.isEmpty() ||
                updatedPenulis.isEmpty() || updatedDeskripsi.isEmpty() || updatedFotoPath.isNullOrEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom dan pilih foto cover buku.", Toast.LENGTH_SHORT).show()
            } else {
                db.UpdateData(
                    originalName,          // oldName
                    updatedNamaBuku,       // newName
                    updatedKategori,
                    updatedTahun,
                    updatedPenulis,
                    updatedDeskripsi,
                    updatedFotoPath ?: ""
                )
                Toast.makeText(this, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }


        btnCancel.setOnClickListener {
            finish() // Kembali ke activity sebelumnya (DaftarBuku)
        }
    }

    // Fungsi untuk memeriksa dan meminta izin (sama seperti di TambahBuku.kt)
    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (API 33) ke atas tidak memerlukan READ_EXTERNAL_STORAGE untuk memilih media
            pickImageLauncher.launch("image/*")
        } else {
            // Android 12 (API 32) ke bawah memerlukan READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch("image/*")
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}