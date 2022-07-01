import { Album } from "./album";

export class Song {
    id: number = 0; //ids get assigned in the backend so this is only included for cleaner JSON
    title!:string;
    artist!:string;
    album!:Album;
    spotifyURI!:string
    popularity!:number;
    rankings!:number[];

    //All ranking data is calculated in the backend, default initialize these values to 0
    averageScore:number = 0.0;
    overallScore:number = 0.0;
    averageRank:number = 0;
    overallRank:number = 0;
    notes:string = ""; //no need to create notes by default
    

    constructor(id?:number, title?:string, artist?:string, album?:Album, spotifyURI?:string, popularity?:number, rankings?:number[], 
        averageScore?:number, overallScore?:number, averageRank?:number, overallRank?:number, notes?:string ) {
        if (id) this.id = id;
        if (title) this.title = title;
        if (artist) this.artist = artist;
        if (album) this.album = album;
        if (spotifyURI) this.spotifyURI = spotifyURI;
        if (popularity) this.popularity = popularity;
        if(rankings) this.rankings = rankings;
        if(averageScore) this.averageScore = averageScore;
        if(overallScore) this.overallScore = overallScore;
        if(averageRank) this.averageRank = averageRank;
        if(overallRank) this.overallRank = overallRank;
        if(notes) this.notes = notes;
    }
}
