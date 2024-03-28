package com.mobile.healthsync.model

import java.io.Serializable

class Availability(var is_available : Boolean = true, var slots : List<Slot>? = null) : Serializable {
}