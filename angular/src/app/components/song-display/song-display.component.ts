import { Component, Input, OnInit, OnChanges } from '@angular/core';
import { Song } from 'src/app/models/song';

@Component({
  selector: 'app-song-display',
  templateUrl: './song-display.component.html',
  styleUrls: ['./song-display.component.css']
})
export class SongDisplayComponent implements OnInit {

  @Input() song!:Song;
  albumArtworkSource!:string;
  songLink:string = "";

  constructor() { }

  ngOnInit(): void {
    
  }

  ngOnChanges() {
    //this function gets called whenever a new song is selected.
    this.songLink = "https://open.spotify.com/track/" + this.song.spotifyURI;
    this.albumArtworkSource = this.song.album.albumArtworkURL;
  }

  notLoaded() {
    console.log("no album loaded");
  }

}
