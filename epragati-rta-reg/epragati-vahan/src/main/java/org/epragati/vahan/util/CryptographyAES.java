package org.epragati.vahan.util;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.sun.crypto.provider.SunJCE;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class CryptographyAES {
    
    private static final Logger logger = Logger.getLogger(CryptographyAES.class);
    private String secretKey;
    
    public CryptographyAES(String secretKey) {
        this.secretKey = secretKey;
    }
//
    public String decryptFile(String s) {
        String decdata = null;
        byte key[] = (byte[]) null;
        key = secretKey.getBytes();
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
            byte keyBytes[] = new byte[16];
            int len = key.length;
            if (len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(key, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(2, keySpec, ivSpec);
            BASE64Decoder decoder = new BASE64Decoder();
            byte results[] = decoder.decodeBuffer(s);
            byte cipherText[] = cipher.doFinal(results);
            decdata = new String(cipherText, "UTF-8");
        } catch (IllegalBlockSizeException e) {
            logger.debug(e.getMessage());
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return decdata;
    }

    public String encryptFile(String s) {
        byte key[] = (byte[]) null;
        key = secretKey.getBytes();
        String encData = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
            byte keyBytes[] = new byte[16];
            int len = key.length;
            if (len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(key, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(1, keySpec, ivSpec);
            byte results[] = cipher.doFinal(s.getBytes("UTF-8"));
            BASE64Encoder encoder = new BASE64Encoder();
            encData = encoder.encode(results);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return encData;
    }

    static {
        Security.addProvider(new SunJCE());
        Security.insertProviderAt(new SunJCE(), 1);
    }
    
    public String decryptFile(String s, String secretKey) {
		String decdata = null;
		byte key[] = (byte[]) null;
		key = secretKey.getBytes();
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			byte keyBytes[] = new byte[16];
			int len = key.length;
			if (len > keyBytes.length)
				len = keyBytes.length;
			System.arraycopy(key, 0, keyBytes, 0, len);
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
			cipher.init(2, keySpec, ivSpec);
			BASE64Decoder decoder = new BASE64Decoder();
			byte results[] = decoder.decodeBuffer(s);
			byte cipherText[] = cipher.doFinal(results);
			decdata = new String(cipherText, "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return decdata;
	}

	public String encryptFile(String s, String secretKey) {
		byte key[] = (byte[]) null;
		key = secretKey.getBytes();
		String encData = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			byte keyBytes[] = new byte[16];
			int len = key.length;
			if (len > keyBytes.length)
				len = keyBytes.length;
			System.arraycopy(key, 0, keyBytes, 0, len);
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
			cipher.init(1, keySpec, ivSpec);
			byte results[] = cipher.doFinal(s.getBytes("UTF-8"));
			BASE64Encoder encoder = new BASE64Encoder();
			encData = encoder.encode(results);
		} catch (Exception ex) {
		}
		return encData;
	}
    }

