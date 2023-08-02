package com.youngminds.upmarkx.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Answer(val answerid:String?=null,
                  val questionid:String?=null,
                  val answer:String?=null,
                  val userid:String?=null,
                  val askeduserid:String?=null,
                  val answerimage:String?=null,
                  val question:String?=null,
                  val edited:String?=null,
                  val date:Long?=null): Parcelable