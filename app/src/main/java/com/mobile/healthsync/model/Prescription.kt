package com.mobile.healthsync.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

/**
 * Data class representing a Prescription.
 * @property appointmentId The ID of the appointment associated with the prescription.
 * @property prescriptionId The ID of the prescription.
 * @property medicines A map of medicines prescribed, where the key is the medicine name and the value is a Medicine object.
 */
data class Prescription(
    @PropertyName("appointment_id")
    @get:PropertyName("appointment_id")
    @set:PropertyName("appointment_id")
    var appointmentId: Int = 0,

    @PropertyName("prescription_id")
    @get:PropertyName("prescription_id")
    @set:PropertyName("prescription_id")
    var prescriptionId: Int = 0,

    @PropertyName("medicines")
    @get:PropertyName("medicines")
    @set:PropertyName("medicines")
    var medicines: HashMap<String, Medicine>? = hashMapOf(),
) : Serializable
{
    /**
     * Data class representing a Medicine.
     * @property name The name of the medicine.
     * @property dosage The dosage of the medicine.
     * @property numberOfDays The number of days the medicine should be taken.
     * @property schedule The schedule for taking the medicine.
     */
    data class Medicine(
        @PropertyName("name")
        @set:PropertyName("name")
        @get:PropertyName("name")
        var name: String = "",

        @PropertyName("dosage")
        @set:PropertyName("dosage")
        @get:PropertyName("dosage")
        var dosage: String = "",

        @PropertyName("number_of_days")
        @set:PropertyName("number_of_days")
        @get:PropertyName("number_of_days")
        var numberOfDays: Int = 0,

        @PropertyName("schedule")
        @set:PropertyName("schedule")
        @get:PropertyName("schedule")
        var schedule: DaySchedule = DaySchedule(),
    ) : Serializable {
        /**
         * Data class representing the schedule for taking a medicine in a day.
         * @property morning The schedule for taking the medicine in the morning.
         * @property afternoon The schedule for taking the medicine in the afternoon.
         * @property night The schedule for taking the medicine at night.
         */
        data class DaySchedule(
            @PropertyName("morning")
            @set:PropertyName("morning")
            @get:PropertyName("morning")
            var morning: Schedule = Schedule(),

            @PropertyName("afternoon")
            @set:PropertyName("afternoon")
            @get:PropertyName("afternoon")
            var afternoon: Schedule = Schedule(),

            @PropertyName("night")
            @set:PropertyName("night")
            @get:PropertyName("night")
            var night: Schedule = Schedule(),
        ) : Serializable {

            /**
             * Data class representing the schedule for taking a medicine.
             * @property doctorSaid Indicates whether the doctor recommended taking the medicine.
             * @property patientTook Indicates whether the patient took the medicine.
             */
            data class Schedule(
                @PropertyName("doctor_said")
                @set:PropertyName("doctor_said")
                @get:PropertyName("doctor_said")
                var doctorSaid: Boolean = false,

                @PropertyName("patient_took")
                @set:PropertyName("patient_took")
                @get:PropertyName("patient_took")
                var patientTook: Boolean = false,
            )
        }
    }
}