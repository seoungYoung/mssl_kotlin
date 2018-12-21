package org.jsy.mssl_kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        var tName1 = intent.getStringExtra("tName")
        var tPhone1 = intent.getStringExtra("tPhone")
        var tPrimeNum1 = intent.getStringExtra("tPrimeNum")
        var tPrivateNum1 = intent.getStringExtra("tPrivateNum")
        var songSite1 = intent.getStringExtra("songSite")

//
//        tIdE.setText(tIdData1)
        tNameE.setText(tName1)
        tPhoneE.setText(tPhone1)
        tPrimeNumE.setText(tPrimeNum1)
        tPrivateNumE.setText(tPrivateNum1)
        songSite.setText(songSite1)

        homeBtn.setOnClickListener {
            finish()
        }
        delBtn.setOnClickListener {

        }
        editBtn.setOnClickListener {

        }
    }
}
