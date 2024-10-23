import { getFirestore, collection, doc, setDoc, getDoc, getDocs, query, where, deleteDoc, Timestamp, GeoPoint } from '@react-native-firebase/firestore';

// In-memory cache for listings
let listingsCache: { [key: string]: any } = {};

// Create or Update a Listing
export const createOrUpdateListing = async (
    listingId: string,
    sellerID: string,
    title: string,
    description: string,
    category: string,
    price: number,
    location: { latitude: number, longitude: number },
    score: number,
    imageUrls: string[],
    isAvailable: boolean
) => {
    const db = getFirestore();

    try {
        // Add or update the listing document in Firestore
        await setDoc(doc(db, 'listings', listingId), {
            sellerID: doc(db, "users", sellerID),
            title,
            description,
            category,
            price,
            location: new GeoPoint(location.latitude, location.longitude),
            imageUrls,
            isAvailable,
            score,
            createdAt: Timestamp.fromDate(new Date()),
        });

        // Cache the updated listing
        listingsCache[listingId] = {
            sellerID,
            title,
            description,
            category,
            price,
            location: new GeoPoint(location.latitude, location.longitude),
            imageUrls,
            isAvailable,
            score,
            createdAt: Timestamp.fromDate(new Date()),
        };

        console.log('Listing created/updated successfully:', listingId);
    } catch (error: any) {
        console.error('Error creating/updating listing:', error.message);
    }
};

// Fetch a single listing by ID
export const fetchListingById = async (listingId: string) => {
    const db = getFirestore();

    // Check cache first
    if (listingsCache[listingId]) {
        console.log('Using cached listing for:', listingId);
        return listingsCache[listingId];
    }

    try {
        // Fetch from Firestore if not cached
        const listingDoc = await getDoc(doc(db, 'listings', listingId));

        if (listingDoc.exists) {
            const listingData = listingDoc.data();

            // Cache the listing
            listingsCache[listingId] = listingData;

            console.log('Fetched listing from Firestore:', listingData);
            return listingData;
        } else {
            console.error('No listing found for ID:', listingId);
            return null;
        }
    } catch (error: any) {
        console.error('Error fetching listing:', error.message);
    }
};

// Fetch multiple available listings (with optional filters)
export const fetchListings = async (userId?: string, category?: string) => {
    const db = getFirestore();
    let listingsQuery;

    try {
        // Start with the base collection query
        listingsQuery = collection(db, 'listings');

        // Create an array to hold where clauses
        const filters: any[] = [];

        // Add filters based on parameters
        if (userId) {
            filters.push(where('sellerID', '==', doc(db, 'users', userId)));
        }
        if (category) {
            filters.push(where('category', '==', category));
        }
        filters.push(where('isAvailable', '==', true));
        

        const querySnapshot = await getDocs(listingsQuery);
        const listings = querySnapshot.docs.map(doc => doc.data());

        // Cache all listings
        listings.forEach(listing => {
            listingsCache[listing.listingId] = listing;
        });

        console.log('Fetched listings:', listings);
        return listings;
    } catch (error: any) {
        console.error('Error fetching listings:', error.message);
        throw new Error(error.message);
    }
};

// Delete a listing by ID
export const deleteListing = async (listingId: string) => {
    const db = getFirestore();

    try {
        // Delete the listing document from Firestore
        await deleteDoc(doc(db, 'listings', listingId));

        // Remove from cache
        delete listingsCache[listingId];

        console.log('Listing deleted successfully:', listingId);
    } catch (error: any) {
        console.error('Error deleting listing:', error.message);
    }
};

// Fetch listings with batch (for optimization)
export const fetchListingsBatch = async (listingIds: string[]) => {
    const db = getFirestore();

    // Filter uncached listings
    const uncachedListingIds = listingIds.filter(listingId => !listingsCache[listingId]);

    if (uncachedListingIds.length > 0) {
        try {
            const listingsQuery = query(collection(db, 'listings'), where('__name__', 'in', uncachedListingIds));
            const querySnapshot = await getDocs(listingsQuery);
            const listings = querySnapshot.docs.map(doc => doc.data());

            // Cache fetched listings
            listings.forEach(listing => {
                listingsCache[listing.listingId] = listing;
            });

            return listings;
        } catch (error: any) {
            console.error('Error fetching listings in batch:', error.message);
        }
    }

    // Return listings from cache
    return listingIds.map(listingId => listingsCache[listingId]);
};
