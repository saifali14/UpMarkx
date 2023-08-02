package com.youngminds.upmarkx.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Videos( val thumbnail:String?=null ,val tittle:String?=null , val videourl:String?=null): Parcelable
