import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'src/app/services/cookie.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private cookieService:CookieService, private router:Router) { }

  ngOnInit(): void {
  }

  /**
 * This is an example of a basic node.js script that performs
 * the Authorization Code oAuth2 flow to authenticate against
 * the Spotify Accounts.
 *
 * For more information, read
 * https://developer.spotify.com/web-api/authorization-guide/#authorization_code_flow
 */

  /*
  express = require('express'); // Express web server framework
  request = require('request'); // "Request" library
  cors = require('cors');
  querystring = require('querystring');
  cookieParser = require('cookie-parser');
  */

  cookieName:string = '';
  cookieValue:string = '';

  generateRandomString(length:number) {
    let text = '';
    let possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

    for (let i:number = 0; i < length; i++) {
      text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
  };

  stateKey = 'spotify_auth_state';

  generateCookie() {
    if (this.cookieName == '' || this.cookieValue == '') return; //don't do anything if either field is blank

    //check to see if the cookie with that name already exists
    if (this.cookieService.getCookie(this.cookieName) != '') console.log("That cookie already exists");
    else this.cookieService.setCookie({name : this.cookieName, value : this.cookieValue});
  }


  backToMainPage() {
    this.router.navigateByUrl('');
  }
}
