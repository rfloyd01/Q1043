export class SongBackend {
    title!:string;
    artist!:string;
    ratings!:number[];

    constructor(title?:string, artist?:string, ratings?:number[]) {
        if (title) this.title = title;
        if (artist) this.artist = artist;
        if (ratings) this.ratings = ratings;
    }
}
