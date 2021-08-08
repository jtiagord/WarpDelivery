import firebase from 'firebase'

const firebaseConfig = {
    apiKey: "AIzaSyB-SqCc9xJfbbnR85poSo3yak6VyH069tI",
    authDomain: "warpdelivery-f2221.firebaseapp.com",
    projectId: "warpdelivery-f2221",
    storageBucket: "warpdelivery-f2221.appspot.com",
    messagingSenderId: "977998463697",
    appId: "1:977998463697:web:a3507793b3740ca01cbbf0",
    measurementId: "G-J1HX3827G5"
};
const firebaseApp=firebase.initializeApp(firebaseConfig);
const db=firebase.firestore();

export default db