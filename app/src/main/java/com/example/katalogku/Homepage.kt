package com.example.katalogku

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Homepage : AppCompatActivity() {

    lateinit var listBukuBaru: ArrayList<DBModelBukuBaru>
    private lateinit var newBooksRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)

        val listView = findViewById<RecyclerView>(R.id.popularBooksRecycler)

        val db = DBHelper(this, null)

        //POPULER BUKU
        var arrayList: ArrayList<DBModel> = arrayListOf()
        listView.setHasFixedSize(true)
        arrayList.addAll(db.getAllData())
        listView.layoutManager = LinearLayoutManager(this)
        var CardDataPopuler = DBAdapterPopuler(arrayList)
        listView.adapter = CardDataPopuler

        // Set the OnItemClickCallback for the popular books adapter
        CardDataPopuler.setOnItemClickCallback(object : DBAdapterPopuler.OnItemClickCallback {
            override fun onItemClicked(data: DBModel) {
                // Handle regular item click if needed (e.g., open a detailed view)
                Toast.makeText(this@Homepage, "You clicked on: ${data.namebuku}", Toast.LENGTH_SHORT).show()
            }

            override fun onDetailsClicked(data: DBModel) {
                // Show a dialog with book details when "Details" button is clicked
                MaterialAlertDialogBuilder(this@Homepage)
                    .setTitle("Detail Buku")
                    .setMessage(
                        "Judul: ${data.namebuku}\n" +
                                "Kategori: ${data.kategoribuku}\n" +
                                "Tahun Terbit: ${data.tahunterbit}\n" +
                                "Penulis: ${data.penulis}\n" +
                                "Deskripsi: ${data.deskripsi}"
                    )
                    .setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        })
        // END POPULER BUKU

        //BUKU BARU
        // Initialize newBooksRecycler here
        newBooksRecycler = findViewById(R.id.newBooksRecycler) // Tambahkan baris ini
        val newBooksList = db.getAllDataBukuBaru() // Pastikan ini mengambil data buku baru
        val newBooksAdapter = DBAdapterBukuBaru(newBooksList)
        newBooksRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        newBooksRecycler.adapter = newBooksAdapter

        // IMPLEMENTASI KLIK UNTUK NEW BOOKS RECYCLER DI SINI
        newBooksAdapter.setOnItemClickCallback(object : DBAdapterBukuBaru.OnItemClickCallback {
            override fun onItemClicked(data: DBModelBukuBaru) {
                // Ketika item di New Books Recycler diklik, navigasi ke DaftarBuku
                val intent = Intent(this@Homepage, DaftarBuku::class.java)
                startActivity(intent)
                // Anda juga bisa menambahkan Toast untuk konfirmasi jika mau
                Toast.makeText(this@Homepage, "Membuka Daftar Buku dari Buku Baru", Toast.LENGTH_SHORT).show()
            }
        })
        // END BUKU BARU

        // NAV BAR
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.action_home

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
                    startActivity(Intent(this, DaftarBuku::class.java))
                    true
                }
                else -> false
            }
        }
        // END NAV BAR

    }
}