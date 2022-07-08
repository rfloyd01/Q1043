import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Artist } from '../models/artist';
import { RawData } from '../models/raw-data';
import { Song } from '../models/song';

@Injectable({
  providedIn: 'root'
})
export class BackendServiceService {

  //we can only access the API endpoints if we're logged in as an authenticated user
  backendUrl:string = 'http://localhost:8083'

  constructor(private http:HttpClient) {
  }

  getPaginatedRawData(pageNumber:number, pageSize:number):Observable<any> {
    //this will return a Java Page type, we don't need all of this data so just return it as an 'any' for now
    return this.http.get<any>(this.backendUrl + '/rawdata?pageNumber=' + pageNumber + '&pageSize=' + pageSize, );
  }

  getDataById(pageNumber:number, pageSize:number):Observable<RawData[]> {
    //Not a real pagination, however, we use a page number and size to get the appropriate raw data by id. We
    //do this because when editing raw data and putting it back in the database, the order isn't maintained, and
    //using dtandard pagination makes it possible to grab the same raw data multiple times.
    return this.http.get<any>(this.backendUrl + '/rawdata/dirty/byid?pageNumber=' + pageNumber + '&pageSize=' + pageSize, );
  }

  getCleanDataById(pageNumber:number, pageSize:number):Observable<RawData[]> {
    //same as the above function, but instead of trying to get song data, I'm now trying to get proper
    //album data from all of the songs that have been cleaned up from the initial Spotify search.

    //TODO: Currently only searching for raw data that doesn't have an album yet, may want to change this back in the future
    return this.http.get<any>(this.backendUrl + '/rawdata/clean/byid/noalbum?pageNumber=' + pageNumber + '&pageSize=' + pageSize, );
  }

  updateRawData(newData:RawData[]):Observable<boolean> {
    return this.http.patch<boolean>(this.backendUrl + '/rawdata/dirty', newData);
  }

  updateCleanRawData(newData:RawData[]):Observable<boolean> {
    return this.http.patch<boolean>(this.backendUrl + '/rawdata/clean', newData);
  }

  addCompleteSongData(songData:Song[]):Observable<boolean> {
    return this.http.post<boolean>(this.backendUrl + '/songs', songData);
  }

  getPaginatedDataByRank(dataType:string, pageNumber:number, pageSize:number, sortType:string, direction?:string):Observable<any> {
    //unlike songs, we only score albums in one way (for now).
    let dir:string = "asc"; //default to ascending order
    if (direction) dir = direction;
    return this.http.get<any>(this.backendUrl + '/' + dataType + 's/byRank?pageNumber=' + pageNumber + '&pageSize=' + pageSize + '&sort=' + sortType + '&direction=' + dir );
  }

  getMultiplePaginatedDataByRank(dataType:string, firstPageNumber:number, pageSize:number, numberOfPages:number, sortType:string, direction?:string):Observable<any[]> {
    //This function returns a Java Page object. I didn't want to go through the hassle of creating the same object
    //on the front end (actually for all I know it's already built-in) so I decided to just return an 'any' object.
    let dir:string = "asc"; //default to ascending order
    if (direction) dir = direction;
    return this.http.get<any>(this.backendUrl + '/' + dataType + 's/byRank/multiple?firstPage=' + firstPageNumber + '&pageSize=' + pageSize + '&numberOfPages=' + numberOfPages + '&sort=' + sortType + '&direction=' + dir );
  }

  getPaginatedArtistsOrderedById(pageNumber:number, pageSize:number):Observable<any> {
    return this.http.get<any>(this.backendUrl + '/artists/byId?pageNumber=' + pageNumber + '&pageSize=' + pageSize);
  }

  updateArtists(artists:Artist[]):Observable<boolean> {
    return this.http.put<boolean>(this.backendUrl + '/artists/byId', artists);
  }
}