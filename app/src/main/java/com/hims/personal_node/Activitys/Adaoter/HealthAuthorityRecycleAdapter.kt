package com.hims.personal_node.Activitys.Adaoter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hims.personal_node.Activitys.Detail.HealthAuthrityDetail
import com.hims.personal_node.R
import com.hims.personal_node.Model.Health.HealthAuthority


class HealthAuthorityRecycleAdapter (var mContext: Context, var node_kn:String):RecyclerView.Adapter<HealthAuthorityRecycleAdapter.Holder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    private var healthAuthoritys = emptyList<HealthAuthority>()

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val authority_nodekn:TextView = itemView.findViewById(R.id.health_authority_nodekn)
        val health_authority_name:TextView = itemView.findViewById(R.id.health_authority_name)
        val health_authority_rag_date:TextView = itemView.findViewById(R.id.health_authority_rag_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = inflater.inflate(R.layout.recycler_health_authority, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = healthAuthoritys[position]
        holder.authority_nodekn.setText(current.node_kn)
        holder.health_authority_name.setText(current.node_name)
        holder.health_authority_rag_date.setText(current.reg_date)
        holder.itemView.setOnClickListener{
            //작성 요망
            var intent = Intent(mContext, HealthAuthrityDetail::class.java)
            intent.putExtra("node_kn", node_kn)
            intent.putExtra("target", current.node_kn)
            mContext.startActivity(intent)
        }
    }

    internal fun setHealth(healths:List<HealthAuthority>){
        this.healthAuthoritys = healths
        notifyDataSetChanged()
    }

    override fun getItemCount() = healthAuthoritys.size

//    fun show() {
//        val builder = AlertDialog.Builder(this.healths)
//        builder.setTitle("AlertDialog Title")
//        builder.setMessage("AlertDialog Content")
//        builder.setPositiveButton("OK"
//        ) { dialog, which ->
//        }
//        builder.setNegativeButton("Cancle"
//        ) { dialog, which ->
//        }
//        builder.show()
//    }
}