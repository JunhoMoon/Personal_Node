package com.hims.personal_node.Model.Message

data class Gender(var gender_key:Int, var parent_key:Int?, var gender_name:String){
    @Override
    override fun toString(): String {
        return gender_name
    }
}