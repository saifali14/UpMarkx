package com.youngminds.upmarkx.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
 data class User(val firstname:String?=null,
                val lastname:String?=null,
                val email:String?=null,
                val standard:String?=null,
                val imageuri:String?=null,
                val uid:String? = null,
                val joined:Long?=null,
                val verified:String?=null,
                val keywords: List<String>?=null,
                val bestsub: String?=null,
                val timestamp:Long?=null,
                val bio:String?=null,
                val dateofbirth:String?=null,
                val status:String?=null


) : Parcelable
