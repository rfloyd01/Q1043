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
import { TopNavbarComponent } from './components/top-navbar/top-navbar.component';
import { HeaderComponent } from './components/header/header.component';
import { SideNavbarComponent } from './components/side-navbar/side-navbar.component';
import { ListItemComponent } from './components/list-item/list-item.component';
import { ChartTestComponent } from './components/chart-test/chart-test.component';
import { NgChartsModule } from 'ng2-charts';
import { AlbumDisplayComponent } from './components/album-display/album-display.component';
import { SpotifyTokenComponent } from './components/spotify-token/spotify-token.component';
import { ArtistDisplayComponent } from './components/artist-display/artist-display.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    MainPageComponent,
    CallbackComponent,
    SongDisplayComponent,
    SpotifyAPIComponent,
    TopNavbarComponent,
    HeaderComponent,
    SideNavbarComponent,
    ListItemComponent,
    ChartTestComponent,
    AlbumDisplayComponent,
    SpotifyTokenComponent,
    ArtistDisplayComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    NgChartsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
