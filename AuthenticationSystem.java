import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * The AuthenticationSystem class,
 * used to create an AuthenticationSystem object
 * and implement all related methods and the interface Hash,
 * e.g. add user record,
 * 		authentication with error handlings and error count
 *    & Modify user record
 * @author hp1
 */
public class AuthenticationSystem implements Hash {
	
	private ArrayList<User> Users;
	private BufferedReader input;

	/**
	 * The main method to execute the system (program)
	 * Display the following menu when it is executed
	 * Create a new AuthenticationSystem object
	 * Get the input of the command and
	 * execute the corresponding method
	 * Terminate the system when "0" is inputted
	 * @param args the parameter of the main method
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to the COMP2396 Authentication system!");
		System.out.println("1. Authenticate user");
	    System.out.println("2. Add user record");
	    System.out.println("3. Edit user record");
	    System.out.println("What would you like to perform?");
	    System.out.println("Please enter your command (1-3, or 0 to terminate the system):");
	    AuthenticationSystem AS = new AuthenticationSystem();
	 	// Use readLine() wherever reading user input is needed to read one line of input
	 	String command = AS. readInput();
	    while (!command.contentEquals("0")) {
	    	String Username = AS.getInputUsername();
	    	if (command.contentEquals("2")) {
	    		AS.addUser(Username, command);
	    	}
	    	else {
	    		if (command.contentEquals("1")) {
	    		    AS.Authentication(Username, command);
	    		}
	    		else if ((command.contentEquals("3"))) {
	    			AS.Modify(Username, command);
	    		}
	    	}
	    	System.out.println("Please enter your command (1-3, or 0 to terminate the system):");
	    	command = AS.readInput();
	    }
	    System.exit(0);
	}
	
	/**
	 * Create an AuthenticationSystem object
	 * with Users, the new ArrayList of User and 
	 * input, the BufferedReader Stream of this system
	 * Declare the BufferedReader once
	 */
	public AuthenticationSystem() {
		Users = new ArrayList<User>();
		// Declare BufferedReader to read from System.in
		input = new BufferedReader(new InputStreamReader(System.in));
	}
	
