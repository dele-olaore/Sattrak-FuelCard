package com.dexter.fuelcard.util;

import java.util.Random;

public class PasswordUtil
{
	private static String[] letters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	private static String[] speletters = new String[]{"@", "%", "&", "$", "="};
	private static String[] digits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	
	public static String generateRandomPassword()
	{
		Random rnd = new Random();
		StringBuilder strB = new StringBuilder();
		
		strB.append(speletters[rnd.nextInt(speletters.length)]);
		for(int i=0; i<3; i++)
		{
			int index = rnd.nextInt(letters.length);
			String letter = letters[index];
			strB.append(letter);
		}
		for(int i=0; i<2; i++)
		{
			int index = rnd.nextInt(digits.length);
			String letter = digits[index];
			strB.append(letter);
		}
		
		return strB.toString();
	}
	
	public static String generateRandomRedeemCode()
	{
		Random rnd = new Random();
		StringBuilder strB = new StringBuilder();
		
		for(int i=0; i<2; i++)
		{
			int index = rnd.nextInt(digits.length);
			String letter = digits[index];
			strB.append(letter);
		}
		for(int i=0; i<3; i++)
		{
			int index = rnd.nextInt(letters.length);
			String letter = letters[index];
			strB.append(letter);
		}
		strB.append(speletters[rnd.nextInt(speletters.length)]);
		
		return strB.toString();
	}
}
