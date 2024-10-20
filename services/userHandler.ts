import { getAuth, createUserWithEmailAndPassword, signInWithEmailAndPassword } from "@react-native-firebase/auth";
import { getFirestore, doc, setDoc, getDoc, GeoPoint } from "@react-native-firebase/firestore";

export const registerUser = async (
    email: string,
    password: string,
    displayName: string,
    location: { latitude: number, longitude: number },
    phoneNumber: string,
    profilePicture: string
) => {
    const auth = getAuth();
    const db = getFirestore();

    try {
        const userCredentials = await createUserWithEmailAndPassword(auth, email, password);
        const user = userCredentials.user;

        await setDoc(doc(db, "users", user.uid), {
            displayName: displayName,
            email: user.email,
            location: new GeoPoint(location.latitude, location.longitude),
            phoneNumber: phoneNumber,
            profilePicture: profilePicture,
        });

        console.log('User registered and additional info saved to Firestore.');
    } catch (error: any) {
        console.error('Error during registration:', error.message);
    }
}

export const loginUser = async (email: string, password: string) => {
    const auth = getAuth();

    try {
        const userCredentials = await signInWithEmailAndPassword(auth, email, password);
        console.log('User logged in successfully:', userCredentials.user);
    } catch (error: any) {
        console.error('Error during login:', error.message);
    }
}

export const fetchUser = async (uID: string) => {
    const db = getFirestore();

    try {
        const user = await getDoc(doc(db, "users", uID));

        if (user.exists) {
            console.log('User data retrieved from Firestore:', user.data());
            return user.data();
        } else {
            console.error('No user data found for UID:', uID);
            return null;
        }
    } catch (error: any) {
        console.error('Error fetching user data:', error.message);
        throw new Error(error.message);
    }
}