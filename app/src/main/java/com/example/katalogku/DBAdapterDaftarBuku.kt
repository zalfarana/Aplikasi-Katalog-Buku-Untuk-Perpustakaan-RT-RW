package com.example.katalogku

import android.graphics.BitmapFactory
import android.net.Uri // Import Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.io.File // Masih diperlukan jika ada path absolut

class DBAdapterDaftarBuku(private val listData: ArrayList<DBModel>):RecyclerView.Adapter<DBAdapterDaftarBuku.CardViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    interface OnItemClickCallback {
        fun onItemClicked(data: DBModel)
        fun onDetailsClicked(data: DBModel) // Ini untuk tombol "Edit Buku"
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    // Fungsi baru untuk memperbarui data adapter
    fun updateData(newList: List<DBModel>) {
        listData.clear() // Hapus data lama
        listData.addAll(newList) // Tambahkan data baru
        notifyDataSetChanged() // Beri tahu RecyclerView bahwa data telah berubah
    }

    inner class CardViewHolder(itemV:View):RecyclerView.ViewHolder(itemV) {
        var Img: ImageView = itemV.findViewById(R.id.imgCover)
        var Judul: TextView = itemV.findViewById(R.id.txtJudul)
        var Penulis: TextView = itemV.findViewById(R.id.txtPenulis)
        var cvData: CardView = itemV.findViewById(R.id.cvData)
        var btnEditBuku: Button = itemV.findViewById(R.id.btnEditBuku)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DBAdapterDaftarBuku.CardViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_buku, parent,false)
        return CardViewHolder(view)
    }
    override fun onBindViewHolder(holder: DBAdapterDaftarBuku.CardViewHolder, position: Int) {
        val data = listData[position]

        val fotoUriString = data.foto // Ambil string URI dari DBModel
        if (!fotoUriString.isNullOrEmpty()) {
            try {
                val imageUri = Uri.parse(fotoUriString)
                // Coba memuat gambar sebagai URI. Ini lebih universal untuk content:// URI.
                holder.Img.setImageURI(imageUri)
                // Jika tidak berhasil memuat sebagai URI (misal: itu adalah path file lama), coba sebagai file
                if (holder.Img.drawable == null) { // Jika setImageURI tidak memuat gambar, coba sebagai file
                    val file = File(fotoUriString)
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        holder.Img.setImageBitmap(bitmap)
                    } else {
                        holder.Img.setImageResource(R.drawable.empty) // Fallback jika file tidak ada
                    }
                }
            } catch (e: Exception) {
                // Jika parsing URI gagal atau ada masalah lain saat memuat URI, coba sebagai file
                e.printStackTrace()
                val file = File(fotoUriString)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    holder.Img.setImageBitmap(bitmap)
                } else {
                    holder.Img.setImageResource(R.drawable.empty) // Fallback jika file tidak ada
                }
            }
        } else {
            // Jika fotoPath kosong atau null
            holder.Img.setImageResource(R.drawable.empty) // Pastikan drawable ini ada
        }

        holder.Judul.text = data.namebuku
        holder.Penulis.text = data.penulis
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listData[holder.adapterPosition])
        }
        holder.btnEditBuku.setOnClickListener { // Pastikan ID ini benar di item_buku.xml
            onItemClickCallback.onDetailsClicked(listData[holder.adapterPosition])
        }
    }
    override fun getItemCount(): Int {
        return listData.size
    }
}