package ch.epfl.polycrowd;


import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static com.google.android.gms.tasks.Tasks.await;

public class FirebaseInterface {

    private FirebaseAuth cachedAuth;
    private DatabaseReference cachedDbRef;

    /***
     * Returns the firebase authentication instance
     * Implemented using a cached instance to avoid repetitive map lookups issued by FirebaseAuth.getInstance()
     * @param refresh option to force the refresh of the cached instance, true to force
     * @return FirebaseAuth authentication instance
     */
    private FirebaseAuth getAuthInstance(boolean refresh){
        if (this.cachedAuth == null || refresh){
            this.cachedAuth = FirebaseAuth.getInstance();
        }
        return this.cachedAuth;
    }

    private DatabaseReference getDbRef(boolean refresh){
        if (this.cachedDbRef == null || refresh) {
            this.cachedDbRef = FirebaseDatabase.getInstance().getReference();
        }
        return this.cachedDbRef;
    }

    /***
     * Utility function to check arguments' integrity
     * @param args
     */
    private void checkArgs(String... args){
        for (String arg : args){
            if (arg == null) throw new IllegalArgumentException("Firebase query cannot be null");
            if (arg.length() == 0) throw new IllegalArgumentException("Firebase query cannot be empty");
        }
    }

    /***
     * Synchronous implementation of Firebase's email sign-up procedure
     * @param email the email to provide
     * @param password the plain-text password (hashing is dealt with by Firebase
     * @return AuthResult the result object from the sign-up. Is null if the sign-up does not complete
     */
    public AuthResult emailSignUp(String email, String password){
        checkArgs(email,password);
        try {
            return await(getAuthInstance(false).createUserWithEmailAndPassword(email,password));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * Synchronous implementation of Firebase's email sign-in procedure
     * @param email the email to provide
     * @param password the plain-text password (hashing is dealt with by Firebase
     * @return AuthResult the result object from the sign-up. Is null if the sign-up does not complete
     */
    public  AuthResult emailSignIn(String email, String password){
        checkArgs(email,password);
        try {
            return await( getAuthInstance(false).signInWithEmailAndPassword(email, password) );
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * Synchronously adds a value to the real time Firebase database
     * @param table the table to insert the value into
     * @param jsonData the stringified json data
     * @return true if the write succeeded, false if not
     */
    public boolean databaseAddEntry(String table, String jsonData){
        checkArgs(table, jsonData);
        try {
            await(getDbRef(false).child(table).setValue(jsonData));
            return true;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
    


}
