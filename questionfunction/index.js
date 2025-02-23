const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendQuestionNotification = functions.database
    .ref('askmate/questions/{pushKey}')
    .onCreate((snapshot, context) => {
        const newValue = snapshot.val(); // Get the data at the pushKey

        // Ensure that the necessary fields are present
        if (!newValue || !newValue.question) {
            console.log('No data or title field missing');
            return null;
        }

        // Create the notification payload
        const payload = {
            notification: {
                // title: 'New News!',
                // body: newValue.content
            },
            data: {
                // imageUrl: newValue.image, // Ensure 'image' field contains the URL to the image
                // content: newValue.content,
                title:"🧠 Do you know its answer? 🤔",
                question: newValue.question
            }
        };
        const options = {
            priority: 'high',
            timeToLive: 0 // Message will not expire
        };

        // Send the notification to the 'news' topic
        return admin.messaging().sendToTopic('questions', payload, options)
            .then(response => {
                console.log('Successfully sent message:', response);
                return null;
            })
            .catch(error => {
                console.error('Error sending message:', error);
                return null;
            });
    });
