import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ClientInfoService {

  constructor() { }

  client_id:string = 'd8efd9555b454aad9bc59f79f2e454c8'; // Your client id
  client_secret:string = 'b31a56ff271e4ca08a71ba3426ce9c46'; // Your secret
  redirect_uri:string = 'http://localhost:4200/callback'; // Your redirect uri

  //my spotify email address: lynrdskynrd05@yahoo.com
  //my spotify password: dosm6GOSS_juw1nam
}
