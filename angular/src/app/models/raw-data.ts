export class RawData {
    id!:number;
    rawData!:string;

    constructor(id?:number, rawData?:string) {
        if (id) this.id = id;
        if (rawData) this.rawData = rawData;
    }
}
