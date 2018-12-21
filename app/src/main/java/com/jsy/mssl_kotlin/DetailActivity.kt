package org.jsy.mssl_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.browse

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        this.goBtn.isEnabled=false
        var tId1 = intent.getStringExtra("tId")
        var tName1 = intent.getStringExtra("tName")
        var tPhone1 = intent.getStringExtra("tPhone")
        var tPrimeNum1 = intent.getStringExtra("tPrimeNum")
        var tPrivateNum1 = intent.getStringExtra("tPrivateNum")
        var songSite1 = intent.getStringExtra("songSite")

        tId.text=tId1
        tName.text=tName1
        tPhone.text=tPhone1
        tPrimeNum.text=tPrimeNum1
        tPrivateNum.text=tPrivateNum1

        if(songSite1!=null) {
            this.goBtn.isEnabled=true
            this.goBtn.setOnClickListener {
                browse(songSite1)
            }
        }
        homeBtn.setOnClickListener {
            finish()
        }
    }
}