package com.youngminds.upmarkx.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pdfs(val documentid:String?=null,
                val name:String?=null,
                val description:String?=null,
                val pdflink:String?=null): Parcelable
