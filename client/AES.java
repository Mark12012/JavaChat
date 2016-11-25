package client;

public class AES
{

	private static int			Nb, Nk, Nr;
	private static int[][]		NumberOfRounds	= { { 10, 12, 14 }, { 12, 12, 14 }, { 14, 14, 14 } };
	// private static byte[][][] subKey;

	public static final int[]	Rcon			= { 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36,
			0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef,
			0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d,
			0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab,
			0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39,
			0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8,
			0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f,
			0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3,
			0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01,
			0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63,
			0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2,
			0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08,
			0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35,
			0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a,
			0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d };

	public static final int[]	RijndaelSBox	= { 0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67,
			0x2B, 0xFE, 0xD7, 0xAB, 0x76, 0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C,
			0xA4, 0x72, 0xC0, 0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31,
			0x15, 0x04, 0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75, 0x09,
			0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84, 0x53, 0xD1, 0x00,
			0xED, 0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF, 0xD0, 0xEF, 0xAA, 0xFB, 0x43,
			0x4D, 0x33, 0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8, 0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38,
			0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2, 0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4,
			0xA7, 0x7E, 0x3D, 0x64, 0x5D, 0x19, 0x73, 0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A, 0x90, 0x88, 0x46, 0xEE, 0xB8,
			0x14, 0xDE, 0x5E, 0x0B, 0xDB, 0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62, 0x91,
			0x95, 0xE4, 0x79, 0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E, 0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE,
			0x08, 0xBA, 0x78, 0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B, 0xBD, 0x8B, 0x8A, 0x70,
			0x3E, 0xB5, 0x66, 0x48, 0x03, 0xF6, 0x0E, 0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E, 0xE1, 0xF8, 0x98,
			0x11, 0x69, 0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55, 0x28, 0xDF, 0x8C, 0xA1, 0x89, 0x0D, 0xBF,
			0xE6, 0x42, 0x68, 0x41, 0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16 };

	/**
	 * Empty AES constructor.
	 */
	public AES()
	{
		// Nothing to initialize here.
	}

	public byte[] encrypt(String message, byte[] key)
	{
		//message = "12345678";
		byte[] keyprim = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		key = keyprim;
		Nk = key.length / 4;
		Nb = 4;
		Nr = NumberOfRounds[Nk / 2 - 2][Nb / 2 - 2];
		byte[] ms = message.getBytes();
		byte[] out;// = new byte[ms.length];
		byte[][] blocks;
		byte[][] encryptedBlocks;
		int blockLenght = Nb * 4;
		int numberOfBlocks = (int) Math.ceil((double) ms.length / blockLenght);
		//if(ms.length != 0 && numberOfBlocks == 0)
			//numberOfBlocks = 1;
		System.out.println("NRoB " + numberOfBlocks + "; MSL " + ms.length + "; BlockL " + blockLenght);
		blocks = new byte[numberOfBlocks][blockLenght];
		encryptedBlocks = new byte[numberOfBlocks][blockLenght];
		//Chopping into blocks
		for(int blockNR = 0; blockNR < numberOfBlocks; blockNR++)
			for(int i = 0; i < blockLenght; i++)
			{
				if(blockNR * blockLenght + i != ms.length - 1)
					blocks[blockNR][i] = ms[blockNR * blockLenght + i];
				else 
					break;
			}
				
		
		blocks[numberOfBlocks - 1] = addPadding(blocks[numberOfBlocks - 1], blockLenght);
		
		for(int k = 0; k < numberOfBlocks; k++)
			encryptedBlocks[k] = encryptBlock(blocks[k], key);
		
		out  = new byte[numberOfBlocks * blockLenght];
		for(int blockNR = 0; blockNR < numberOfBlocks; blockNR++)
			for(int i = 0; i < blockLenght; i++)
				out[blockNR * blockLenght + i] = encryptedBlocks[blockNR][i];
		
		// Assert.assertEquals(expectedFirstNames, firstNames);
		return out;
	}
	
