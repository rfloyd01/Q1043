import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { FormsModule } from '@angular/forms';
import { MainPageComponent } from './components/main-page/main-page.component';
import { CallbackComponent } from './components/callback/callback.component';
import { HttpClientModule } from '@angular/common/http';
import { SongDisplayComponent } from './components/song-display/song-display.component';
import { SpotifyAPIComponent } from './components/spotify-api/spotify-api.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    MainPageComponent,
    CallbackComponent,
    SongDisplayComponent,
    SpotifyAPIComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
