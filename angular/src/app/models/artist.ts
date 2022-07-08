import { Album } from "./album";

export class Artist {
    id: number = 0; //ids get assigned in the backend so this is only included for cleaner JSON
    name!:string;
    albums!:Album[];
    artistScore!:number;
    rankedTracks!:number;
    spotifyURI!:string;
    artistArtworkURL!:string;
    notes!:string;

    constructor(id?:number, name?:string, albums?:Album[], artistScore?:number,
        rankedTracks?:number, spotifyURI?:string, artistArtworkURL?:string, notes?:string) {
        if (id) this.id = id;
        if (name) this.name = name;
        if (albums) this.albums = albums;
        if (artistScore) this.artistScore = artistScore;
        if (rankedTracks) this.rankedTracks = rankedTracks;
        if (spotifyURI) this.spotifyURI = spotifyURI;
        if (artistArtworkURL) this.artistArtworkURL = artistArtworkURL;
        if (notes) this.notes = notes;
    }
}
