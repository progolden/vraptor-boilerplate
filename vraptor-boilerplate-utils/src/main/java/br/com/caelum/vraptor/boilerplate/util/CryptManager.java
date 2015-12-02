/*

The MIT License (MIT)

Copyright (c) 2015 ProGolden Technology Solutions

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package br.com.caelum.vraptor.boilerplate.util;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

/**
 * Utility class to generate encrypted content.
 * @author Renato R. R. de Oliveira
 *
 */
public class CryptManager {

	private static final Logger LOG = Logger.getLogger(CryptManager.class);
	
	private static String SALT = "ZZy!2mn'/1dS4p@";
	public static void updateSalt(String newSalt) {
		if (GeneralUtils.isEmpty(newSalt) || (newSalt.length() < 6)) {
			throw new IllegalArgumentException("The provided salt must have at least 6 characters length.");
		}
		SALT = newSalt + "'" + StringUtils.reverse(newSalt);
	}
	
	/** Cipher key. */
	private final byte[] key;
	/** Cipher secret key spec. */
	private final SecretKeySpec keySpec;
	/** Cipher to encrypt */
	private final Cipher encrypter;
	/** Cipher to decrypt */
	private final Cipher decrypter;
	/** Base64 Encoder/Decoder */
	private final Base64 base64;
	
	/**
	 * Instantiates a crypt manager that uses a fallback encryption key.
	 * @deprecated PROVIDE YOUR OWN ENCRYPTION KEY!
	 */
	@Deprecated
	public CryptManager() {
		this("QT15k78s-/*'G6m9");
	}
	
	/**
	 * Instantiates a crypt manager that uses the given encryption key.
	 * @param key 16-characters length key/password to Blowfish encryption.
	 */
	public CryptManager(String key) {
		LOG.debug("Instantiating crypt manager.");
		this.key = key.getBytes();
		try {
			this.encrypter = Cipher.getInstance("Blowfish");
			this.decrypter = Cipher.getInstance("Blowfish");
			this.keySpec = new SecretKeySpec(this.key, "Blowfish");
			this.encrypter.init(Cipher.ENCRYPT_MODE, this.keySpec);
			this.decrypter.init(Cipher.DECRYPT_MODE, this.keySpec);
		} catch (GeneralSecurityException ex) {
			LOG.error("Erro ao inicializar o sistema de criptografia.", ex);
			throw new RuntimeException("Erro ao inicializar o sistema de criptografia.", ex);
		}
		this.base64 = new Base64(true);
	}
	
	public String encrypt(String plain) {
		LOG.debug("Starting encryption...");
		try {
			byte[] encrypted = this.encrypter.doFinal(plain.getBytes());
			return this.base64.encodeToString(encrypted).trim();
		} catch (GeneralSecurityException ex) {
			LOG.error("Erro ao criptografar: "+plain, ex);
			throw new RuntimeException("Erro ao criptografar.", ex);
		}
	}
	

	public String decrypt(String encrypted) {
		LOG.debug("Starting encryption...");
		try {
			byte[] plain = this.base64.decode(encrypted);
			return new String(this.decrypter.doFinal(plain));
		} catch (GeneralSecurityException ex) {
			LOG.error("Erro ao decriptografar: "+encrypted, ex);
			throw new RuntimeException("Erro ao decriptografar.", ex);
		}
	}
	
	public static String toHexString(byte[] bytes) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
			int parteBaixa = bytes[i] & 0xf;
			if (parteAlta == 0)
				s.append('0');
			s.append(Integer.toHexString(parteAlta | parteBaixa));
		}
		return s.toString();
	}
	
	public static String passwordHash(String plainPwd) {
		return digest(SALT.replaceAll("'", plainPwd));
	}
	
	public static String digest(String raw) {
		return DigestUtils.sha256Hex(raw);
	}
	
	public static String token(String value) {
		return digest(value+String.valueOf(System.nanoTime()));
	}

}