	private byte[] addPadding(byte[] message, int blockLenght)
	{
		byte[] newMs = null;
		int lenghtOfMessageInBytes = message.length;
		int padding;
		if(lenghtOfMessageInBytes > 32)
		{
			System.err.println("Block is too long");
			return null;
		}
		
		if(lenghtOfMessageInBytes == blockLenght)
		{	
			//no padding
			newMs = message;
		}
		else
		{
			newMs = new byte[blockLenght];
			padding = blockLenght - lenghtOfMessageInBytes;
			for(int i = 0; i < lenghtOfMessageInBytes; i++)
			{
				newMs[i] = (byte) message[i];
			}
			for(int i = 0; i < padding; i++)
			{
				newMs[lenghtOfMessageInBytes + i] = (byte) padding;
			}
		}
		
		return newMs;
	}

	/**
	 * Encrypting block of 128, 192 or 256 bits with key that have the same
	 * possible lengths
	 */
	/**
	 * How it works: 
	 * Zero round(State,RoundKey) 
	 * 	addRoundKey(State,RoundKey)
	 * 
	 * Round(State,RoundKey) 
	 * 	byteSub(State) 
	 * 	shiftRow(State) 
	 * 	mixColumns(State)
	 * 	addRoundKey(State) 
	 * 
	 * FinalRound(State,RoundKey) 
	 * 	byteSub(State)
	 * 	shiftRow(State) 
	 * 	addRoundKey(State)
	 * 
	 * Output - State
	 */
	private static byte[] encryptBlock(byte[] input, byte[] key)
	{

		byte[] tmp = new byte[input.length];
		byte[][] state = new byte[4][Nb];
		byte[][][] rounKey = rijndaelKeySchedule(key);

		for (int i = 0; i < input.length; i++)
			state[i / 4][i % 4] = input[i % 4 * 4 + i / 4];

		state = addRoundKey(state, rounKey[0]);
		for (int round = 1; round < Nr; round++)
		{
			state = byteSub(state);
			state = shiftRow(state);
			state = mixColumns(state);
			state = addRoundKey(state, rounKey[round]);
		}
		state = byteSub(state);
		state = shiftRow(state);
		state = addRoundKey(state, rounKey[Nr]);

		for (int i = 0; i < tmp.length; i++)
			tmp[i % 4 * 4 + i / 4] = state[i / 4][i % 4];

		return tmp;
	}

	/**
	 * Methods for generating matrix of round keys
	 */
	private static byte[][][] rijndaelKeySchedule(byte[] key)
	{

		int numberOfColumns = 4;
		// Output
		byte[][][] out = new byte[Nr + 1][numberOfColumns][4];
		// Main variable for generating subKeys bytes in batches of 4
		byte[] temp = new byte[4];
		// How many bytes where already generated
		int progress = 0;
		// Key block is a number of key blocks which are 32 bits = 4 bytes
		int keyBlock = key.length / 4;
		// How many bytes we have to generate for specified key length
		int target = (Nr + 1) * numberOfColumns;

		/**
		 * Rewriting key to the beginning of subKeymatrix
		 */
		for (int i = 0; i < keyBlock; i++)
			for (int j = 0; j < 4; j++)
			{
				out[0][i][j] = key[i * 4 + j];
			}
		progress = keyBlock;
		while (progress < target)
		{
			int colId = progress % numberOfColumns;
			int roundId = (progress - colId) / 4;
			for (int k = 0; k < 4; k++)
			{
				if (colId == 0)
					temp[k] = out[roundId - 1][numberOfColumns - 1][k];
				else
					temp[k] = out[roundId][colId - 1][k];
			}
			if (progress % Nk == 0)
			{
				temp = SubWord(rotateRSK(temp));
				temp[0] = (byte) (temp[0] ^ (Rcon[progress / Nk] & 0xff));
			} else if (Nk > 6 && progress % Nk == 4)
			{
				temp = SubWord(temp);
			}
			out[roundId][colId] = xor(out[roundId - 1][colId], temp);
			progress++;
		}
		return out;
	}

