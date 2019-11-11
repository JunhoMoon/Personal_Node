package com.hims.personal_node.Activitys.Detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hims.personal_node.HIMSDB
import com.hims.personal_node.R

class HealthDetailViewPage : AppCompatActivity() {
    private var himsdb: HIMSDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_detail_view_page)

        var node_kn:String
        var target:String
        if (intent.hasExtra("node_kn") && intent.hasExtra("target")) {
            target = intent.getStringExtra("target")
            node_kn = intent.getStringExtra("node_kn")

            himsdb = HIMSDB.getInstance(this, node_kn)
        }


    }
}
