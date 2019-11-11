package com.hims.personal_node.Model.Message

data class Education(var edu_key:Int, var edu_name:String){
    @Override
    override fun toString(): String {
        return edu_name
    }
}