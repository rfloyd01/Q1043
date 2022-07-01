import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { filter, Observable } from 'rxjs';
import { CookieService } from 'src/app/services/cookie.service';
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { ClientInfoService } from 'src/app/services/client-info.service';
import { User } from 'src/app/models/user';
import { ApiServiceService } from 'src/app/services/api-service.service';

@Component({
  selector: 'app-callback',
  templateUrl: './callback.component.html',
  styleUrls: ['./callback.component.css']
})
export class CallbackComponent implements OnInit {

  constructor(private activatedRoute:ActivatedRoute, private cookieService:CookieService, private http:HttpClient,
    private clientInfo:ClientInfoService, private router:Router, private apiService:ApiServiceService) { }

  paramsObject:any;

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((params) => {
      this.paramsObject = {...params.keys, ...params};

      let code:string = this.paramsObject['params']['code'];
      let state:string = this.paramsObject['params']['state'];

      //We create the body using URLSearchParams so we can convert to application/x-www-form-urlencoded format
      let requestBody = new URLSearchParams();
      requestBody.set('code', code);
      requestBody.set('redirect_uri', this.clientInfo.redirect_uri);
      requestBody.set('grant_type', 'authorization_code');

      this.requestAuthToken(requestBody).subscribe(res => {
        let newUser = new User();
        newUser.accessToken = res['access_token'];
        newUser.refreshToken = res['refresh_token'];

        let expirationTime = res['expires_in'];

        this.cookieService.setCookie({'name': 'User', 'value' : JSON.stringify(newUser), 'expireSeconds': res['expires_in']})
        this.cookieService.deleteCookie('spotify_auth_state'); //don't need this cookie anymore
        //this.apiService.setAuthenticatedUser(newUser); //set the logged in user's crednetials in the API service as we'll need access to the tokens
        
        this.router.navigateByUrl(''); //go back to the homepage

      }, error => {
        console.log("Got the following error: " + JSON.stringify(error['error']));
      })
    })
  }

  requestAuthToken(body:URLSearchParams):Observable<any> {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/x-www-form-urlencoded',
        Authorization: 'Basic ' + btoa(this.clientInfo.client_id + ':' + this.clientInfo.client_secret)
      })
    };

    return this.http.post('https://accounts.spotify.com/api/token', body.toString(), httpOptions) as Observable<any>;
  }

}
