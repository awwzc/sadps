package com.sk.deploy.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

public class DesUtil {
	public static String ENCODING = "utf-8";
	public static String ALGORITHM = "DES";
	public static String KEY = "www.tianxiaxinyong.com";
	
	private Cipher encryptCipher = null;
	private Cipher decryptCipher = null;
	public DesUtil() throws Exception {
		Key key = getKey(KEY);
		encryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		
		decryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
	}
	
	private Key getKey(String str) throws Exception{
		DESKeySpec dks = new DESKeySpec(str.getBytes(ENCODING));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		return keyFactory.generateSecret(dks);
	}
	
	public byte[] encrypt(byte[] bytes) throws Exception{
		return encryptCipher.doFinal(bytes);
	}
	public byte[] decrypt(byte[] bytes) throws Exception{
		return decryptCipher.doFinal(bytes);
	}
	public String encrypt(String str) throws Exception{
		return new String(Base64.encodeBase64(encrypt(str.getBytes(ENCODING)),true),ENCODING).replaceAll("\n", "");
	}
	public String decrypt(String str) throws Exception{
		return new String(decrypt(Base64.decodeBase64(str.getBytes(ENCODING))),ENCODING);
	}
	
}
