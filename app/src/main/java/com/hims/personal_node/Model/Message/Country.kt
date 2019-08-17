package com.hims.personal_node.Model.Message

data class Country(var country_no:Int, var country_name:String){
    @Override
    override fun toString(): String {
        return country_name
    }
}