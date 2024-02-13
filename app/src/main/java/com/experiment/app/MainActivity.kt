package com.experiment.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.experiment.app.utils.PermissionUtils


class MainActivity : AppCompatActivity() {
    //private var binding:Ma

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.start_play_btn)
        val fragment = AlphaPlayerFragment()

        btn.setOnClickListener {
            if(!PermissionUtils.verifyStoragePermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                return@setOnClickListener
            }
            val frTrans = supportFragmentManager.beginTransaction()
            if (!fragment.isAdded) {
                frTrans.add(R.id.main_frame, fragment, fragment.tag)
            } else {
                frTrans.show(fragment)
            }
            frTrans.commit()
        }
        val vapFragment = TencentVapFragment("http://freedom.patathai.com/vapx/vap_demo.mp4")
        val detach = findViewById<Button>(R.id.detach_btn)
        detach.setOnClickListener {
            val frTrans = supportFragmentManager.beginTransaction()
            frTrans.remove(fragment)
            frTrans.remove(vapFragment)
            frTrans.commit()
        }

        val newBtn = findViewById<Button>(R.id.start_acty_btn)
        newBtn.setOnClickListener {
            val intent = Intent(this, GiftPlayActivity::class.java)
            startActivity(intent)
        }

        val vapBtn = findViewById<Button>(R.id.vap_btn)

        vapBtn.setOnClickListener {
            val frTrans = supportFragmentManager.beginTransaction()
            if (!vapFragment.isAdded) {
                frTrans.add(R.id.main_frame, vapFragment, vapFragment.tag)
            } else {
                frTrans.show(vapFragment)
            }
            frTrans.commit()
        }

    }


}