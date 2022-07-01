import { Component, Input, OnInit } from '@angular/core';
import { Song } from 'src/app/models/song';

@Component({
  selector: 'app-song-display',
  templateUrl: './song-display.component.html',
  styleUrls: ['./song-display.component.css']
})
export class SongDisplayComponent implements OnInit {

  @Input() song!:Song;

  constructor() { }

  ngOnInit(): void {
  }

}
