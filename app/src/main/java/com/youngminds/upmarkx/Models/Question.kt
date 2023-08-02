package com.youngminds.upmarkx.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Question(val questionid:String?=null,
                    val useruid:String?=null,
                    val question:String?=null,
                    val date:Long?=null,
                    val subject:String?=null,
                    val keywords:List<String>?=null):  Parcelable
