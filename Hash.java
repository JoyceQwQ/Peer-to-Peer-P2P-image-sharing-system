/**
 * The Hashing interface,
 * used to perform hashing (MD5, SHA1, SHA256 etc…)
 * Implement a hash function for the program to use
 * allowing the system to change the hashing algorithm 
 * without modifying the program logic
 * @author hp1
 */
public interface Hash {
    /**
     * The hash function for the program to use,
     * i.e. hash the user input password when adding a user, for the 
     *        authentication and when the user change or reset the password
     * @param Password the password input
     * @return The string of the hashed password
     */
    public String Hashing(String Password);
}
