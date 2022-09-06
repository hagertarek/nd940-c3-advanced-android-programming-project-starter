package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

const val STATUS = "STATUS"
const val FILE = "FILE"

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        tvFileName.text = intent.getStringExtra(FILE)
        tvStatus.text = intent.getStringExtra(STATUS)
        okButton.setOnClickListener {
            finish()
        }
    }

}
