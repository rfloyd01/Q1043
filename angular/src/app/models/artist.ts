import { Album } from "./album";

export class Artist {
    id: number = 0; //ids get assigned in the backend so this is only included for cleaner JSON
    name!:string;
    albums!:Album[];
    artistScore!:number;
    totalRankedSongs!:number;
    notes!:string;

    constructor(id?:number, name?:string, albums?:Album[], artistScore?:number,
        totalRankedSongs?:number, notes?:string) {
        if (id) this.id = id;
        if (name) this.name = name;
        if (albums) this.albums = albums;
        if (artistScore) this.artistScore = artistScore;
        if (totalRankedSongs) this.totalRankedSongs = totalRankedSongs;
        if (notes) this.notes = notes;
    }
}
