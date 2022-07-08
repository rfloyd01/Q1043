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
  //albumLink:string = "";

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges() {
    //this function gets called whenever a new song is selected.
    //this.albumLink = "https://open.spotify.com/album/" + this.album.spotifyURI;
    //this.albumArtworkSource = this.album.albumArtworkURL;
    this.rankedAlbums = this.artist.albums.length;
  }

  notLoaded() {
    console.log("no album loaded");
  }

}