	private static byte[] xor(byte[] a, byte[] b)
	{
		byte[] out = new byte[a.length];
		for (int i = 0; i < a.length; i++)
		{
			out[i] = (byte) (a[i] ^ b[i]);
		}
		return out;

	}

	private static byte[] SubWord(byte[] in)
	{
		byte[] tmp = new byte[in.length];

		for (int i = 0; i < tmp.length; i++)
			tmp[i] = (byte) (RijndaelSBox[in[i] & 0x000000ff] & 0xff);

		return tmp;
	}

	private static byte[] rotateRSK(byte[] in)
	{
		byte[] tmp = new byte[4];
		tmp[0] = in[1];
		tmp[1] = in[2];
		tmp[2] = in[3];
		tmp[3] = in[0];

		return tmp;
	}

	public static byte[] schedule_core(byte[] in, int rconpointer)
	{
		in = rotateRSK(in);
		int hex;
		for (int i = 0; i < in.length; i++)
		{
			hex = in[i];
			in[i] = (byte) RijndaelSBox[hex & 0x000000ff];
		}
		in[0] = (byte) Rcon[rconpointer];
		return in;
	}

	/**
	 * Methods for encryption
	 * 
	 * State - message during encryption roundKey - Round Key round - Round
	 */
	private static byte[][] addRoundKey(byte[][] state, byte[][] roundKey)
	{
		byte[][] out = new byte[state.length][state[0].length];
		for (int i = 0; i < Nb; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				out[j][i] = (byte) (roundKey[i][j] ^ state[j][i]);
			}
		}
		return out;
	}

	private static byte[][] byteSub(byte[][] state)
	{
		byte[][] out = new byte[state.length][state[0].length];

		for (int row = 0; row < 4; row++)
			for (int col = 0; col < Nb; col++)
				out[row][col] = (byte) (RijndaelSBox[(state[row][col] & 0x000000ff)] & 0xff);

		return out;

	}

	private static byte[][] shiftRow(byte[][] state)
	{
		byte[] t = new byte[4];
		for (int r = 1; r < 4; r++)
		{
			for (int c = 0; c < Nb; c++)
				t[c] = state[r][(c + r) % Nb];
			for (int c = 0; c < Nb; c++)
				state[r][c] = t[c];
		}

		return state;
	}

	private static byte[][] mixColumns(byte[][] state)
	{
		int[] sp = new int[4];
		byte b02 = (byte) 0x02;
		byte b03 = (byte) 0x03;
		byte[][] out = new byte[4][4];

		for (int c = 0; c < 4; c++)
		{
			sp[0] = gMul(b02, state[0][c]) ^ gMul(b03, state[1][c]) ^ state[2][c] ^ state[3][c];
			sp[1] = state[0][c] ^ gMul(b02, state[1][c]) ^ gMul(b03, state[2][c]) ^ state[3][c];
			sp[2] = state[0][c] ^ state[1][c] ^ gMul(b02, state[2][c]) ^ gMul(b03, state[3][c]);
			sp[3] = gMul(b03, state[0][c]) ^ state[1][c] ^ state[2][c] ^ gMul(b02, state[3][c]);
			for (int i = 0; i < 4; i++)
				out[i][c] = (byte) (sp[i]);
		}
		return out;
	}

	// Galois Field (256) Multiplication of two Bytes
	public static byte gMul(byte a, byte b)
	{
		byte out = 0;
		int aCopy = a, bCopy = b, t;
		while (aCopy != 0)
		{
			if ((aCopy & 1) != 0)
				out = (byte) (out ^ bCopy);

			t = (bCopy & 128);
			bCopy = (bCopy << 1);

			if (t != 0)
				bCopy = (bCopy ^ 27);
			aCopy = ((aCopy & 255) >> 1);
		}
		return out;
	}
}
