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

class DBAdapterPopuler(private val listData: ArrayList<DBModel>):RecyclerView.Adapter<DBAdapterPopuler.CardViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    interface OnItemClickCallback {
        fun onItemClicked(data: DBModel)
        fun onDetailsClicked(data: DBModel)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    inner class CardViewHolder(itemV:View):RecyclerView.ViewHolder(itemV) {
        var Img: ImageView = itemV.findViewById(R.id.imgCover)
        var Judul: TextView = itemV.findViewById(R.id.txtJudul)
        var Penulis: TextView = itemV.findViewById(R.id.txtPenulis)
        var cvData: CardView = itemV.findViewById(R.id.cvData)
        var btnDetails: Button = itemV.findViewById(R.id.btnDetails)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DBAdapterPopuler.CardViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.populer_buku, parent,false)
        return CardViewHolder(view)
    }
    override fun onBindViewHolder(holder: DBAdapterPopuler.CardViewHolder, position: Int) {
        val data = listData[position]

        val fotoPath = data.foto // Ambil jalur file dari DBModel
        if (!fotoPath.isNullOrEmpty()) {
            val file = File(fotoPath)
            if (file.exists()) {
                // Jika file ada, muat bitmap dari jalur file
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.Img.setImageBitmap(bitmap)
            } else {
                // Jika file tidak ditemukan, fallback ke gambar default (misal: empty.png)
                holder.Img.setImageResource(R.drawable.empty) // Pastikan drawable ini ada
                // Anda juga bisa menambahkan log atau toast untuk debugging jika perlu
                // Log.e("DBAdapterPopuler", "File tidak ditemukan: $fotoPath")
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
        holder.btnDetails.setOnClickListener {
            onItemClickCallback.onDetailsClicked(listData[holder.adapterPosition])
        }
    }
    override fun getItemCount(): Int {
        return listData.size
    }
}