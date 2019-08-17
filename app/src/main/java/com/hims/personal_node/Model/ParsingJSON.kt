package com.hims.personal_node.Model

import com.google.gson.Gson

object ParsingJSON{
    val gson = Gson()

    internal fun modelToJson(model:Any?):String{
        return gson.toJson(model)
    }

//    internal fun jsonToModel(message:String, model: Any):Any?{
//        return if(model is MutableList<*>){
//            var modeltype = model.
//            val type =  object : TypeToken<MutableList<model::class.java>>(){}
//            gson.fromJson(message, model::class.java).asList()
//        }else{
//            gson.fromJson(message, model::class.java)
//        }
//    }

    internal fun jsonToModel(message:String, model: Any):Any?{
        return gson.fromJson(message, model::class.java)
    }

    internal fun createMessage(requestType:String):String{
        return "{\"requestType\" : \"$requestType\"}"
    }

    internal fun createMessage(requestType:String, receiverPublicKey:String, message:String?):String{
        return "{\"requestType\" : \"$requestType\", \"receiverPublicKey\" : \"$receiverPublicKey\", \"message\" : \"$message\"}"
    }

    internal fun createResult(result:String, receiverPublicKey:String, message:String?):String{
        return "{\"result\" : \"$result\", \"receiverPublicKey\" : \"$receiverPublicKey\", \"message\" : \"$message\"}"
    }

    internal fun createResult(result:String, message:String?):String{
        return "{\"result\" : \"$result\", \"message\" : \"$message\"}"
    }
}