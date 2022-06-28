#include <iostream>
#include <vector>
#include <fstream>
#include <chrono>
#include <string>
#include "SongCompare.h"

struct Song
{
	std::string artist = "";
	std::string title = "";

	//order for years is {2001, 2002, 2003, ..., 2021}
	bool yearsNomiated[21] = { false };
	int rankByYear[21] = { 0 };
};

int alphabeticallyFirst(std::string one, std::string two)
{
	//returns true if string one comes alphabetically before string two

	//we're going to ignore punctuation marks so every time a symbol is encountered we skip
	//over it and subtract the total length of the word by one. Create two temp variables to 
	//hold adjusted length data
	int oneLength = one.length(), twoLength = two.length();

	int stop = one.length() < two.length() ? one.length() : two.length();
	int oneLocation = 0, twoLocation = 0;
	bool oneParantheses = false, twoParantheses = false;

	//If either string starts with the word "the" we need to ignore it.
	//Example: "The Beatles" will come alphabetically before "Led Zeppelin" even though 'T' comes after 'L'
	if (one.substr(0, 3) == "The " || one.substr(0, 3) == "the ") oneLocation = 4;
	if (two.substr(0, 3) == "The " || two.substr(0, 3) == "the ") twoLocation = 4;

	//while (true)
	for (int i = 0; i < stop; i++)
	{
		//song titles can feature anything from letters, to numbers, to symbols.
		//For my purposes, symbols come first alphabetically, then numbers and then letters.
		//ASCII codes for lower case letters are 97 - 122
		//ASCII codes for upper case letters are 65 - 90
		//ASCII codes for numbers are 48 - 57
		//any other number can be considered a symbol
		//As a final note, if a space is included in either string then it will constitute a new
		//word and will stop the comparison. ASCII code for space is 32.
		//if (oneLocation >= stop || twoLocation >= stop) break;

		bool oneIsALetter = true, twoIsALetter = true;

		//If either character is a symbol (other than space) then skip over it
		//TODO: Maybe I can use some kind of Regex expression here?
		//if (one[oneLocation] < 32 || (one[oneLocation] > 32 && one[oneLocation] < 48) || (one[oneLocation] > 57 && one[oneLocation] < 65)
		//	|| (one[oneLocation] > 90 && one[oneLocation] < 97) || one[oneLocation] > 122)
		//{
		//	//this character is a symbol so we skip over it
		//	oneLocation++;
		//	oneLength--;
		//	//if (oneLocation >= stop) break; //if the symbol was at the end of the shorter word we can stop scanning
		//}
		//if (two[twoLocation] < 32 || (two[twoLocation] > 32 && two[twoLocation] < 48) || (two[twoLocation] > 57 && two[twoLocation] < 65)
		//	|| (two[twoLocation] > 90 && two[twoLocation] < 97) || two[twoLocation] > 122)
		//{
		//	//this character is a symbol so we skip over it
		//	twoLocation++;
		//	twoLength--;
		//	//if (twoLocation >= stop) break; //if the symbol was at the end of the shorter word we can stop scanning
		//}
		//if (oneLocation >= stop || twoLocation >= stop) break; //check to see if there was any punctuation at the end of the string

		//any letters should be converted to uppercase for simplicity
		if (one[oneLocation] >= 97 && one[oneLocation] <= 122) one[oneLocation] -= 32;
		if (two[twoLocation] >= 97 && two[twoLocation] <= 122) two[twoLocation] -= 32;

		//check to see if either character isn't a letter
		if (one[oneLocation] >= 91) oneIsALetter = false;
		if (two[twoLocation] >= 91) twoIsALetter = false;

		//if one of the characters isn't a letter, check to see if it's a space.
		if (!(oneIsALetter && twoIsALetter))
		{
			//Case: One word has a space but the other doesn't
			if (one[oneLocation] == 32 && two[twoLocation] != 32) return 1; //the word in one finshed first so it comes first alphabetically
			if (one[oneLocation] != 32 && two[twoLocation] == 32) return 0; //the word in two finshed first so it comes first alphabetically

			//Case: Both words have a space (do nothing and continue on to the next word)

			//Case: One of the words has a number and the other doesn't
			if (oneIsALetter) return 0; //the character in two is a number and therefore comes first
			if (twoIsALetter) return 1; //the character in one is a number and therefore comes first

			//both characters are numbers, compare them like normal
			if (one[oneLocation] < two[twoLocation]) return 1; //the number in one comes before the number in two
			else if (one[oneLocation] > two[twoLocation]) return 0; //the number in one comes after the number in two
			//if the numbers are equal then we do nothing
		}
		else
		{
			//Case: we have two letters, this is just a normal alphabetical check
			if (one[oneLocation] > two[twoLocation]) return 0;
			else if (one[oneLocation] < two[twoLocation]) return 1;

			//if the letters are the same then we do nothing
		}

		//advance both letters to the next one in each word
		oneLocation++;
		twoLocation++;
	}

	//if we get to this point it means one of two things: Either that the two strings are the same,
	//or, we've reached the end of one of them. For the example "Black" and "Black Dog" would both 
	//reach this point.
	if (oneLength == twoLength) return 2; //the strings are the same and thus we return 2 to indicate equality
	else if (oneLength < twoLength) return 1; //one comes first as it's a shorter string
	else return 0; //one comes second because it's a longer string
}

