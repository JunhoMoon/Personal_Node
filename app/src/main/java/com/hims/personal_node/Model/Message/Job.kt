package com.hims.personal_node.Model.Message

data class Job(var job_key:String, var parent_key:String?, var job_name:String){
    @Override
    override fun toString(): String {
        return job_name
    }
}