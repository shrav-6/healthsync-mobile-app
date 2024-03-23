package com.mobile.healthsync.model

import java.io.Serializable

data class Ratings(var doctor_id : Int = -1,var stars : Int = 0, var comment: String = "" ) : Serializable {
}