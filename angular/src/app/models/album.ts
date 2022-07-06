import { Artist } from "./artist";
import { Song } from "./song";

export class Album {
    id: number = 0; //ids get assigned in the backend so this is only included for cleaner JSON
    title!:string;
    releaseYear!:number;
    totalTracks!:number;
    spotifyURI!:string
    albumArtworkURL!:string;
    artist!:Artist;
    songs!:Song[];
    albumScore!:number;
    notes!:string;

    constructor(id?:number, title?:string, releaseYear?:number, totalTracks?:number, spotifyURI?:string, albumArtworkURL?:string,
        artist?:Artist, songs?:Song[], albumScore?:number, notes?:string) {
        if (id) this.id = id;
        if (title) this.title = title;
        if (releaseYear) this.releaseYear = releaseYear;
        if (totalTracks) this.totalTracks = totalTracks;
        if (spotifyURI) this.spotifyURI = spotifyURI;
        if (albumArtworkURL) this.albumArtworkURL = albumArtworkURL;
        if (artist) this.artist = artist;
        if (songs) this.songs = songs;
        if (albumScore) this.albumScore = albumScore;
        if (notes) this.notes = notes;
    }

}
