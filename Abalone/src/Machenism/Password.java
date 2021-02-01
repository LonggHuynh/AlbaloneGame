package Machenism;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/**
 * This class is used for hashing password
 */

public class Password {

	/**
	 * Encrypts password
	 * 
	 * @param pass password to be hashed
	 * @requires pass != null
	 * @return string representation as encrypted
	 */
	public static String getHashPassword(String pass) {
		MessageDigest md;
		String hashedPass = "";
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(pass.getBytes());
			hashedPass = Hex.encodeHexString(md.digest());

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hashedPass;
	}
}
