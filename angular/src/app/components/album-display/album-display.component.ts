import { Component, Input, OnInit } from '@angular/core';
import { Album } from 'src/app/models/album';

@Component({
  selector: 'app-album-display',
  templateUrl: './album-display.component.html',
  styleUrls: ['./album-display.component.css']
})
export class AlbumDisplayComponent implements OnInit {

  @Input() album!:Album;
  albumArtworkSource!:string;
  albumLink:string = "";

  constructor() { }

  ngOnInit(): void {
    
  }

  ngOnChanges() {
    //this function gets called whenever a new song is selected.
    this.albumLink = "https://open.spotify.com/album/" + this.album.spotifyURI;
    this.albumArtworkSource = this.album.albumArtworkURL;
  }

  notLoaded() {
    console.log("no album loaded");
  }

}
