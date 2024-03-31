package com.mobile.healthsync.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewsTest {
    @Test
    fun testReviewsProperties() {
        // Create a Reviews instance
        val reviews = Reviews(
            comment = "Great experience!",
            doctorId = 123,
            patientId = 456,
            stars = 4.5,
            patientName = "John Doe"
        )

        // Assert Reviews properties
        assertEquals("Great experience!", reviews.comment)
        assertEquals(123, reviews.doctorId)
        assertEquals(456, reviews.patientId)
        assertEquals(4.5, reviews.stars, 0.001)
        assertEquals("John Doe", reviews.patientName)
    }
}
