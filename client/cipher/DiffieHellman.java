package client.cipher;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellman {
	private final static int bitLength = 128;
	
	
	private BigInteger key;
	
	private BigInteger primePublic;
	private BigInteger generatorPublic;
	private BigInteger privateValue;
	private BigInteger sendingValue;
	private BigInteger receivedValue;
	

	public void generatePublicVars() {
		SecureRandom randomizer = new SecureRandom();
		primePublic = BigInteger.probablePrime(bitLength, randomizer);
		generatorPublic = BigInteger.probablePrime(bitLength, randomizer);	
	}
	
	public void setPublicVars(BigInteger p, BigInteger g) {
		this.primePublic = p;
		this.generatorPublic= g;
	}
	
	public void setReceivedValue(BigInteger B) {
		this.receivedValue = B;
	}
	
	public void randomizePrivateValue() {
		SecureRandom randomizer = new SecureRandom();
		do {
		    privateValue = new BigInteger(primePublic.bitLength(), randomizer);
		} while (privateValue.compareTo(primePublic) < 0);
		
		sendingValue =  generatorPublic.modPow(privateValue, primePublic);
	}
	
	public void generateKey() {
			key = receivedValue.modPow(privateValue, primePublic);
		
	}
	
	public BigInteger getKey()
	{
		return key;
	}
	
	public byte[] getKeyBytes()
	{
		return key.toByteArray();
	}

	public int getKeyLenght()
	{
		return key.toByteArray().length;
	}
	
	public String getP() {
		return primePublic.toString();
	}

	public String getG() {
		return generatorPublic.toString();
	}

	public String getA() {
		return sendingValue.toString();
	}
}
