import { Component, OnInit } from '@angular/core';
import { Event, RouterEvent, Router, NavigationEnd } from '@angular/router';
import { BehaviorSubject, filter, Observable } from 'rxjs';
import { Album } from 'src/app/models/album';
import { Artist } from 'src/app/models/artist';
import { RawData } from 'src/app/models/raw-data';
import { Song } from 'src/app/models/song';
import { User } from 'src/app/models/user';
import { ApiServiceService } from 'src/app/services/api-service.service';
import { BackendServiceService } from 'src/app/services/backend-service.service';
import { ClientInfoService } from 'src/app/services/client-info.service';
import { CookieService } from 'src/app/services/cookie.service';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  scope:string = 'user-read-private user-read-email';
  searchSong:string = '';
  loggedIn:boolean = false;
  foundSongs:Array<Song> = [];
  rawData:Array<RawData> = [];
  problemSongs:Array<RawData> = []; //For some reasons, not every song can be searched by track (like "'Aint Wastin' Time No More"), add problem songs here as they're encountered
  pageNumber:number = 1;
  pageSize:number = 10;
  printScoreResults:boolean = false; //used for debugging issues with the song score algorithm, set to true to see individual scores for songs
  processedRequests:number = 0;
  processedProblems:number = 0;
  maximumPage:number = 0; //for now just do ten pages worth of raw data to see how things are working
  cleanRawData:boolean = true; //if this is true we try to create "complete" songs
  yearsOfData:number = 21;

  private rawDataEvent = new BehaviorSubject<number>(0);
  private songDataEvent = new BehaviorSubject<number>(0);
  private problemSongEvent = new BehaviorSubject<number>(0);

  constructor(private clientInfoService:ClientInfoService, private cookieService:CookieService, private apiService:ApiServiceService,
    private backendService:BackendServiceService) {}

  ngOnInit(): void {
    //see if a user is already logged in or not by looking to see if there's an active "User" cookie.
    //If there isn't then go to the login page.
    if (this.cookieService.getCookie('User') == '') this.login();
    this.loggedIn = true;

    //After logging in (or if we're already logged in) set the authenticated user in the authenticated user service
    let userCredentialsCookie = JSON.parse(this.cookieService.getCookie("User"));
    
    let userCredentials:User = new User(userCredentialsCookie['accessToken'], userCredentialsCookie['refreshToken']);
    this.apiService.setAuthenticatedUser(userCredentials); //set the logged in user's crednetials in the API service as we'll need access to the tokens

    //subscribe to the rawDataLoaded observable so we know when raw data is finished loading
    this.rawDataEvent.subscribe((res: number) => {
      //console.log("Raw Data Event: " + res);
      if (res == 1) {
        //We've obtained a new page of raw data from the backend. We can now start querying the
        //Spotify API.
        
        this.sleep(5000); //set a timer for 5 seconds so we don't make too many API calls too quickly
        this.getSongsFromRawDataArray(); //with the raw data loaded we can now query the Spotify API
      }
      else if (res == 2) {
        //The updated raw data has been successfully saved into the backend database, we can now save
        //the song data in the database as well.
        //console.log(this.foundSongs);
        this.addSongDataToDatabase();
      }
    })

    this.songDataEvent.subscribe((res: number) => {
      //console.log("Song Data Event: " + res);
      if (res == this.pageSize && this.pageSize != 0) {
        //We've gotten song data from Spotify for all of the songs in our raw data page, we now need to
        //send the song data, as well as the updated raw data, back to the backend. We first send the
        //raw data.

        //we need to check and make sure that no problem songs arose before proceeding.
        if (this.problemSongs.length > 0) {
          //subtract the number of problem songs from our processed data variable so that
          //after processing again we will be redirected back to this function.
          this.processedRequests -= this.problemSongs.length;
          this.searchForGenericSongs();
        }
        else {
          this.updateRawData();
          this.pageNumber++; //increment the page number after successfully retrieving raw data from back end
        }
      }
      else if (res == -1) {
        //Song data has been successfully saved into the backend database or we're just starting to collect data.
        //Either way, we now start the process of getting raw data from the backend and processing it.
        if (this.pageNumber > this.maximumPage) return; //We're done when there are no more pages of raw data to process
        console.log("Starting page " + this.pageNumber + " out of " + this.maximumPage);

        //First make the call for paginated data from the backend
        this.processedRequests = 0; //reset our processed data counter to 0
        this.getRawData();
      }
    })

    this.problemSongEvent.subscribe((res:number) => {
      //console.log("Problem Data Event: " + res);
      if (res == this.problemSongs.length && res > 0) {
        //we've processed all of the problem songs, which means we can continue our regular algorithm.
        //Empty out the problem song array and set the songDataEvent Behavior Subject to this.pageSize 
        //in order to continue. (We need the greater than 0 part because when we initialize the page, the
        //problem songs array has length of 0 and this event gets raised on initializetion of the problem
        //song event at 0)
        this.problemSongs = [];
        this.processedProblems = 0;
        this.setSongDataEvent = this.pageSize;
      }
    })

    //after logging in and gaining access to the Spotify, start a loop to work through all of the raw data in the back end
    this.setSongDataEvent = -1; //setting the Behavoir Subject to -1 triggers the loop to start
  }

  public set setRawDataEvent(statusCode:number) {
    //The raw data event lets us know three different things:
    //1. When we've successfully read a new page of raw data from the backend that needs to be processed.
    //   This event gets a status code of 1.
    //2. When we've successfully sent updated raw data to the back end. This event has a status code of 2.
    //3. There was an issue with the raw data for one (or more) of our search queries. In this case, we
    //   make another call to the Spotify API using a generic search (instead of a track search) to select our song.
    //   In this case we get a status code of -1.
    this.rawDataEvent.next(statusCode);
  }

  public get getRawDataEvent():Observable<number> {
    return this.rawDataEvent.asObservable();
  }

  public set setSongDataEvent(dataLoaded:number) {
    //The song data event lets us know two things:
    //1. When we've successfully searched for a song in Spotify and obtained the best result. In this case
    //   the variable will be set to a number between 1 and the data pagination size. To let us know what
    //   percentage of the paginated raw data has been processed through Spotify.
    //2. After we've successfully sent the Spotify data to the backend and saved it into our database. To
    //   make sure this number doesn't get confused with a pagination number the code will be -1 as that
    //   number isn't possible for pagination.
    this.songDataEvent.next(dataLoaded);
  }

  public get getSongDataEvent():Observable<number> {
    return this.songDataEvent.asObservable();
  }

  public set setProblemSongEvent(statusCode:number){
    //If one of our Spotify search results turns up empty, this event is triggered which tells us to try and
    //serach with different parameters. There's one kind of status code here:
    //1. A status between 0 - problemSongs.length let's us know how many problem songs have been processed. When
    //   the value of the status code is the same as the length of the array we're done processing songs.
    this.problemSongEvent.next(statusCode);
  }

  public get getProblemSongEvent():Observable<number> {
    return this.problemSongEvent.asObservable();
  }

  sleep(milliseconds:number) {
    //a custom sleep function, we use this to wait inbetween Apotify API calls so that we don't overload
    //the server and get a 429 response. I don't currently know what the limit here is but I figure it's
    //better to be safe
    let start = new Date().getTime();
    while ((new Date().getTime() - start) <= milliseconds) {
      //do nothin while we wait for the timer to expire
    }
  }

  login() {
    let state:string = this.generateRandomString(16);
    let redirectLocation:string = "response_type=code&client_id=";

    redirectLocation += this.clientInfoService.client_id;
    redirectLocation += ("&scope=" + this.scope + "&redirect_uri=" + this.clientInfoService.redirect_uri);
    redirectLocation += ("&state=" + state);

    //put the state key in a cookie
    this.cookieService.deleteCookie('spotify_auth_state'); //delete the cookie if it already exists
    this.cookieService.setCookie({name : 'spotify_auth_state', value : state});

    //delete any existing User cookies
    this.cookieService.deleteCookie('User');
    this.apiService.removeAuthenticatedUser();
    this.loggedIn = false;

    window.location.href = ("https://accounts.spotify.com/authorize?" + redirectLocation);
  }

  generateRandomString(length:number):string {
    var text = '';
    var possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  
    for (var i = 0; i < length; i++) {
      text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
  }

  getSongsFromRawDataArray() {
    if (this.loggedIn) {
      if (this.cleanRawData) {
        this.getCompleteSongsFromRawDataArray();
        return;
      }
      //this function will iterate through everything currently in the raw data array, call to the Spotify API
      //and attempt to get the correct song, and then update the raw data as well as send back the song

      //Before iterating through the raw data, clear out all songs (if any) from previous searches
      this.foundSongs = [];

      for (let data of this.rawData) {
        //raw data comes in as a single comma delimited string, split it to get the different components
        //the order of the parsed data is [raw artist, found artist (starts off blank), raw title, found title (starts off blank), 2001 rating, 2002 rating, ... 2021 rating]
        let parsedRawData:string[] = data.rawData.split(',');
        let correctSong:Song = new Song();
        let winningScore:number = -1; //start at -1 in case we have trouble finding results and the 'winner' has a score of 0

        //this block is used to help debug the scoring algorithm
        if (this.printScoreResults) {
          console.log("Raw Data: " + JSON.stringify(data));
        }

        //call the api service for each song in the raw data list
        this.apiService.searchSongsByTrackName(parsedRawData[2], parsedRawData[0]).subscribe(res => {
          //console.log(this.apiDataLoaded.getValue());
          let items:Array<any> = res['tracks']['items'];
          if (items.length < 4) {
            //for some reason this song can't be searched by track name, add it to the problem songs list
            //which will allow us to do another generic search with the API instead of a track search. We return 
            //from the subscribe without implementing any of the below logic except for incrementing the 
            //processed data variable. Adding songs to the problem array means we can't make another call to the
            //back end until the problem songs are processed.
            this.problemSongs.push(data);
            this.setSongDataEvent = ++this.processedRequests;
            return;
          }
          for (let item of items) {
            let albumInfo:any = item['album'];
            let albumArtwork:Array<any> = albumInfo['images']
            let artistInfo:Array<any> = item['artists'];

            //make sure to only take the first four digits (year) of the release date
            let releaseYear:string = albumInfo['release_date'];
            releaseYear = releaseYear.substring(0, 4);
            let artwork:string = '';
            if (albumArtwork[0] != undefined) artwork = albumArtwork[0]['url'];
            else (console.log('yeeet'));
  
            //TODO: The below line is no longer correct as I changed the constructor for the Song class.
            let foundSong:Song = new Song(0, item['name'], artistInfo[0]['name'], albumInfo['name'], artwork, parseInt(releaseYear), item['popularity'], item['uri']);
            let songScore:number = this.createSongScore(parsedRawData, foundSong);

            if (songScore > winningScore) {
              winningScore = songScore;
              correctSong = foundSong;
            }
          }

          if ((correctSong.artist == undefined || correctSong.title == undefined)) console.log(parsedRawData);

          //once we get the correct song, update the raw data with the found artist and track name for validation later on
          //let rawDataArray = data.rawData.split(',');
          parsedRawData[1] = correctSong.artist.split(',').join(' '); //make sure to remove any commas in the artist name
          parsedRawData[3] = correctSong.title.split(',').join(' '); //make sure to remove any commas in the song title
          data.rawData = parsedRawData.join(',');

          //after finding the correct song, add in the ratings from the raw data
          for (let i:number = 4; i < parsedRawData.length; i++) correctSong.rankings.push(parseInt(parsedRawData[i]));

          this.foundSongs.push(correctSong); //add the song to our found songs array
          //console.log(data);
          //console.log(correctSong);
          //Increment our processed data counter. When this variable equals the pagination size this is our indicator
          //to start sending data to the backend.
          this.setSongDataEvent = ++this.processedRequests;

        }, error => {
          if (parseInt(error['error']['status']) == 429) {
            //We got rejected from making any more API calls because we've reached our limit. Wait for a minute
            //and then continue the search.
            this.sleep(60000);
            this.setRawDataEvent = 0; //reset the raw data Behaviour Subject
            this.setRawDataEvent = 1; //trigger the raw data event to try and query SPotify again using the same raw data
          }
          else {
            //we've gotten some other error, print to the console to figure out what happened
            console.log(error['error'])
          }
        })
      }
    }
    
  }

  getCompleteSongsFromRawDataArray() {
    if (this.loggedIn) {
      //this function will iterate through everything currently in the raw data array, call to the Spotify API
      //and attempt to get the correct song, and then update the raw data as well as send back the song

      //Before iterating through the raw data, clear out all songs (if any) from previous searches
      this.foundSongs = [];
      for (let data of this.rawData) {
        console.log(data);
        //raw data comes in as a single comma delimited string, split it to get the different components
        //the order of the parsed data is [raw artist, found artist (starts off blank), raw title, found title (starts off blank), 2001 rating, 2002 rating, ... 2021 rating]
        if (data == null) {
          this.pageNumber = this.maximumPage + 1;
          break; //don't try and parse any data that isn't actually there
        }
        let parsedRawData:string[] = data.rawData.split(String.fromCharCode(9)); //the clean raw data is delimited with tabs
        let correctSongRankings:number[] = [];

        for (let i:number = 0; i < this.yearsOfData; i++) correctSongRankings.push(parseInt(parsedRawData[i + 4]));
        //let correctSong:Song = new Song(0, parsedRawData[1], parsedRawData[0], new Album(), "", 0, correctSongRankings, 0, 0, 0, 0, parsedRawData[parsedRawData.length - 1]);
        let correctSong:Song = new Song(0, parsedRawData[1], parsedRawData[0], new Album(), "", 0, correctSongRankings, 0, 0, 0, 0, " ");

        //this block is used to help debug the scoring algorithm
        if (this.printScoreResults) {
          console.log("Raw Data: " + JSON.stringify(data));
        }

        //the last thing we do before initiating the search is to see if we have a combined song (like "Brain Damage / Eclipse"). These songs,
        //which are really the combination of two songs rarely turn up any results. Separate the songs out and only search for the first one.
        let searchTitle:string = parsedRawData[1].split("/")[0];

        //call the api service for each song in the raw data list
        this.apiService.searchSongsByNameArtistAndYear(searchTitle, parsedRawData[0], parseInt(parsedRawData[3])).subscribe(res => {
          //console.log(this.apiDataLoaded.getValue());
          let items:Array<any> = res['tracks']['items'];
          let earliestYear = 2022; //start with a high year so that we can get under it when searching

          if (items.length == 0) {
            //instead of adding the album to the problem songs array, we just skip it. With the data clean already it shouldn't
            //be too hard to just input missing albums by hand.
            this.foundSongs.push(correctSong); //the song data is still accurate, only the Album and Artist data will need to be fixed by hand later
            this.setSongDataEvent = ++this.processedRequests;
            return;
          }

          for (let item of items) {
            let albumInfo:any = item['album'];
            let albumArtwork:Array<any> = albumInfo['images']
            let artistInfo:Array<any> = item['artists'];
            let albumName:string = albumInfo["name"];

            //first and foremost we check to make sure we have an exact match on the artist, if not then go to the next album
            //console.log("The album " + albumInfo['name'] + " was released by " +  artistInfo[0]['name']);
            if (artistInfo[0]['name'] != parsedRawData[0]) continue;

            //also make sure that we aren't looking at a compilation album as it's possible for them to have the
            //correct artist name but not actually be by the artist.
            if (albumInfo['album_type'] != "album") continue;

            //make sure to only take the first four digits (year) of the release date
            let releaseYear:string = albumInfo['release_date'];
            releaseYear = releaseYear.substring(0, 4);

            //console.log("The album " + albumInfo['name'] + "was released in " +  releaseYear);

            //unlike when using the uncleaned raw data, we're very trusting of the results we get when using the clean
            //raw data. As such we don't need to worry about creating album scores or anything like that. Simply choose
            //the album with the earliest release year as it gives us the best chance of it being the original album and
            //not some sort of live album or greatest hits compilation.
            if (parseInt(releaseYear) > earliestYear) continue;
            else if (parseInt(releaseYear) == earliestYear) {
              //if the years are the same, make sure the current album doesn't feature the words "live", "deluxe" or "greatest"
              if (albumName.search("Live") != -1) continue;
              if (albumName.search("Greatest") != -1) continue;
              if (albumName.search("Deluxe") != -1) continue;
            }
            else earliestYear = parseInt(releaseYear);

            let artwork:string = '';
            if (albumArtwork[0] != undefined) artwork = albumArtwork[0]['url'];

            //Create an artist object to full in the album object and then put them both in the song object
            let artist:Artist = new Artist(0, parsedRawData[0], []);
            let album:Album = new Album(0, albumName, parseInt(releaseYear), albumInfo['total_tracks'], albumInfo['id'], artwork, artist, []);

            //console.log("Here's the found album: " + JSON.stringify(album));
            correctSong.album = album;

            //we also need to add the song URI and popularity values from the search
            correctSong.popularity = item['popularity'];
            correctSong.spotifyURI = item['id'];
          }

          //console.log("Correct song is: " + JSON.stringify(correctSong));

          this.foundSongs.push(correctSong); //add the song to our found songs array
          this.setSongDataEvent = ++this.processedRequests;

        }, error => {
          if (parseInt(error['error']['status']) == 429) {
            //We got rejected from making any more API calls because we've reached our limit. Wait for a minute
            //and then continue the search.
            this.sleep(60000);
            this.setRawDataEvent = 0; //reset the raw data Behaviour Subject
            this.setRawDataEvent = 1; //trigger the raw data event to try and query SPotify again using the same raw data
          }
          else {
            //we've gotten some other error, print to the console to figure out what happened
            console.log(error['error'])
          }
        })
      }
    }
    
  }

  getRawData() {
    if (this.cleanRawData) {
      this.backendService.getCleanDataById(this.pageNumber, this.pageSize).subscribe(res => {
        //res will hold a Java Page object, we're only interested in the 'items' array, which will be
        //an array of raw data
        this.rawData = res;
        this.pageNumber = this.rawData[this.rawData.length - 1].id; //TODO: Only using this to find last few albums, remove when done
        this.setRawDataEvent = 1; //triggers event that will allow us to access Spotify API
      })
    }
    else {
      this.backendService.getDataById(this.pageNumber, this.pageSize).subscribe(res => {
        //res will hold a Java Page object, we're only interested in the 'items' array, which will be
        //an array of raw data
        this.rawData = res;
        this.setRawDataEvent = 1; //triggers event that will allow us to access Spotify API
      })
    }
  }

  updateRawData() {
    if (this.cleanRawData) {
      //we don't update the raw data when working with clean data
      this.setRawDataEvent = 2;
      return;
    }
    this.backendService.updateRawData(this.rawData).subscribe(res => {
      if (res) {
        //the updated raw data has been successfully saved in the backend, we change the
        //rawDataEvent Behavior Subject to alert the app that we can now start saving the
        //song data into the backend.
        this.setRawDataEvent = 2;
      }
    });
  }

  addSongDataToDatabase() {
    this.backendService.addCompleteSongData(this.foundSongs).subscribe(res => {
      if (res) {
        //the song data has been successfully added to the database. We set the songDataEvent Behavior Subject
        //to -1 which alerts the app that we can now safely get more paginated raw data from the backend.
        this.setSongDataEvent = -1;
      }
    });
  }

  createSongScore(rawString:string[], foundSong:Song):number {
    //takes a song found from Spotify and assigns it a numeric value based on how likely it is to be the song we're actually
    //looking for. First and foremost we care about how close the title of the song is between the raw data and the search result.
    //Next, if the artist is included in the raw data we want to see how close it is to the found song.

    let remasterIndex:number = foundSong.title.indexOf('Remaster');
    if (remasterIndex >= 0) {
      //I'm not sure if this is always the case, but on Spotify songs that are remastered seem to be of the form Song Name - Year Remaster.
      //If this holds true for all songs then starting at the back of the song title and moving forwards, scan until we find the location of
      //the first '-' symbol and then cut everything from that symbol to the end of the title. We work from the back in case the actual song
      //title also has a '-' character in it.
      let hyphenIndex = foundSong.title.length;
      while (hyphenIndex > 0) {
        if (foundSong.title.charAt(--hyphenIndex) == '-') {
          foundSong.title = foundSong.title.substring(0, hyphenIndex - 1);
          break;
        }
      }
    }

    //First see how close the names of the songs are in the raw data and the found song
    let titleScore:number = this.createStringSimilarityScore(rawString[2], foundSong.title);
    if (titleScore == undefined) titleScore = 0; //if the score comes up as undefined just set it to 0

    //After getting the title score we next look for the artist score. If no artist is included in the raw data then we skip this part
    let artistScore:number = 0;
    if (rawString[0] != "") artistScore = this.createStringSimilarityScore(rawString[0], foundSong.artist);
    if (artistScore == undefined) artistScore = 0; //if the score comes up as undefined just set it to 0

    //Now we need to look at the release date and deem how "classic" the song is. Songs released at least 25 years ago get a full score,
    //songs released at least 20 years ago get 75%, 15 years ago get 50%, 10 years ago get 25% and anything newer than 10 years old gets 
    //a score of 0.

    //TODO: Uncomment the below code if I ever need to search through Spotify with unclean raw data again.
    let yearScore:number = 0;
    // if (2022 - foundSong.released >= 25) yearScore = 100;
    // else if (2022 - foundSong.released >= 20) yearScore = 75;
    // else if (2022 - foundSong.released >= 15) yearScore = 50;
    // else if (2022 - foundSong.released >= 10) yearScore = 25;
    // if (yearScore == undefined) yearScore = 0; //if the score comes up as undefined just set it to 0

    //Last but not least we need to look at the popularity score for the song. We don't necessarily need the most popular
    //song on the list, however, we want to make sure we aren't selecting something with a popularity score that's low enough
    //it wouldn't really warrant being on a top 1,043 list
    let popularityScore = foundSong.popularity;
    if (popularityScore == undefined) popularityScore = 0; //if the score comes up as undefined just set it to 0

    if (this.printScoreResults) {
      console.log("Song: " + foundSong.title);
      console.log("      Title score: " + titleScore + " (weighted = " + titleScore / 3 + ")");
      console.log("      Artist score: " + artistScore + " (weighted = " + artistScore / 3 + ")");
      console.log("      Year score: " + yearScore + " (weighted = " + yearScore / 6 + ")");
      console.log("      Popularity score: " + popularityScore + " (weighted = " + popularityScore / 6 + ")");
      console.log("      Total song score = " + ((titleScore / 3) + (artistScore / 3) + (yearScore / 6) + (popularityScore / 6)))
    }

    //The year only carries half as much weight as the title and artist names
    return ((titleScore / 3) + (artistScore / 3) + (yearScore / 6) + (popularityScore / 6));
  }

  createStringSimilarityScore(oneString:string, twoString:string):number {
    //Compares two strings to see how similar they are and returns a numeric score to reflect the similarity. If two strings
    //are identical they'll get a score of 100 and if they have 0 shared letters whatsoever they'll get a score of 0. When 
    //comparing strings only letters and numbers are taken into account, not symbols.
    let score:number = 0;

    //Cast the letters of both strings to uppercase for an easier comparison
    let one:string = oneString.toUpperCase();
    let two:string = twoString.toUpperCase();

    //If the names are an exact match then we score a perfect 100
    let oneLetters:Array<number> = new Array<number>(26).fill(0);
    let oneNumbers:Array<number> = new Array<number>(10).fill(0);
    let twoLetters:Array<number> = new Array<number>(26).fill(0);
    let twoNumbers:Array<number> = new Array<number>(10).fill(0);

    //first scan the raw data
    for (let char of one) {
      //make sure that we're looking at a letter or number
      if (char >= '0' && char <= '9') {
        oneNumbers[parseInt(char)]++;
      }
      else if (char >= 'A' && char <= 'Z') {
        oneLetters[char.charCodeAt(0) - 65]++; //65 is ASCII for 'A'
      }
    }

    //then scan the found data
    for (let char of two) {
      //make sure that we're looking at a letter or number
      if (char >= '0' && char <= '9') twoNumbers[parseInt(char)]++;
      else if (char >= 'A' && char <= 'Z') twoLetters[char.charCodeAt(0) - 65]++; //65 is ASCII for 'A'
    }

    //iterate through both letter and number arrays. There are 36 total slots to check so we compare the numbers in 
    //each slot, find the percentage difference between them and take a weighted average. For example, if both words had
    //two of the letter A this would get a weighted average of 100% / 36 = 2.78%. Letters and numbers that don't appear
    //in either word should be ommitted from the final tally. Using this style of counting the words 'abcdea' and 'bbb'
    //would get a score of being 6.6% similar to each other and the words 'abcdebb' and 'bbb' would be 20% similar to
    //each other. This may need to be tweaked at some point if it ends up not working that well.

    let charactersIncluded:number = 36; //we chip away from this number and divide by it at the end

    for (let i:number = 0; i < 10; i++) {
      //we need to make sure we don't do any division by 0
      if (oneNumbers[i] == 0 && twoNumbers[i] == 0) charactersIncluded-- //both are zero so don't include in final tally
      else if (!(oneNumbers[i] == 0 || twoNumbers[i] == 0)) {
        //we take the lower number and divide it by the higher number.
        let lower:number = (oneNumbers[i] < twoNumbers[i]) ? oneNumbers[i] : twoNumbers[i];
        let higher:number = (lower == oneNumbers[i]) ? twoNumbers[i] : oneNumbers[i];
        score += (lower / higher);
      }

      //if only one of the values is 0 we add nothing to the score but also don't lower the characters included number
    }

    for (let i:number = 0; i < 26; i++) {
      //we need to make sure we don't do any division by 0
      if (oneLetters[i] == 0 && twoLetters[i] == 0) charactersIncluded-- //both are zero so don't include in final tally
      else if (!(oneLetters[i] == 0 || twoLetters[i] == 0)) {
        //we take the lower number and divide it by the higher number.
        let lower:number = (oneLetters[i] < twoLetters[i]) ? oneLetters[i] : twoLetters[i];
        let higher:number = (lower == oneLetters[i]) ? twoLetters[i] : oneLetters[i];
        score += (lower / higher);
      }

      //if only one of the values is 0 we add nothing to the score but also don't lower the characters included number
    }

    //For the final step, divide the titleScore by the amount of charactersIncluded
    score /= charactersIncluded;

    //As one final kicker we compare the ratio of lengths of the two strings to make sure nothing strange has happened. For
    //example the song 1999 by Prince would score a perfect 100 with any song that happened to be remastered in the year 1999
    //(like "Some Kind of Wonderful - 1999 Remaster").
    let long:number = (one.length > two.length) ? one.length : two.length;
    let short:number = (one.length == long) ? two.length : one.length;

    //console.log("Similarity score between " + one + " and " + two + " is " + score * short / long);
    return 100 * score * short / long;
  }

  searchForGenericSongs() {
    if (this.loggedIn) {
      //this function will iterate through all of our problem songs, call to the Spotify API with a generic query,
      //and attempt to get the correct song.
      for (let data of this.problemSongs) {

        //raw data comes in as a single comma delimited string, split it to get the different components
        //the order of the parsed data is [raw artist, found artist (starts off blank), raw title, found title (starts off blank), 2001 rating, 2002 rating, ... 2021 rating]
        let parsedRawData:string[] = data.rawData.split(',');
        let correctSong:Song = new Song();
        let winningScore:number = -1; //start at -1 in case we have trouble finding results and the 'winner' has a score of 0

        //this block is used to help debug the scoring algorithm
        if (this.printScoreResults) {
          console.log("Raw Data: " + JSON.stringify(data));
        }

        //call the api service for each song in the problem list
        this.apiService.searchSongsGeneric(parsedRawData[2], parsedRawData[0]).subscribe(res => {

          let items:Array<any> = res['tracks']['items'];
          if (items.length < 4) {
            //for whatever reason we just can't get any hits on this song, skip over it for now and fill in by hand
            //at a later point.
            this.setProblemSongEvent = ++this.processedProblems;
            return;
          }

          for (let item of items) {
            let albumInfo:any = item['album'];
            let albumArtwork:Array<any> = albumInfo['images']
            let artistInfo:Array<any> = item['artists'];

            //make sure to only take the first four digits (year) of the release date
            let releaseYear:string = albumInfo['release_date'];
            releaseYear = releaseYear.substring(0, 4);
            let artwork:string = '';
            if (albumArtwork[0].length > 0) artwork = albumArtwork[0]['url'];

            //TODO: Fix the below constructor if I ever need this function again
            let foundSong:Song = new Song();
            //console.log(foundSong.title + ' gets a song score of ' + this.createSongScore(parsedRawData, foundSong));
            //console.log(foundSong.title + ' has a popularity of ' + foundSong.popularity);
            let songScore:number = this.createSongScore(parsedRawData, foundSong);

            //if (parsedRawData[2] == "Ain't My Cross To Bear") console.log(foundSong + ', ' + songScore);
            //if((songScore < 0)) console.log(parsedRawData  + ', ' + songScore);
            //console.log(songScore);
            if (songScore > winningScore) {
              //if ((correctSong.artist == undefined || correctSong.title == undefined)
              winningScore = songScore;
              correctSong = foundSong;
            }
          }

          if ((correctSong.artist == undefined || correctSong.title == undefined)) console.log(parsedRawData);

          //once we get the correct song, update the raw data with the found artist and track name for validation later on
          //let rawDataArray = data.rawData.split(',');
          parsedRawData[1] = correctSong.artist.split(',').join(' '); //make sure to remove any commas in the artist name
          parsedRawData[3] = correctSong.title.split(',').join(' '); //make sure to remove any commas in the song title
          data.rawData = parsedRawData.join(',');

          //after finding the correct song, add in the ratings from the raw data
          for (let i:number = 4; i < parsedRawData.length; i++) correctSong.rankings.push(parseInt(parsedRawData[i]));

          this.foundSongs.push(correctSong); //add the song to our found songs array
          //console.log(data);
          //console.log(correctSong);

          //We change the processedProblems and problemSongEvent variables last as this can potentially trigger other events
          //to happen.
          this.setProblemSongEvent = ++this.processedProblems;

        }, error => {
          if (parseInt(error['error']['status']) == 429) {
            //We got rejected from making any more API calls because we've reached our limit. Wait for a minute
            //and then continue the search.
            this.sleep(60000);
            this.setRawDataEvent = 0; //reset the raw data Behaviour Subject
            this.setRawDataEvent = 1; //trigger the raw data event to try and query SPotify again using the same raw data
          }
          else {
            //we've gotten some other error, print to the console to figure out what happened
            console.log(error['error'])
          }
        })
      }
    }
  }

}
