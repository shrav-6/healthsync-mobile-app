const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

const {messaging} = require("firebase-admin");
const db = admin.database();

exports.sendAppointmentNotifications = functions.pubsub
    .schedule("every 30 minutes")
    .timeZone("your-timezone") // Replace with your actual timezone
    .onRun(async (context) => {
      try {
      // Get current time and time 1 hour from now
        const now = new Date();
        const nextHour = new Date(now.getTime() + 60 * 60 * 1000);

        // Fetch patients with upcoming appointments within the next hour
        const appointmentsSnapshot = await db
            .ref("patients")
            .orderByChild("appointmentTime")
            .startAt(now)
            .endAt(nextHour)
            .once("value");

        // Iterate through the appointments and send notifications
        appointmentsSnapshot.forEach((appointment) => {
          const patient = appointment.val();
          const deviceToken = patient.deviceToken;

          // Check if the patient has a device token
          if (deviceToken) {
            const notification = {
              notification: {
                title: "Upcoming Appointment",
                body: "Your appointment is in 1 hour!",
              },
              token: deviceToken,
            };

            // Send FCM notification
            messaging()
                .send(notification)
                .then((response) => {
                  console.log("Notification sent successfully:", response);
                })
                .catch((error) => {
                  console.error("Error sending notification:", error);
                });
          }
        });

        return null;
      } catch (error) {
        console.error("Error in sendAppointmentNotifications:", error);
        return null;
      }
    });
