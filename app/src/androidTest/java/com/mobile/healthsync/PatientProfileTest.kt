
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.mobile.healthsync.R
import com.mobile.healthsync.views.patientProfile.PatientProfile
import org.junit.Rule
import org.junit.Test

class PatientProfileTest {

    @get:Rule
    val activityRule = IntentsTestRule(PatientProfile::class.java)

    @Test
    fun clickingEditButton_startsEditPatientProfileActivityWithCorrectExtra() {
        // Click the edit button
        onView(withId(R.id.editPatient)).perform(click())
        // Verify that the correct intent extra is passed to the EditPatientProfile activity
        Intents.intended(hasExtra("patientID", "00KDbESIgVNTIDzyAP04"))
    }
}
