package com.mobile.healthsync.model

import java.io.Serializable

/**
 * Data class representing ratings given by a patient to a doctor.
 * @property doctor_id The ID of the doctor being rated.
 * @property stars The rating given in stars (0 to 5).
 * @property comment The comment or feedback provided by the patient.
 */
data class Ratings(var doctor_id : Int = -1,var stars : Int = 0, var comment: String = "" ) : Serializable {
}