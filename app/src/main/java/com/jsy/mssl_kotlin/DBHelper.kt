package org.jsy.mssl_kotlin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Created by softcampus on 2018-03-06.
 */
class DBHelper(context : Context) : SQLiteOpenHelper(context, "msslKotlin.db", null, 1){

    override fun onCreate(p0: SQLiteDatabase?) {
        Log.d("msg", "on create")

        var sql = "create table msslTable (" +
                "songId integer primary key autoincrement, " +
                "songSubject text not null, " +
                "songNumber text not null, " +
                "songKind text not null, " +
                "songEtc text not null, " +
                "songSite text not null " +
                ")"
        p0?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("msg", "oldVersion : ${oldVersion}, newVersion : ${newVersion}")
    }
}