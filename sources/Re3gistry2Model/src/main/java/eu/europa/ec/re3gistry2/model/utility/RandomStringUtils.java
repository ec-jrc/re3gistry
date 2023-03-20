/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.europa.ec.re3gistry2.model.utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author oriol
 */
public class RandomStringUtils {

    /**
     * A method to generate encoded Strings
     * 
     * @param size The size
     * @param param
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public static String encodeString(int size, String param) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(param.trim().getBytes(StandardCharsets.UTF_8));
        
        return bytesToHex(encodedhash,size);
    }

    /**
     * Translates bytes into Hexadecimal
     * 
     * @param hash the hash to translate
     * @return the translated String
     */
    private static String bytesToHex(byte[] hash,int size) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        if(hexString.length() > size){
            return hexString.toString().substring(0, size-1);
        }else{
            return hexString.toString();
        }
        
    }
}
