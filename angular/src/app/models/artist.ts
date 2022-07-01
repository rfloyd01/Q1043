import { Album } from "./album";

export class Artist {
    id: number = 0; //ids get assigned in the backend so this is only included for cleaner JSON
    name!:string;
    albums!:Album[];

    constructor(id?:number, name?:string, albums?:Album[]) {
        if (id) this.id = id;
        if (name) this.name = name;
        if (albums) this.albums = albums;
    }
}
