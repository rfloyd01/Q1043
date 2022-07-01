export class User {
    accessToken:string = '';
    refreshToken:string = '';

    constructor(accessToken?:string, refreshToken?:string) {
        if (accessToken) this.accessToken = accessToken;
        if (refreshToken) this.refreshToken = refreshToken;
    }
}