int main()
{
	auto run_time = std::chrono::steady_clock::now();
	//start a timer to see how long this takes
	

	////Used to figure out the appropriate column size for database
	//int longestSongName = 0;
	//int longestArtistName = 0;
	//std::string longestSong = "";
	//std::string longestArtist = "";

	//std::ifstream myFile;
	//std::string songString;

	//std::vector<Song> allSongs;

	////read through each character...one at a time...for all 1043 songs
	////long long location = 0; //a long long is overkill here right?

	//for (int year = 2021; year >= 2001; year--)
	//{
	//	myFile.open("Song_Data/" + std::to_string(year) + ".txt");

	//	int numberOfSongs = 1043;
	//	if (year <= 2003) numberOfSongs = 104; //apparently they only went up to 104 until the year 2004

	//	bool artistsIncluded = false;
	//	if (year == 2021 || year == 2019 || year == 2014) artistsIncluded = true; //for some reason the artists are only included for these years

	//	for (int i = 1; i <= numberOfSongs; i++)
	//	{
	//		//songs are either of the form:
	//		//#. Song Name 
	//		//or
	//		//#. Song Name - Artist Name
	//		//For years where artists are included I'll scan backwards until hitting the first '-' character,
	//		//and consider everything after that to be the artist (not sure what will happen to any artists that happen
	//		//to have a hyphen in their name, sorry Ne-Yo!)

	//		//Read each song one line at a time
	//		Song currentSong;
	//		std::getline(myFile, songString);
	//		int location = 0, end = songString.length() - 1, compareValue = 0;

	//		if (artistsIncluded)
	//		{
	//			while (songString[end] != '-') end--;

	//			for (int j = end + 2; j < songString.length(); j++)
	//				if (songString[j] != ',') currentSong.artist += songString[j]; //ignore commas because of comma delimitting for Excel
	//			end -= 2; //this puts the end of the song title to the approprite spot right before the '-'

	//			if (currentSong.artist.length() > longestArtistName)
	//			{
	//				longestArtistName = currentSong.artist.length();
	//				longestArtist = currentSong.artist;
	//			}
	//		}

	//		//Iterate through the line until we hit the fist '.' character, the song starts two characters after this
	//		while (songString[location] != '.') location++;
	//		location += 2;

	//		//iterate until the end of the song title and capture each character in the title field of currentSong
	//		while (location <= end)
	//		{
	//			//I'm using comma delimitting in Excel so just leave off any commas in song titles or artist names
	//			if (songString[location] != ',') currentSong.title += songString[location];
	//			location++;
	//		}
	//		currentSong.yearsNomiated[year - 2001] = true;
	//		currentSong.rankByYear[year - 2001] = i;

	//		if (currentSong.title.length() > longestSongName)
	//		{
	//			longestSongName = currentSong.title.length();
	//			longestSong = currentSong.title;
	//		}
	//		//For some reason the year 2021 has the songs counting down instead of up (maybe because it's
	//		//the most recent). Adjust the song rankings here
	//		if (year == 2021) currentSong.rankByYear[year - 2001] = (i + 1044 - 2 * i);

	//		//Place song in the ongoing list using a binary search algorithm
	//		int front = 0, back = allSongs.size() - 1, search_index = 0;
	//		while (true)
	//		{
	//			if (front > back)
	//			{
	//				allSongs.insert(allSongs.begin() + front, currentSong);
	//				break;
	//			}

	//			search_index = (front + back) / 2;
	//			compareValue = alphabeticallyFirst(currentSong.title, allSongs[search_index].title);

	//			if (compareValue == 2)
	//			{
	//				//The song names are the same, we have to be carfeul though because there are some
	//				//songs by different artists that have the same name (i.e. All Along the Watchtower
	//				//by Jimi Hendrix and Bob Dylan). Check to see if this song has already been placed this year
	//				//and if so, add it as a distinct line item. This will add more line items overall, but,
	//				//prevents overwritting of useful data. Generally speaking, it's unlikely that songs with the
	//				//same names will be close to eachother on the list (i.e. Jimi Hendrix's All along the Watchtower
	//				//will probably always be ranked a few hundred places higher than Bob Dylan's). Since we'll always
	//				//encounter duplicates in the same order, we insert duplicate songs AFTER the current song instead
	//				//of before it.
	//				if (allSongs[search_index].yearsNomiated[year - 2001])
	//				{
	//					//song has already been added this year, add it as a new line item after the currently
	//					//found song
	//					auto yote = year;
	//					auto yeet = allSongs[search_index];

	//					allSongs.insert(allSongs.begin() + search_index + 1, currentSong);
	//					break;
	//				}

	//				//Song hasn't been added this year already
	//				if (artistsIncluded) allSongs[search_index].artist = currentSong.artist;
	//				allSongs[search_index].yearsNomiated[year - 2001] = true;
	//				allSongs[search_index].rankByYear[year - 2001] = i;
	//				break;
	//			}
	//			else if (compareValue == 1) back = search_index - 1;
	//			else front = search_index + 1;
	//		}
	//	}

	//	//close the file once we're done with it
	//	myFile.close();
	//}
	//
	///*std::cout << "The longest song name has a length of " << longestSongName << " characters." << std::endl;
	//std::cout <<  longestSong << std::endl;
	//std::cout << "The longest artist name has a length of " << longestArtistName << " characters." << std::endl;
	//std::cout << longestArtist << std::endl;*/
	////int artist_missing = 0;

	////print the songs in a CSV format to an external file, make sure to leave a space for new title and new artist which 
	////will help in auditing Spotify's performance
	//std::ofstream outputFile;
	//outputFile.open("Song_Data/combined_data.txt");
	//for (int i = 0; i < allSongs.size(); i++)
	//{
	//	//if (allSongs[i].artist == "") artist_missing++;
	//	outputFile << (allSongs[i].artist + ",," + allSongs[i].title + ",,");
	//	for (int j = 0; j < 20; j++)
	//	{
	//		if (allSongs[i].yearsNomiated[j]) outputFile << allSongs[i].rankByYear[j] << ",";
	//		else outputFile << "0,";
	//	}
	//	if (allSongs[i].yearsNomiated[20]) outputFile << allSongs[i].rankByYear[20];
	//	else outputFile << "0";
	//	outputFile << "\n";
	//}

	////std::cout << "There are currently " << artist_missing << " lines of data without an artist" << std::endl;

textCompare("2007_compare");

	std::cout << "Ran in ";
	std::cout << std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now() - run_time).count() / 1000000000.0;
	std::cout << " seconds." << std::endl;

	return 0;
}

