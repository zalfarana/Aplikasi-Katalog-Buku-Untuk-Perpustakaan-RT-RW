package com.example.katalogku

import android.graphics.BitmapFactory
import android.net.Uri // Import Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class DBAdapterBukuBaru(private val listData: ArrayList<DBModelBukuBaru>):RecyclerView.Adapter<DBAdapterBukuBaru.CardViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    interface OnItemClickCallback {
        fun onItemClicked(data: DBModelBukuBaru)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    inner class CardViewHolder(itemV:View):RecyclerView.ViewHolder(itemV) {
        var Img: ImageView = itemV.findViewById(R.id.imgCoverBaru)
        // Jika Anda memutuskan untuk menggunakan CardView di baru_buku.xml, biarkan ini CardView
        // Jika Anda tetap LinearLayout, ubah tipe data ke LinearLayout
        var cvData: LinearLayout = itemV.findViewById(R.id.cvData) // Sesuaikan dengan tipe di baru_buku.xml
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DBAdapterBukuBaru.CardViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.baru_buku, parent,false)
        return CardViewHolder(view)
    }
    override fun onBindViewHolder(holder: DBAdapterBukuBaru.CardViewHolder, position: Int) {
        val data = listData[position]

        // *********** PERUBAHAN PENTING DI SINI ***********
        val fotoPath = data.foto // Ambil jalur file dari DBModelBukuBaru
        if (!fotoPath.isNullOrEmpty()) {
            val file = File(fotoPath)
            if (file.exists()) {
                // Jika file ada, muat bitmap dari jalur file
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.Img.setImageBitmap(bitmap)
            } else {
                // Jika file tidak ditemukan, fallback ke gambar default (misal: empty.png)
                holder.Img.setImageResource(R.drawable.empty) // Pastikan drawable ini ada di folder drawable Anda
                // Anda juga bisa menambahkan log atau toast untuk debugging jika perlu
                // Log.e("DBAdapterBukuBaru", "File tidak ditemukan: $fotoPath")
            }
        } else {
            // Jika fotoPath kosong atau null
            holder.Img.setImageResource(R.drawable.empty) // Pastikan drawable ini ada di folder drawable Anda
        }
        // **************************************************

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listData[holder.adapterPosition])
        }
    }
    override fun getItemCount(): Int {
        return listData.size
    }
}