package com.example.carsharingapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object UsersDb {

    const val TABLE_NAME = "users"
    const val COLUMN_NAME_EMAIL = "email"
    const val COLUMN_NAME_FIRST_NAME = "first_name"
    const val COLUMN_NAME_LAST_NAME = "last_name"
    const val COLUMN_NAME_PHONE = "phone_nummer"
    const val COLUMN_NAME_TOKEN = "token"
    const val COLUMN_NAME_ACTIVE = "active"

    private const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${UsersDb.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${UsersDb.COLUMN_NAME_EMAIL} TEXT, " +
                "${UsersDb.COLUMN_NAME_FIRST_NAME} TEXT, " +
                "${UsersDb.COLUMN_NAME_LAST_NAME} TEXT, " +
                "${UsersDb.COLUMN_NAME_PHONE} TEXT, " +
                "${UsersDb.COLUMN_NAME_TOKEN} TEXT, " +
                "${UsersDb.COLUMN_NAME_ACTIVE} INTEGER)"

    fun addUser(context:Context,user:User): Long? {
        val dbHelper = UserReaderDbHelper(context)
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME_EMAIL,user.email)
            put(COLUMN_NAME_FIRST_NAME,user.first_name)
            put(COLUMN_NAME_LAST_NAME,user.last_name)
            put(COLUMN_NAME_PHONE,user.phone)
            put(COLUMN_NAME_TOKEN,user.token)
            put(COLUMN_NAME_ACTIVE,user.active)
        }
        val newRowID = db?.insert(TABLE_NAME,null,values)
        return newRowID
    }

    fun logoutUser(context:Context):Int?{
        val dbHelper = UserReaderDbHelper(context)
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME_ACTIVE,0)
        }
        val response = db.update(TABLE_NAME,values,"$COLUMN_NAME_EMAIL = ?", arrayOf(ServiceBuilder.currentUser?.email))
        return response
    }

    fun loginUser(context: Context,user:User): Int? {
        val dbHelper = UserReaderDbHelper(context)
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_EMAIL = '${user.email}'",null)
        var response:Int? = null
        if(cursor.count > 0){
            val values = ContentValues().apply {
                put(COLUMN_NAME_ACTIVE,1)
            }
            response = db.update(TABLE_NAME,values,"$COLUMN_NAME_EMAIL = ?", arrayOf(user.email))

        }else{
            response = addUser(context,user)?.toInt()
        }
        return response
    }

    @SuppressLint("Range")
    fun getActiveUser(context: Context):User?{
        val dbHelper = UserReaderDbHelper(context)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_ACTIVE = 1",null)
        var user:User? = null

        if(cursor.count > 0){
            cursor.moveToFirst()
            with(cursor){
                user = User(
                    getString(getColumnIndex(COLUMN_NAME_EMAIL)),
                    getString(getColumnIndex(COLUMN_NAME_FIRST_NAME)),
                    getString(getColumnIndex(COLUMN_NAME_LAST_NAME)),
                    getString(getColumnIndex(COLUMN_NAME_PHONE)),
                    getString(getColumnIndex(COLUMN_NAME_TOKEN)),
                    getInt(getColumnIndex(COLUMN_NAME_ACTIVE))
                )
            }

        }
        return user

    }

    class UserReaderDbHelper(context:Context):SQLiteOpenHelper(context, DATABASE_NAME,null,
        DATABASE_VERSION){

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
            TODO("Not yet implemented")
        }
        companion object{
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "carsharing"
        }
    }
}