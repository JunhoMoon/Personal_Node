package com.hims.personal_node.Activitys.Adaoter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hims.personal_node.DataMamager.Repository.Health.PrimaryPhysicianRepository
import com.hims.personal_node.HIMSDB
import com.hims.personal_node.R
import com.hims.personal_node.Model.Health.PrimaryPhysician


class HealthAuthorityDetailRecycleAdapter (var mContext: Context, var node_kn:String):RecyclerView.Adapter<HealthAuthorityDetailRecycleAdapter.Holder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    private var primaryPhysicians = emptyList<PrimaryPhysician>()
    private lateinit var primaryPhysicianRepository: PrimaryPhysicianRepository

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val health_authority_primaryphysician_id:TextView = itemView.findViewById(R.id.health_authority_primaryphysician_id)
        val health_authority_primaryphysician_name:TextView = itemView.findViewById(R.id.health_authority_primaryphysician_name)
        val health_authority_primaryphysician_regdate:TextView = itemView.findViewById(R.id.health_authority_primaryphysician_regdate)
        val health_authrity_detail_primaryphysician_write_bt: Switch = itemView.findViewById(R.id.health_authrity_detail_primaryphysician_write_bt)
        val health_authrity_detail_primaryphysician_read_bt:Switch = itemView.findViewById(R.id.health_authrity_detail_primaryphysician_read_bt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = inflater.inflate(R.layout.recycler_health_authority_detail, parent, false)
        val himsdb = HIMSDB.getInstance(mContext, node_kn)!!
        primaryPhysicianRepository = PrimaryPhysicianRepository(himsdb.primaryPhysicianDAO())
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = primaryPhysicians[position]
        holder.health_authority_primaryphysician_id.setText(current.primaryPhysician_id)
        holder.health_authority_primaryphysician_name.setText(current.primaryPhysician_name)
        holder.health_authority_primaryphysician_regdate.setText(current.reg_date)
        holder.health_authrity_detail_primaryphysician_write_bt.isChecked = current.record_auth == 1
        holder.health_authrity_detail_primaryphysician_read_bt.isChecked = current.read_auth == 1

        holder.health_authrity_detail_primaryphysician_write_bt.setOnClickListener {
            var primaryPhysician = current
            if (holder.health_authrity_detail_primaryphysician_write_bt.isChecked){
                primaryPhysician.record_auth = 1
            }else{
                primaryPhysician.record_auth = 0
            }
            val addRunnable = Runnable {
                primaryPhysicianRepository.update(primaryPhysician)
            }
            val addThread = Thread(addRunnable)
            addThread.start()
        }

        holder.health_authrity_detail_primaryphysician_read_bt.setOnClickListener {
            var primaryPhysician = current
            if (holder.health_authrity_detail_primaryphysician_read_bt.isChecked){
                primaryPhysician.read_auth = 1
            }else{
                primaryPhysician.read_auth = 0
            }
            val addRunnable = Runnable {
                primaryPhysicianRepository.update(primaryPhysician)
            }
            val addThread = Thread(addRunnable)
            addThread.start()
        }
    }

    internal fun setPrimaryPhysician(primaryPhysicians:List<PrimaryPhysician>){
        this.primaryPhysicians = primaryPhysicians
        notifyDataSetChanged()
    }

    override fun getItemCount() = primaryPhysicians.size

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