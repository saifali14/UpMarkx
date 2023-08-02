package com.youngminds.upmarkx.Models

data class DeliverChat(val messageid:String?=null,
                       val message:String?=null,
                       val inid:String?=null,
                       val outid:String?=null,
                       val time:String?=null,
                       val date:String?=null,
                       val timestamp:Long?=null,
                       val seen:String?=null,
                       val type:String?=null,
                       val sentby:String?=null,
                       val like:Boolean?=null,
                       val deleted:Boolean?=null,
                       val shareuser:User?=null,
)