	/**
	 * The hash function for the program to use
	 * Hash the user input password when adding a user, for the 
     * authentication and when the user change or reset the password
	 * Implements and Overrides the interface Hash.Hashing
	 * @param Password the password input
     * @return The string of the hashed password
	 */
	public String Hashing(String Password) {
		try { 
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
            md.update(Password.getBytes());
    		byte[] hash = md.digest();
    		BigInteger no = new BigInteger(1, hash);
    		String HashedPassword = no.toString(16);
    		while (HashedPassword.length() < 32) { 
                HashedPassword = "0" + HashedPassword; 
            }
            return HashedPassword; 
        } 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        }
	}
	
	/**
	 * Check if the user input password has at least 
	 * 1 small letter, 1 capital letter, 1 digit
	 * @param Password the user input password
	 * @return true if it has at least 1 small letter, 
	 *         1 capital letter, 1 digit;
	 *         false if not
	 */
	public boolean checkPassword (String Password) {
	    char c;
	    boolean SmallLetter = false;
	    boolean CapitalLetter = false;
	    boolean digit = false;
	    for(int i=0;i < Password.length();i++) {
	        c = Password.charAt(i);
	        if( Character.isLowerCase(c)) {
	            SmallLetter = true;
	        }
	        else if (Character.isUpperCase(c)) {
	            CapitalLetter = true;
	        } 
	        else if (Character.isDigit(c)) {
	            digit = true;
	        }
	        if(SmallLetter && CapitalLetter && digit)
	            return true;
	    }
	    return false;
	}
	
	/**
	 * Get the input of the Username
	 * @return the user input Username
	 */
	public String getInputUsername() {
		System.out.println("Please enter your username:");
		String Username = readInput();
		return Username;
	}
	
	/**
	 * Get the index of the user the system is looking for
	 * in the ArrayList Users
	 * @param Username the user name of the user that
	 *        this system needs to look for in the ArrayList Users
	 * @return the index of the Usernmae (User) in the ArrayList Users;
	 * 		   or -1 if the user does not exist in the ArrayList Users
	 */
	public int getUserIndex(String Username) {
		int Index = -1;
		for (int i=0; i < Users.size(); i++) {
			User u = Users.get(i);
			if (Username.equals(u.getUsername())) {
				Index = i;
			}
		}
	    return Index;
	}
	
	/**
	 * Print "Please enter your password:" for command 2 (addUser) and command 1 (Authentication)
	 * Otherwise, print "Please enter your new password:" for command 3 (Modify)
	 * @param command the command the user entered
	 */
	public void DifferentPrintln(String command) {
		if (command.contentEquals("2") || command.contentEquals("1")) {
			System.out.println("Please enter your password:");
		}
		else {
			System.out.println("Please enter your new password:");
		}
	}
	
	/**
	 * Get the user input of the password
	 * Check if the password fulfils the requirement,
	 * i.e. The password should contain at least 6 characters, 
	 * with at least 1 small letter, 1 capital letter, 1 digit.
	 * If not, ask the user to enter the password again
	 * until it fulfils the requirement.
	 * @param command the command the user entered
	 * @return the user input password
	 */
	public String getInputPassword(String command) {
		DifferentPrintln(command);
		String Password = readInput();
		if (command.contentEquals("2") || command.contentEquals("3")) {
		    while (Password.length() < 6 || !checkPassword(Password)) {
			    System.out.println("Your password has to fulfil: at least "
					    + "6 characters, 1 small letter, 1 capital letter, 1 digit!");
			    DifferentPrintln(command);
			    Password = readInput();
		    }
		}
		return Password;
	}
	
	/**
	 * Add a user record in the authentication system
	 * If the username is used by another, 
	 * “The username is already taken!” would be printed out 
	 * right after the user inputted the desired username 
	 * and exit the add user process.
	 * Use the hashing function to hash the user input password.
	 * The system will store the hashed password instead of the plain text password
	 * The program will check if the password fulfils the following requirements:
	 * 	   The password should contain at least 6 characters, 
	 *     with at least 1 small letter, 1 capital letter, 1 digit.
	 *     The program will check if the two passwords entered are identical. 
	 *     If not identical, “Passwords do not match, no user added!” would be printed out
	 *     and exit the add user process.
	 * @param Username the user input username got from the main method
	 * @param command the command the user entered
	 */
	public void addUser (String Username, String command) {
		if (getUserIndex(Username) != -1) {
			System.out.println("The username is already taken!");
			return;
		}
		String Password = getInputPassword(command);
		String HashedPassword = Hashing(Password);
		System.out.println("Please re-enter your password:");
		String RePassword = readInput();
		if (!Password.equals(RePassword)){
			System.out.println("Passwords do not match, no user added!");
			return;
		}
		System.out.println("Please enter your full name:");
		String Full_Name = readInput();
		System.out.println("Please enter your email address:");
		String EmailAddress = readInput();
		System.out.println("Please enter your phone number:");
		String PhoneNumber = readInput();
		User u = new User(Username, HashedPassword, Full_Name, EmailAddress, PhoneNumber);
		Users.add(u);
		System.out.println("Record added successfully!");
		try {
			File file = new File ("User.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file, true);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			String Content = "username:"+Username+
					";hashPassword:"+HashedPassword;
			printWriter.println(Content);
		    printWriter.close();
		}
		catch (IOException e) {
		      System.out.println("User.txt cannot be generated.");
		      e.printStackTrace();
		    }
	}
	
	/**
	 * Authentication with error handlings and error count,
	 * i.e. Login process for the user
	 * The account will be validated
	 * if the user can provide a correct username and password.
	 * If the user tries to login with a username that does not exist, 
	 * “User not found!” will be printed out.
	 * If the user enters a wrong password, 
	 * “Login failed!” will be printed out.
	 * If the failed count is less than 3 and the user can login successfully,
	 * the failed count will reset to 0.
	 * If the failed count is greater than or equal to 3, 
	 * the user account is locked, 
	 * and the user will not be allowed to login again. 
	 * In this case, no matter the user has inputted a correct password not, 
	 * “Login failed! Your account has been locked!” will be printed out.
	 * i.e. the user cannot login anymore
	 * @param Username the user input username got from the main method
	 * @param command the command the user entered
	 * @return true if the login succeeded
	 * 		   false if not
	 */
	public boolean Authentication(String Username, String command) {
		String Password = getInputPassword(command);
		String HashedPassword =  Hashing(Password);
		boolean SuccessfulLogin = false;
		if (getUserIndex(Username) != -1) {
			User u = Users.get(getUserIndex(Username));
			if (u.LockAccount()) {
			    System.out.println("Login failed! Your account has been locked!");
			}
			else {
			    if (HashedPassword.equals(u.getHashedPassword())) {
				    u.ResetFailedCount();
					System.out.println("Login success! Hello " + Username + "!");
					SuccessfulLogin = true;
				}
				else {
					u.IncreaseFailedCount();
					System.out.println("Login failed!");
				}
			}
		}
		else {
			System.out.println("User not found!");
		}
		return SuccessfulLogin;
	}
	
	/**
	 * Modify the user record, i.e. password, full name and email address
	 * can be changed.
	 * the user has to provide the username and password 
	 * before he can edit the record.
	 * Therefore, this method will handle incorrect username or password input 
	 * in the same way as stated in part 3,
	 * i.e. the user will not be prompted to edit the user record.
	 * When the user successfully login,
	 * the user can change the password, full name and email address.
	 * This method will check if the two new passwords entered are identical
	 * If not identical, 
	 * “New passwords do not match, user record not edited!” will be printed out
	 * and exit the edit user process. 
	 * @param Username the user input username got from the main method
	 * @param command the command the user entered
	 */
	public void Modify(String Username, String command) {
		boolean SuccessfulLogin = Authentication(Username, "1");
		if (!SuccessfulLogin) {
			return;
		}
		String Password = getInputPassword(command);
		String HashedPassword =  Hashing(Password);
		System.out.println("Please re-enter your new password:");
		String RePassword = readInput();
		if (!Password.equals(RePassword)){
			System.out.println("New passwords do not match, user record not edited!");
			return;
		}
		System.out.println("Please enter your new full name:");
		String Full_Name = readInput();
		System.out.println("Please enter your new email address:");
		String EmailAddress = readInput();
		User u = Users.get(getUserIndex(Username));
		u.UpdatePasswordFullNameAndEmail(HashedPassword, Full_Name, EmailAddress);
		System.out.println("Record update successfully!");
	}
	
	/**
	 * Read one line of user input from the BufferedReader input
	 * of this Authentication System
	 * @return the user input of this line in String
	 */
	public String readInput() {
		String inputLine;
		try {
	 	    inputLine = input.readLine();
	 	    return inputLine;
	 	} catch (IOException e) {
	 		System.out.print("Input Error.");
	 	}
		return new String();
	}
	
}
