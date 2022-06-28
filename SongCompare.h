#pragma once
#include <iostream>
#include <string>
#include <fstream>
#include <sstream>
#include <vector>

//a function for comparing the text of two excel columns. all data has been searched in Spotify and cleaned up
//in Excel, this function is to help audit any songs that got mixed up in the process.

void textCompare(std::string fileName) {
	std::ifstream myFile;
	myFile.open("Song_Data/" + fileName + ".txt");

	std::vector<std::string> results;
	std::string currentLine = "";
	std::string splitLine = "";
	char delimeter = 9;

	for (int i = 0; i < 1043; i++) {
		std::getline(myFile, currentLine); //split each line at the tab

		//text file is created from excel and track names are separated with a horizontal tab
		//(ASCII character 9).
		//std::cout << currentLine << std::endl;
		int currentString = 0; //needed to split out period after ranking number
		std::pair<std::string, std::string> comparison;
		std::stringstream ss(currentLine);
		while (std::getline(ss, splitLine, delimeter))
		{
			if (!(currentString++)) {
				//this is the first word, remove the number and period
				comparison.first = splitLine.substr(splitLine.find('.') + 2);
			}
			else comparison.second = splitLine;
		}

		//check to see if either of our strings has parantheses in them, by removing the contents inside we got a lot more matches
		//without compromising songs that aren't matches. For example "Rosalita (Come Out Tonight)" and "Rosalita" will now match
		//after removing parantheses but "Rosalita" and "Jackie Wilson Said (I'm in Heaven When You Smile)" wouldn't.
		if (comparison.first.find('(') != 4294967295)
		{
			//almost every song with parentheses has them at the start or end. It may effect a few songs, but we'll ignore
			//any songs with parentheses in the middle of the string for simplicity
			if (comparison.first.find('(') == 0) comparison.first = comparison.first.substr(comparison.first.find(')') + 2);
			else  comparison.first = comparison.first.substr(0, comparison.first.find('('));

		}
		if (comparison.second.find('(') != 4294967295)
		{
			//almost every song with parentheses has them at the start or end. It may effect a few songs, but we'll ignore
			//any songs with parentheses in the middle of the string for simplicity
			if (comparison.second.find('(') == 0) comparison.second = comparison.second.substr(comparison.second.find(')') + 2);
			else  comparison.second = comparison.second.substr(0, comparison.second.find('('));

		}
		
		//with the string properly split we can start the comparison algorithm (this is more or less copy and pasted
		//from my front end.
		double score = 0;

		//create arrays to count the number of characters and numbers in each word
		int oneLetters[26] = { 0 };
		int twoLetters[26] = { 0 };
		int oneNumbers[10] = { 0 };
		int twoNumbers[10] = { 0 };

		//cast both strings to uppercase for an easier comparison
		for (int j = 0; j < comparison.first.length(); j++)
		{
			//convert letters to uppercase if necessary
			comparison.first[j] = toupper(comparison.first[j]);

			if ((comparison.first[j] >= (int)'0') && (comparison.first[j] <= (int)'9')) oneNumbers[comparison.first[j] - '0']++;
			else if ((comparison.first[j] >= (int)'A') && (comparison.first[j] <= (int)'Z')) oneLetters[comparison.first[j] - 'A']++;
		}
		for (int j = 0; j < comparison.second.length(); j++)
		{
			//convert letters to uppercase if necessary
			comparison.second[j] = toupper(comparison.second[j]);

			if ((comparison.second[j] >= (int)'0') && (comparison.second[j] <= (int)'9')) twoNumbers[comparison.second[j] - '0']++;
			else if ((comparison.second[j] >= (int)'A') && (comparison.second[j] <= (int)'Z')) twoLetters[comparison.second[j] - 'A']++;
		}

		int charactersIncluded = 36; //we chip away from this (total possible letters + total possible numbers)
		double lower = 0, higher = 0;

		//first compare numbers
		for (int j = 0; j < 10; j++)
		{
			if (!(oneNumbers[j] || twoNumbers[j])) charactersIncluded--; //both are zero so don't include
			else
			{
				//at least one of the numbers is non-zero. we take the lower number and divide by the higher
				lower = (oneNumbers[j] < twoNumbers[j]) ? oneNumbers[j] : twoNumbers[j];
				higher = (lower == oneNumbers[j]) ? twoNumbers[j] : oneNumbers[j];

				score += (lower / higher);
			}
		}

		//reset these variables to 0 before counting letters
		lower = 0;
		higher = 0;

		//then compare letters
		for (int j = 0; j < 26; j++)
		{
			if (!(oneLetters[j] || twoLetters[j])) charactersIncluded--; //both are zero so don't include
			else
			{
				//at least one of the numbers is non-zero. we take the lower number and divide by the higher
				lower = (oneLetters[j] < twoLetters[j]) ? oneLetters[j] : twoLetters[j];
				higher = (lower == oneLetters[j]) ? twoLetters[j] : oneLetters[j];

				score += (lower / higher);
			}
		}

		score /= (double)charactersIncluded;

		//if the score similarity is 67% or over than we can effectively say the songs are the same:
		if (score < 0.67) std::cout << i + 1 << ": " << comparison.first << std::endl;
	}
}
