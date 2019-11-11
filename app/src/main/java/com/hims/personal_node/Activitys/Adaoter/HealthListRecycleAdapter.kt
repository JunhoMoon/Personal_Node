package com.hims.personal_node.Activitys.Adaoter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hims.personal_node.Model.Health.Health
import com.hims.personal_node.R
import androidx.appcompat.app.AlertDialog
import com.hims.personal_node.Activitys.Detail.HealthDetailViewPage
import com.hims.personal_node.ChangeDevice
import com.hims.personal_node.MainActivity


class HealthListRecycleAdapter internal constructor(var mContext: Context, var node_kn:String):RecyclerView.Adapter<HealthListRecycleAdapter.Holder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    private var healths = emptyList<Health>()

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val recorder_kn:TextView = itemView.findViewById(R.id.health_list_recorder_node)
        val recorder:TextView = itemView.findViewById(R.id.health_list_recorder)
        val regDate:TextView = itemView.findViewById(R.id.health_list_regdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = inflater.inflate(R.layout.recycler_health_list, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = healths[position]
        println(current.toString())
        holder.recorder_kn.setText(current.node_kn)
        holder.regDate.setText(current.reg_date)
        holder.recorder.setText(current.physician_id)
        holder.itemView.setOnClickListener{
            //작성 요망
            var intent = Intent(mContext, HealthDetailViewPage::class.java)
            intent.putExtra("node_kn", node_kn)
            intent.putExtra("message_id", current.subject_health_no)
            mContext.startActivity(intent)
        }
    }

    internal fun setHealth(healths:List<Health>){
        this.healths = healths
        notifyDataSetChanged()
    }

    override fun getItemCount() = healths.size
}