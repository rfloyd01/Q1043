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
    this.currentBackgroundColorValue = this.backgroundColorValue;

    if (this.dataType == "song" || this.dataType == "album") this.content += this.data['title'];
    else if (this.dataType == "artist") this.content += this.data['name'];
    else {
      //year data will be sent via the Album object
      let releaseYear:number = this.data['releaseYear'];
      this.content += releaseYear.toString();
    }
  }

  @Input() listValue:number = 0;
  @Input() backgroundColorValue:number = 0;
  @Input() data!:any;
  @Input() dataType:string = "";
  @Output() clickEvent = new EventEmitter<ListItemComponent>();

  currentlySelected:boolean = false;
  currentBackgroundColorValue:number = 0;

  content!:string;

  bacgkroundColorOptions:string[] = ["white", "lightgrey", "lightblue"];


  clicked() {
    this.clickEvent.emit(this);
  }

  setSelected() {
    this.currentBackgroundColorValue = 2;
    this.currentlySelected = true;
  }

  setUnselected() {
    this.currentBackgroundColorValue = this.backgroundColorValue;
    this.currentlySelected = false;
  }

}
