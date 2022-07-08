import { Component, OnInit, OnChanges, Input } from '@angular/core';
import { Artist } from 'src/app/models/artist';

@Component({
  selector: 'app-artist-display',
  templateUrl: './artist-display.component.html',
  styleUrls: ['./artist-display.component.css']
})
export class ArtistDisplayComponent implements OnInit {

  @Input() artist!:Artist;
  rankedAlbums:number = 0;
  artistArtworkSource:string = "";
  artistLink:string = "";

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges() {
    //this function gets called whenever a new song is selected.
    this.artistLink = "https://open.spotify.com/artist/" + this.artist.spotifyURI;
    this.rankedAlbums = this.artist.albums.length;
    this.artistArtworkSource = this.artist.artistArtworkURL;
  }

  notLoaded() {
    console.log("no album loaded");
  }

}
