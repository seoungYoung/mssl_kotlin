package org.jsy.mssl_kotlin

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_write.*
import org.jetbrains.anko.alert

class WriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        appendBtn.setOnClickListener {
            var helper:DBHelper = DBHelper(this)
            var db: SQLiteDatabase = helper.writableDatabase

            var cv1=ContentValues()
            cv1.put("songSubject",songSubjectTxt.text.toString())
            cv1.put("songNumber",songSiteTxt.text.toString())
            cv1.put("songKind",songKindTxt.text.toString())
            cv1.put("songEtc",songEtcTxt.text.toString())
            cv1.put("songSite",songSiteTxt.text.toString())
            db.insert("msslTable",null,cv1)

            /*
                var sql="insert into msslTable ('songSubject','songNumber','songKind','songEtc') values(?, ?, ?, ?)"
                var songs=songSubjectTxt.text.toString()
                var songn=songNumberTxt.text.toString()
                var songk=songKindTxt.text.toString()
                var songe=songEtcTxt.text.toString()
                var arg1 = arrayOf("${songs}","${songn}","${songk}","${songe}")
                db.execSQL(sql,arg1)
             */

            db.close()
            alert(title="확인",message = "저장했습니다.") {
                positiveButton("확인") {

                }
            }.show()
        }
        homeBtn.setOnClickListener {
            finish()
        }
        songJenre.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.radioButton ->
                        songKindTxt.setText("발라드")
                R.id.radioButton2 ->
                    songKindTxt.setText("댄스")
                R.id.radioButton3 ->
                    songKindTxt.setText("팝")
                R.id.radioButton4 ->
                    songKindTxt.setText("트로트")
            }

        }
    }
}