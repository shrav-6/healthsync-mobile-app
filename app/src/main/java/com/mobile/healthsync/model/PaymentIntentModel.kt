package com.mobile.healthsync.model

/**
 * Data class representing a PaymentIntentModel.
 * @property id The ID of the payment intent.
 * @property client_secret The client secret associated with the payment intent.
 */
data class PaymentIntentModel(

    val id: String,
    val client_secret: String
)