//Notes:
// It currently takes just under 4 seconds (when not printing anything) to go through all of the data and sort it alphabetically by song title

//Current Issues
//1. Songs that are spelt the same, but with different capital letters aren't being recognized as the same song.
//   i.e. 25 or 6 To 4, 25 Or 6 To 4 and 25 or 6 to 4 are all counted as different songs when they're clearly the same.
// (complete): this brought the amount fo distinct songs from ~3900 to ~2400

//2. Songs with the same name but by different artists can overwrite eachother, i.e. All Along the Watchtower by Jimi
//   Hendrix and Bob Dylan
//(in progress) This appears to be working, there were a lot more repeats per year than I though, for example there are
// at least four songs called "Dreams" that are by different artists appearing in various years. Looking at the rankings for
//all along the watchtower though, it appears that some data is going into the wrong row at times, so this still may need a
//little work.

//3. There may be something wrong with the alphabetical algorithm when it comes to checking a space against a letter.
//   The parsed data has a section of songs that looks like this: American Girl, American Pie, America, American Girl,
//   American Pie, American Woman, America ... and goes on for like 10 more lines.
//   (complete): Turns out the algorithm didn't account for strings that started with the same word, this has been fixed now

//4. Might need to expand to Unicode, certain songs have symbols not supported by ASCII, etc. "And the Cradle Will Rock..."
//   by Van Halen feature an elipses character (not just three dots) and it garbles things a bit.

//5. Songs with a single punctation mark at the end are being counted as different songs then those without a punctuation 
//   mark, e.g. "Are You Experienced" and "Are You Experienced?" should be the same song

//6. There may be a slight issue with the sorting algorithm, for some reason the various songs name "Dreams" are getting
//   sprinkled in between songs that start with the word "Don't". Not sure why as most other things seem to be in the right order.
//(complete) It turns out that when placing songs with the same name in the same year I was using the wrong variable to insert, 
// I was using the 'front' variable instead of the 'current_index' variable.

//Tackling these 6 issues would probably solve about 90% of all issues/duplicates. This will make cleaning up by hand much easier.