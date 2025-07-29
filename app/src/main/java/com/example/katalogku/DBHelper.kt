package com.example.katalogku

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

public class DBHelper (context: Context, factory:SQLiteDatabase.CursorFactory?)
    : SQLiteOpenHelper(
    context, DATABASE_NAME,
    factory, DATABASE_VERSION
){
    companion object{
        private const val DATABASE_NAME = "dbData.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "tbBuku"
        public const val ID_COL = "id"
        //public const val NAME_COL = "name"
        //public const val AGE_COL = "age"

        public const val NAMEBUKU = "namebuku"
        public const val KATEGORIBUKU = "kategoribuku"
        public const val TAHUNTERBIT = "tahunterbit"
        public const val PENULIS = "penulis"
        public const val DESKRIPSI = "deskripsi"
        public const val FOTO = "foto"

        public var IdRow : Long = 0
        //public var getAge = "getage"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $NAMEBUKU TEXT, $KATEGORIBUKU TEXT, $TAHUNTERBIT TEXT, $PENULIS TEXT, $DESKRIPSI TEXT, $FOTO TEXT)")
        //val query = ("CREATE TABLE $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $NAME_COL TEXT, $AGE_COL TEXT)")
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    //fun AddData(name: String?, age: String?): Boolean {
    fun AddData(namebuku: String?, kategoribuku: String?, tahunterbit: String?, penulis: String?, deskripsi: String?, foto: String?): Boolean {
        val values = ContentValues()
        //values.put(NAME_COL, name)
        //values.put(AGE_COL, age)
        values.put(NAMEBUKU, namebuku)
        values.put(KATEGORIBUKU, kategoribuku)
        values.put(TAHUNTERBIT, tahunterbit)
        values.put(PENULIS, penulis)
        values.put(DESKRIPSI, deskripsi)
        values.put(FOTO, foto)
        val db = this.writableDatabase
        //var cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE name = '$name'", null)
        var cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE namebuku = '$namebuku'", null)
        if(cursor.moveToFirst()){
            return false
        } else{
            IdRow = db.insert(TABLE_NAME, null, values)
            return true
        }
    }

    fun getAllDataBukuBaru(): ArrayList<DBModelBukuBaru> {
        val list = ArrayList<DBModelBukuBaru>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val fotoPath = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
                list.add(DBModelBukuBaru(fotoPath))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }

    //fun DeleteData(name: String){
    fun DeleteData(namebuku: String){
        val db: SQLiteDatabase = this.writableDatabase
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE namebuku = '$namebuku'")
        db.close()
    }

    @SuppressLint("Range")
    //fun SearchData(name:String): ArrayList<DBModel>{
    fun SearchData(namebuku:String): ArrayList<DBModel>{
        val db: SQLiteDatabase = this.readableDatabase
        val arrayList = arrayListOf<DBModel>()
        //val res: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $NAME_COL = '$name'" , null)
        val res: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $NAMEBUKU = '$namebuku'" , null)
        if (res.moveToFirst()){
            do {
                val namebukus = res.getString(res.getColumnIndex(NAMEBUKU))
                val kategoribuku = res.getString(res.getColumnIndex(KATEGORIBUKU))
                val tahunterbit = res.getString(res.getColumnIndex(TAHUNTERBIT))
                val penulis = res.getString(res.getColumnIndex(PENULIS))
                val deskripsi = res.getString(res.getColumnIndex(DESKRIPSI))
                val foto = res.getString(res.getColumnIndex(FOTO))

                arrayList.add(DBModel(namebukus, kategoribuku, tahunterbit, penulis, deskripsi, foto))
            }
            while(res.moveToNext())
        }
        return arrayList
    }

    @SuppressLint("Range")
    fun getAllData(): ArrayList<DBModel>{
        val db: SQLiteDatabase = this.readableDatabase
        //val arrayList = ArrayList<String>()
        val arrayList = arrayListOf<DBModel>()
        //var Nama: String
        var namebuku: String
        var kategoribuku: String
        var tahunterbit: String
        var penulis: String
        var deskripsi: String
        var foto: String
        val res: Cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
        if (res.moveToFirst()){
            do {
                //Nama = res.getString(res.getColumnIndex("name"))
                //getAge = res.getString(res.getColumnIndex("age"))
                //arrayList.add(DBModel(Nama, getAge))
                namebuku = res.getString(res.getColumnIndex("namebuku"))
                kategoribuku = res.getString(res.getColumnIndex("kategoribuku"))
                tahunterbit = res.getString(res.getColumnIndex("tahunterbit"))
                penulis = res.getString(res.getColumnIndex("penulis"))
                deskripsi = res.getString(res.getColumnIndex("deskripsi"))
                foto = res.getString(res.getColumnIndex("foto"))
                arrayList.add(DBModel(namebuku, kategoribuku, tahunterbit, penulis, deskripsi, foto))
            }
            while(res.moveToNext())
        }
        return arrayList
    }

    // Fungsi baru: Mendapatkan semua kategori unik
    @SuppressLint("Range")
    fun getAllCategories(): ArrayList<String> {
        val categories = ArrayList<String>()
        val db = this.readableDatabase
        // Menggunakan DISTINCT untuk mendapatkan nilai unik
        val cursor: Cursor = db.rawQuery("SELECT DISTINCT $KATEGORIBUKU FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndex(KATEGORIBUKU)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return categories
    }

    // Fungsi baru: Mendapatkan semua tahun terbit unik, diurutkan
    @SuppressLint("Range")
    fun getAllYears(): ArrayList<String> {
        val years = ArrayList<String>()
        val db = this.readableDatabase
        // Menggunakan DISTINCT dan ORDER BY untuk mendapatkan nilai unik yang terurut
        val cursor: Cursor = db.rawQuery("SELECT DISTINCT $TAHUNTERBIT FROM $TABLE_NAME ORDER BY $TAHUNTERBIT ASC", null)
        if (cursor.moveToFirst()) {
            do {
                years.add(cursor.getString(cursor.getColumnIndex(TAHUNTERBIT)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return years
    }

    // Fungsi baru: Mendapatkan data buku berdasarkan filter
    @SuppressLint("Range")
    fun getFilteredData(category: String?, year: String?, searchKeyword: String?): ArrayList<DBModel> {
        val db: SQLiteDatabase = this.readableDatabase
        val filteredList = arrayListOf<DBModel>()
        var query = "SELECT * FROM $TABLE_NAME WHERE 1" // WHERE 1 selalu true sebagai dasar

        val args = mutableListOf<String>()

        category?.let {
            if (it != "Semua Kategori") { // Asumsi "Semua Kategori" adalah opsi default
                query += " AND $KATEGORIBUKU = ?"
                args.add(it)
            }
        }
        year?.let {
            if (it != "Semua Tahun") { // Asumsi "Semua Tahun" adalah opsi default
                query += " AND $TAHUNTERBIT = ?"
                args.add(it)
            }
        }
        searchKeyword?.let {
            if (it.isNotBlank()) {
                query += " AND ($NAMEBUKU LIKE ? OR $PENULIS LIKE ? OR $DESKRIPSI LIKE ?)"
                args.add("%$it%")
                args.add("%$it%")
                args.add("%$it%")
            }
        }

        val res: Cursor = db.rawQuery(query, args.toTypedArray())

        if (res.moveToFirst()){
            do {
                val namebuku = res.getString(res.getColumnIndex(NAMEBUKU))
                val kategoribuku = res.getString(res.getColumnIndex(KATEGORIBUKU))
                val tahunterbit = res.getString(res.getColumnIndex(TAHUNTERBIT))
                val penulis = res.getString(res.getColumnIndex(PENULIS))
                val deskripsi = res.getString(res.getColumnIndex(DESKRIPSI))
                val foto = res.getString(res.getColumnIndex(FOTO))
                filteredList.add(DBModel(namebuku, kategoribuku, tahunterbit, penulis, deskripsi, foto))
            }
            while(res.moveToNext())
        }
        res.close()
        return filteredList
    }

    //fun UpdateData (name: String, age: String) {
    fun UpdateData(
        oldName: String,
        newName: String,
        kategoribuku: String,
        tahunterbit: String,
        penulis: String,
        deskripsi: String,
        updatedFotoPath: String
    ) {
        val db: SQLiteDatabase = this.writableDatabase

        val sql = """
        UPDATE $TABLE_NAME 
        SET 
            $NAMEBUKU = ?, 
            $KATEGORIBUKU = ?, 
            $TAHUNTERBIT = ?, 
            $PENULIS = ?, 
            $DESKRIPSI = ?, 
            $FOTO = ? 
        WHERE $NAMEBUKU = ?
    """

        val statement = db.compileStatement(sql)
        statement.bindString(1, newName)
        statement.bindString(2, kategoribuku)
        statement.bindString(3, tahunterbit)
        statement.bindString(4, penulis)
        statement.bindString(5, deskripsi)
        statement.bindString(6, updatedFotoPath)
        statement.bindString(7, oldName)

        statement.execute()
        db.close()
    }




    @SuppressLint("Range")
    //fun SearchDataByName(name: String): ArrayList<DBModel>{
    fun SearchDataByName(namebuku: String): ArrayList<DBModel>{
        val db: SQLiteDatabase = this.readableDatabase
        val arrayList = arrayListOf<DBModel>()
        //var Nama: String
        //var Usia: String
        var namebukus: String
        var kategoribuku: String
        var tahunterbit: String
        var penulis: String
        var deskripsi: String
        var foto: String
        //val res: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $NAME_COL like '%$name%'" , null)
        val res: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $NAMEBUKU like '%$namebuku%'" , null)
        if (res.moveToFirst()){
            do {
                //Nama = res.getString(res.getColumnIndex("name"))
                //Usia = res.getString(res.getColumnIndex("age"))
                //arrayList.add(DBModel(Nama, Usia))
                namebukus = res.getString(res.getColumnIndex("namebuku"))
                kategoribuku = res.getString(res.getColumnIndex("kategoribuku"))
                tahunterbit = res.getString(res.getColumnIndex("tahunterbit"))
                penulis = res.getString(res.getColumnIndex("penulis"))
                deskripsi = res.getString(res.getColumnIndex("deskripsi"))
                foto = res.getString(res.getColumnIndex("foto"))
                arrayList.add(DBModel(namebukus, kategoribuku, tahunterbit, penulis, deskripsi, foto))
            }
            while(res.moveToNext())
        }
        return arrayList
    }

}