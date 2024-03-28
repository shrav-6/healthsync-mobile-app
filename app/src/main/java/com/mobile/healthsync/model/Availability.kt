package com.mobile.healthsync.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Availability(
    @get:PropertyName("is_available")
    @set:PropertyName("is_available")
    var isAvailable: Boolean = true,

    @get:PropertyName("slots")
    @set:PropertyName("slots")
    var slots: List<Slot>? = null
) : Serializable
