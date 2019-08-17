package com.hims.personal_node.Model.Message

data class Admin(var admin_no:Int, var admin_name:String, var country_no:Int){
    @Override
    override fun toString(): String {
        return admin_name
    }
}