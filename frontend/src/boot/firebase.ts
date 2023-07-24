import { initializeApp } from 'firebase/app';
import { getAuth, onAuthStateChanged, User } from 'firebase/auth';

// Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const config = process.env.VUE_APP_FIREBASE_PUBLIC_CONFIG;
if (config === undefined) {
  throw Error('Missing firebase configuration');
}

const firebaseConfig = JSON.parse(config);

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
  return user !== null; // && user.emailVerified;
};

export const signOut = async () => {
  await init();
  return auth.signOut();
};
