package com.youngminds.upmarkx.Models

data class News(val newsid:String?=null,
                val imageuri:String?=null,
                val heading:String?=null,
                val body:String?=null,
                val newslink:String?=null,
                val time:Long?=null,
                val keywords:List<String>?=null)
