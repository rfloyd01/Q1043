import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user';
import { ClientInfoService } from './client-info.service';

@Injectable({
  providedIn: 'root'
})
export class ApiServiceService {

  //we can only access the API endpoints if we're logged in as an authenticated user
  authenticatedUser:User;
  searchURLString:string = 'https://api.spotify.com/v1/search'
  requestHeaders:HttpHeaders;

  constructor(private http:HttpClient, private clientInfo:ClientInfoService) {
    this.authenticatedUser = new User();
    this.requestHeaders = new HttpHeaders();
  }

  setAuthenticatedUser(newUser:User) {
    //set the user and http request header information
    this.authenticatedUser = newUser;
    this.requestHeaders = new HttpHeaders({
      'Authorization': 'Bearer ' + this.authenticatedUser.accessToken
    });
  }

  removeAuthenticatedUser() {
    this.authenticatedUser = new User();
  }

  searchSongsByTrackName(trackName:string, artistName:string):Observable<any> {
    //console.log(trackName + ', ' + artistName);
    let queryString:string = 'track:' + trackName;
    if (artistName != "") queryString += '+artist:' + artistName;

    //let finalQueryString:string = queryString.split(' ').join('%20');
    let httpParameters = new HttpParams();
    httpParameters = httpParameters.append('q', queryString);
    httpParameters = httpParameters.append('type', 'track');
    let httpOptions = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.authenticatedUser.accessToken),
      params: httpParameters
    };
    
    return this.http.get<any>(this.searchURLString, httpOptions);
  }

  searchSongsGeneric(trackName:string, artistName:string):Observable<any> {
    let queryString:string = trackName; //the 'track:' is missing from this query on purpose
    if (artistName != "") queryString += '+artist:' + artistName; //the artist name still seems to work

    let httpParameters = new HttpParams();
    httpParameters = httpParameters.append('q', queryString);
    httpParameters = httpParameters.append('type', 'track');
    let httpOptions = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.authenticatedUser.accessToken),
      params: httpParameters
    };
    
    //console.log(queryString + '&type=track');
    //return this.http.get<any>(this.searchURLString + queryString + '&type=track', httpOptions);
    return this.http.get<any>(this.searchURLString, httpOptions);
  }

  searchSongsByNameArtistAndYear(trackName:string, artistName:string, year:number):Observable<any> {
    //this function only gets called after we've completed inital calls on all of the raw data. After that happens
    //we have the correct song and artist name for each song, but not necessarily the correct album. By searching with
    //the correct track, artist, an approximate year (recieved from initial api calls) and limiting the results to only
    //5 should allow us to really hone in on the correct album. We don't include the 'track:' in the search query here.

    //After playing around with the API for a bit it seems that we get the best results if we only include two filters
    //instead of three. The best thing to do here is to search by the track name ("without the track filter") and a range
    //of years that spans +/-10 from the given year. Once we get our results ignore anything where the artist isn't an
    //exact match for the artist passed to this function.
    let queryString:string = trackName + "+artist:" + artistName  + "+year:" + (year - 10) + "-" + (year + 10);
    let httpParameters = new HttpParams();
    httpParameters = httpParameters.append('q', queryString);
    httpParameters = httpParameters.append('type', 'track'); //for some reason we get better results when searching by track instead of album
    httpParameters = httpParameters.append('limit', '5'); //we have better info than with unclean raw data so no need to search for full 20 items
    let httpOptions = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.authenticatedUser.accessToken),
      params: httpParameters
    };
    
    return this.http.get<any>(this.searchURLString, httpOptions);
  }

  getArtistInformation(artistName:string):Observable<any> {
    //I decided after getting all of the album information that it would also be nice to have the URI and artwork URL for artists as 
    //well. All of the artists in the database (with the exception of those with non-UTF-8 characters in their name) should be exactly 
    //the way they are in spotify.
    let queryString:string = artistName;
    let httpParameters = new HttpParams();
    httpParameters = httpParameters.append('q', queryString);
    httpParameters = httpParameters.append('type', 'artist'); //for some reason we get better results when searching by track instead of album
    httpParameters = httpParameters.append('limit', '5'); //we have better info than with unclean raw data so no need to search for full 20 items
    let httpOptions = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.authenticatedUser.accessToken),
      params: httpParameters
    };
    
    return this.http.get<any>(this.searchURLString, httpOptions);
  }
}
