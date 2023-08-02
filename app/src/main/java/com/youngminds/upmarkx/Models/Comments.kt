package com.youngminds.upmarkx.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comments(val commentid:String?=null,
                    val comment:String?=null,
                    val userid:String?=null,
                    val questionid:String?=null,
                    val answerid:String?=null,
                    val questiontext:String?=null,
                    val answertext:String?=null,
                    val time:Long?=null): Parcelable
