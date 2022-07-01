import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CookieService {

  constructor() { }

  public getCookie(name:string) {
    let ca:Array<string> = document.cookie.split(';');
    let caLen:number = ca.length;
    let cookieName = `${name}=`;
    let c:string;

    for (let i:number = 0; i < caLen; i++) {
      c = ca[i].replace(/^\s+/g, '');
      if (c.indexOf(cookieName) == 0) {
        return c.substring(cookieName.length, c.length);
      }
    }
    return '';
  }

  public deleteCookie(cookieName:string) {
    this.setCookie({name: cookieName, value: '', expireDays: -1});
  }

  public setCookie(params:any) {

    let d: Date = new Date();
    if (params.expireSeconds) {
      d.setTime(
        d.getTime() + params.expireSeconds * 1000
      );
    }
    else {
      //if the expiration time isn't given in seconds then default to days. Default value is 1 day
      d.setTime(
        d.getTime() + (params.expireDays ? params.expireDays: 1) * 24 * 60 * 60 * 1000
      );
    }
    
    document.cookie =
    (params.name ? params.name : '') + 
    '=' +
    (params.value ? params.value : '') + 
    ';' +
    (params.session && params.session == true ? '' : 'expires=' + d.toUTCString() + ';') + 
    'path=' +
    (params.path && params.path.length > 0 ? params.path : '/') +
    ';' +
    (location.protocol === 'https:' && params.secure && params.secure == true ? 'secure' : '');
  }
}
