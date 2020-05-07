package com.neptunesoftware.reuseableClasses;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class CypherCrypt {
	public static String deCypher(String encryptedText){
		String decrypted = null; 
		try{
	        String key = "NeptuneMbranch01"; // 128 bit encryption key
	        // Create key and cipher
	        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
	        Cipher cipher = Cipher.getInstance("AES");
	        // decrypt the text
	        cipher.init(Cipher.DECRYPT_MODE, aesKey);
	        byte [] decodedText = DatatypeConverter.parseBase64Binary(encryptedText);
	        decrypted = new String(cipher.doFinal(decodedText));
	    }
	    catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e)
	    {
	    	System.out.println("Error is "+e.getMessage());
	    }
		return decrypted;
	}
	
	public static String enCrypt(String originalText){
		String encodedText = null; 
		try{
			String key = "NeptuneMbranch01"; // 128 bit encryption key

            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(originalText.getBytes());
            encodedText = DatatypeConverter.printBase64Binary(encrypted);
	    }
	    catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e)
	    {
	    	System.out.println("Error is "+e.getMessage());
	    }
		return encodedText;
	}
}
