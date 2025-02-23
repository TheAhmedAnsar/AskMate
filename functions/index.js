const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// exports.sendanswers = functions.database
// .ref('users/{uid}/answers')
// .onCreate(async (snapshot, context) =>{
//     const uid = context.params.uid;
//     // const admissionId = context.params.admissionId;
//     // const questionname = snapshot.val();

//        // Fetch the user's device token (Assuming you store it in /users/{uid}/deviceToken)
//        const userSnapshot = await admin.database().ref(`users/${uid}/deviceToken`).once('value');
//        const deviceToken = userSnapshot.val();


//        if (!deviceToken) {
//         console.log('No device token found for user:', uid);
//         return null;
        
//     }

//       // Create a notification message
//       const message = {
//         notification: {
//             title: 'Wooh 🥳',
//             body: 'Your question got an answer!',
//         },
//         data: {
//             title: 'Wooh 🥳',
//             body: 'Your question got an answer!',
//         },
//         token: deviceToken,
//     };

//      // Send the notification
//      try {
//         await admin.messaging().send(message);
//         console.log('Notification sent successfully.');
//     } catch (error) {
//         console.error('Error sending notification:', error);
//     }

//     return null;
// });


exports.sendanswers = functions.database
    .ref('users/{uid}/answers/{answerId}')
    .onCreate(async (snapshot, context) => {
        const uid = context.params.uid;
        const answerId = context.params.answerId;
        const answerData = snapshot.val();

        console.log(`New answer added for user: ${uid}`);
        console.log(`Answer ID: ${answerId}`);
        console.log(`Answer data: ${JSON.stringify(answerData)}`);

        try {
            // Fetch the user's device token
            const userSnapshot = await admin.database().ref(`users/${uid}/deviceToken`).once('value');
            const deviceToken = userSnapshot.val();

            if (!deviceToken) {
                console.log('No device token found for user:', uid);
                return null;
            }

            // Create a notification message
            const message = {
                notification: {
                    // title: 'Wooh 🥳',
                    // body: 'Your question got an answer!',
                },
                data: {
                    title: 'Wooh 🥳',
                    body: 'Your question got an answer!',
                },
                token: deviceToken,
            };

            // Send the notification
            await admin.messaging().send(message);
            console.log('Notification sent successfully to device token:', deviceToken);
        } catch (error) {
            console.error('Error fetching device token or sending notification:', error);
        }

        return null;
    });



// Function to handle notifications for new questions
exports.sendQuestionNotification = functions.database
    .ref('askmate/questions/{pushKey}')
    .onCreate((snapshot, context) => {
        const newValue = snapshot.val();

        if (!newValue || !newValue.question) {
            console.log('No data or question field missing');
            return null;
        }

   // Retrieve the user ID of the creator
   const creatorId = newValue.uid; // Ensure the question data includes the userId
   const currentUserId = context.auth.uid; // Assuming you are using Firebase Authentication

   // Check if the question was created by the current user
   if (creatorId === currentUserId) {
       console.log('Question created by the current user. No notification sent.');
       return null;
   }

        const payload = {
            notification: {
                // title: "🧠 Do you know its answer? 🤔",
                // body: newValue.question
            },
            data: {
                title: "🧠 Do you know its answer? 🤔",
                question: newValue.question,
                fragment: "QnA", // Specify the fragment
            }
        };

        const options = {
            priority: 'high',
            timeToLive: 0 // 1 day in seconds
        };

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

// Function to handle notifications for news
exports.sendNewsNotification = functions.database
    .ref('/news/{pushKey}')
    .onCreate((snapshot, context) => {
        const newValue = snapshot.val();

        if (!newValue || !newValue.title) {
            console.log('No data or title field missing');
            return null;
        }

        const payload = {
            notification: {
                // title: "📢 New Update!",
                // body: newValue.title
            },
            data: {
                // title: newValue.title,
                fragment: "News", // Specify the fragment
                headline: newValue.title
            }
        };

        const options = {
            priority: 'high',
            timeToLive: 0 // 1 day in seconds
        };

        return admin.messaging().sendToTopic('news', payload, options)
            .then(response => {
                console.log('Successfully sent message:', response);
                return null;
            })
            .catch(error => {
                console.error('Error sending message:', error);
                return null;
            });
    });


// dont update it

// const functions = require('firebase-functions');
// const admin = require('firebase-admin');
// admin.initializeApp();

// exports.sendNewsNotification = functions.database
//     .ref('/news/{pushKey}')
//     .onCreate((snapshot, context) => {
//         const newValue = snapshot.val(); // Get the data at the pushKey

//         // Ensure that the necessary fields are present
//         if (!newValue || !newValue.title) {
//             console.log('No data or title field missing');
//             return null;
//         }

//         // Create the notification payload
//         const payload = {
//             notification: {
//                 // title: 'New News!',
//                 // body: newValue.content
//             },
//             data: {
//                 imageUrl: newValue.image, // Ensure 'image' field contains the URL to the image
//                 // content: newValue.content,
//                 headline: newValue.title
//             }
//         };
//         const options = {
//             priority: 'high',
//             timeToLive: 0 // Message will not expire
//         };

//         // Send the notification to the 'news' topic
//         return admin.messaging().sendToTopic('news', payload, options)
//             .then(response => {
//                 console.log('Successfully sent message:', response);
//                 return null;
//             })
//             .catch(error => {
//                 console.error('Error sending message:', error);
//                 return null;
//             });
//     });
