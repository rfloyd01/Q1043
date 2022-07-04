import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Song } from 'src/app/models/song';

@Component({
  selector: 'app-list-item',
  templateUrl: './list-item.component.html',
  styleUrls: ['./list-item.component.css']
})
export class ListItemComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
    //make sure we don't get an index out of bounds
    if (this.backgroundColorValue < -1 || this.backgroundColorValue > this.bacgkroundColorOptions.length) this.backgroundColorValue = 0;

    this.content = this.listValue + ". ";

    if (this.displayType == "song") this.content += this.song.title;
    else if (this.displayType == "artist") this.content += this.song.artist;
    else if (this.displayType == "album") this.content += this.song.album.title;
    else this.content += this.song.album.releaseYear.toString();
  }

  @Input() listValue:number = 0;
  @Input() backgroundColorValue:number = 0;
  @Input() song!:Song;
  @Input() displayType:string = "";
  @Output() clickEvent = new EventEmitter<Song>();

  content!:string;

  bacgkroundColorOptions:string[] = ["white", "lightgrey"];

  clicked() {
    this.clickEvent.emit(this.song);
  }


}
