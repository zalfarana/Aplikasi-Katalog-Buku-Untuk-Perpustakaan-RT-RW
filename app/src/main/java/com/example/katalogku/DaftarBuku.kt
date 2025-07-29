package com.example.katalogku

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DaftarBuku : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var recyclerDaftarBuku: RecyclerView
    private lateinit var adapterDaftarBuku: DBAdapterDaftarBuku // Menggunakan DBAdapterDaftarBuku
    private lateinit var tvKategori: TextView
    private lateinit var tvTahun: TextView
    private lateinit var etSearch: EditText

    private var currentCategoryFilter: String? = null
    private var currentYearFilter: String? = null
    private var currentSearchKeyword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daftar_buku)

        db = DBHelper(this, null)
        recyclerDaftarBuku = findViewById(R.id.recyclerDaftarBuku)
        tvKategori = findViewById(R.id.tvKategori)
        tvTahun = findViewById(R.id.tvTahun)
        etSearch = findViewById(R.id.etSearch)

        // Setup RecyclerView
        recyclerDaftarBuku.layoutManager = GridLayoutManager(this, 2)
        // Inisialisasi adapter dengan list kosong, akan diisi oleh filterBooks()
        adapterDaftarBuku = DBAdapterDaftarBuku(ArrayList())
        recyclerDaftarBuku.adapter = adapterDaftarBuku

        // Set OnItemClickCallback for DaftarBuku adapter
        adapterDaftarBuku.setOnItemClickCallback(object : DBAdapterDaftarBuku.OnItemClickCallback {
            override fun onItemClicked(data: DBModel) {
                // Handle item click (misal: tampilkan detail singkat, atau buka halaman detail)
                Toast.makeText(this@DaftarBuku, "Anda mengklik: ${data.namebuku}", Toast.LENGTH_SHORT).show()
            }

            override fun onDetailsClicked(data: DBModel) {
                //
                val context = this;
                // Aksi ketika tombol "Edit Buku" diklik
                MaterialAlertDialogBuilder(this@DaftarBuku)
                    .setTitle("Data : ${data.namebuku}")
                    .setMessage("What do you want to do?")
                    .setPositiveButton("Delete"){_,_ ->
                        db.DeleteData("${data.namebuku}")
                        Toast.makeText(this@DaftarBuku, "Data has been deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Update"){_,_ ->
                        val moveIntent = Intent(this@DaftarBuku, EditBuku::class.java).apply {
                            putExtra(EditBuku.NAMEBUKU, "${data.namebuku}")
                            putExtra(EditBuku.KATEGORIBUKU, "${data.kategoribuku}")
                            putExtra(EditBuku.TAHUNTERBIT, "${data.tahunterbit}")
                            putExtra(EditBuku.PENULIS, "${data.penulis}")
                            putExtra(EditBuku.DESKRIPSI, "${data.deskripsi}")
                            putExtra(EditBuku.FOTO, "${data.foto}")
                        }
                        startActivity(moveIntent)
                        finish()
                    }
                    .setNeutralButton("Cancel"){_,_ ->
                    }
                    .show()
                // Jika ingin membuka halaman edit buku, Anda bisa menambahkan Intent di sini
                // val intent = Intent(this@DaftarBuku, EditBukuActivity::class.java)
                // intent.putExtra("book_name", data.namebuku) // Contoh mengirim data
                // startActivity(intent)
            }
        })

        // Inisialisasi filter default
        currentCategoryFilter = "Semua Kategori"
        currentYearFilter = "Semua Tahun"
        tvKategori.text = currentCategoryFilter // Set teks awal
        tvTahun.text = currentYearFilter // Set teks awal

        // Muat data awal (semua buku)
        filterBooks()

        // Set listener untuk filter Kategori
        tvKategori.setOnClickListener {
            showCategoryFilterDialog()
        }

        // Set listener untuk filter Tahun
        tvTahun.setOnClickListener {
            showYearFilterDialog()
        }

        // Set listener untuk Search EditText
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchKeyword = s?.toString()
                filterBooks() // Panggil filter setiap kali teks berubah
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Listener untuk tombol "Done" atau "Search" pada keyboard
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                filterBooks() // Panggil filter saat pengguna menekan Search/Done
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
                true
            } else {
                false
            }
        }

        // NAV BAR
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.action_books

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, Homepage::class.java))
                    true
                }
                R.id.action_tambah -> {
                    startActivity(Intent(this, TambahBuku::class.java))
                    true
                }
                R.id.action_books -> {
                    true // Sudah di DaftarBuku, tidak perlu aksi
                }
                else -> false
            }
        }
    }

    private fun showCategoryFilterDialog() {
        val categories = db.getAllCategories()
        val options = mutableListOf("Semua Kategori")
        options.addAll(categories)

        MaterialAlertDialogBuilder(this)
            .setTitle("Pilih Kategori")
            .setItems(options.toTypedArray()) { dialog, which ->
                val selectedCategory = options[which]
                tvKategori.text = selectedCategory
                currentCategoryFilter = selectedCategory
                filterBooks()
                dialog.dismiss()
            }
            .show()
    }

    private fun showYearFilterDialog() {
        val years = db.getAllYears()
        val options = mutableListOf("Semua Tahun")
        options.addAll(years)

        MaterialAlertDialogBuilder(this)
            .setTitle("Pilih Tahun Terbit")
            .setItems(options.toTypedArray()) { dialog, which ->
                val selectedYear = options[which]
                tvTahun.text = selectedYear
                currentYearFilter = selectedYear
                filterBooks()
                dialog.dismiss()
            }
            .show()
    }

    private fun filterBooks() {
        val filteredList = db.getFilteredData(
            currentCategoryFilter,
            currentYearFilter,
            currentSearchKeyword
        )
        // Panggil updateData pada adapter
        adapterDaftarBuku.updateData(filteredList) // <<-- PERBAIKAN DI SINI

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Tidak ada buku yang ditemukan dengan filter ini.", Toast.LENGTH_SHORT).show()
        }
    }
}