import { initializeApp } from 'firebase/app';
import { getAuth, onAuthStateChanged, User } from 'firebase/auth';

// Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: 'AIzaSyCyN-PMo0yV45thK2PijPWdTmDKf2Q7HvQ',
  authDomain: 'budget-c1ab6.firebaseapp.com',
  projectId: 'budget-c1ab6',
  storageBucket: 'budget-c1ab6.appspot.com',
  messagingSenderId: '847714116015',
  appId: '1:847714116015:web:39e5a2682c89bc031e4f5a',
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Firebase Authentication and get a reference to the service
const auth = getAuth(app);
export default auth;

let initialized = false;

onAuthStateChanged(auth, () => {
  initialized = true;
});

const init = async (): Promise<boolean> => {
  if (initialized) {
    return true;
  }
  console.log('app to be initialized');
  return new Promise((resolve) => {
    const interval = setInterval(() => {
      if (initialized) {
        console.log('initialized, continue');
        clearInterval(interval);
        resolve(true);
      }
    }, 5);
  });
};

export const getUser = async (): Promise<User | null> => {
  await init();
  return auth.currentUser;
};

export const isAuthenticated = async () => {
  const user = await getUser();
  return user !== null && user.emailVerified;
};

export const signOut = async () => {
  await init();
  return auth.signOut();
};
