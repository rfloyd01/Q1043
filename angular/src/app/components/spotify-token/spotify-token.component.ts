import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { CookieService } from 'src/app/services/cookie.service';

@Component({
  selector: 'app-spotify-token',
  templateUrl: './spotify-token.component.html',
  styleUrls: ['./spotify-token.component.css']
})
export class SpotifyTokenComponent implements OnInit {

  accessToken:string = "";

  constructor(private cookieService:CookieService,  private router:Router) {
  }

  ngOnInit(): void {
  }

  getToken() {
    if (this.cookieService.getCookie("User") != "") {
      let user:User = JSON.parse(this.cookieService.getCookie("User"))
      this.accessToken = user.accessToken;
    }
    else {
      this.router.navigateByUrl("/SpotifyAPI");
    }
  }

}
