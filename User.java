/**
 * The User class,
 * to store the following private instant variables
 * for a user record
 * @author hp1
 */
public class User {
	
	private String Username;
	private String HashedPassword;
	private String Full_Name;
	private String EmailAddress;
	private String PhoneNumber;
	private int FailedLoginCount;
	private boolean AccountLocked;
	
	/**
	 * Create a User object
	 * FailedLoginCount: the failed login count of the user, 
	 * 		  			 i.e. how many times the user fails to login
	 * AccountLocked: the account locked, i.e. whether the account is locked
	 * @param Username the username of the user
	 * @param HashedPassword the hashed user input password
	 * @param Full_Name the full name of the user
	 * @param EmailAddress the email address of the user
	 * @param PhoneNumber the phone number of the user
	 */
	public User (String Username, String HashedPassword,
	             String Full_Name, String EmailAddress,
	             String PhoneNumber) {
		this.Username = Username;
		this.HashedPassword = HashedPassword;
		this.Full_Name = Full_Name;
		this.EmailAddress = EmailAddress;
		this.PhoneNumber = PhoneNumber;
		this.FailedLoginCount = 0;
		this.AccountLocked = false;
	}
	
	/**
	 * Get the username of this User object
	 * @return the User name in string
	 */
	public String getUsername() {
		return this.Username;
	}
	
	/**
	 * Get the hashed password of this User object
	 * @return the hashed password in string
	 */
	public String getHashedPassword() {
		return this.HashedPassword;
	}
	
	/**
	 * Increase the failed login count by 1,
	 * i.e. when the user failed to login once,
	 * 		the failed count will increase by 1
	 */
	public void IncreaseFailedCount() {
		this.FailedLoginCount++;
	}
	
	/**
	 * Lock this account
	 * If the failed count is greater than or equal to 3,
	 * the user account is locked,
	 * and the user will not be allowed to login again.
	 * @return the AccountLocked of this User object as Boolean
	 * 		   true if the account is locked;
	 * 		   false if not, as the initial AccountLocked is false
	 * 		   and it is not locked
	 */
	public boolean LockAccount() {
		if (this.FailedLoginCount >= 3) {
		    this.AccountLocked = true;
		}
		return this.AccountLocked;
	}
	
	/**
	 * Reset the failed count to 0,
	 * If the failed login count is less than 3 
	 * and the user can login successfully.
	 */
	public void ResetFailedCount() {
		if (this.FailedLoginCount < 3) {
			this.FailedLoginCount = 0;
		}
	}
	
	/**
	 * Change the password, full name, and email address
	 * of this User object
	 * @param HashedPassword the new hashed password
	 * @param Full_Name the new full name
	 * @param EmailAddress the new email address
	 */
	public void UpdatePasswordFullNameAndEmail
	(String HashedPassword, String Full_Name, String EmailAddress) {
		this.HashedPassword = HashedPassword;
		this.Full_Name = Full_Name;
		this.EmailAddress = EmailAddress;
	}
}
