package com.youngminds.upmarkx.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OutUser(val firstname:String?=null,
                val lastname:String?=null,
                val email:String?=null,
                val standard:String?=null,
                val imageuri:String?=null,
                val uid:String? = null,
                val verified:String?=null,
                val bio:String?=null,
                val dateofbirth:String?=null,
                val status:String?=null
) : Parcelable
