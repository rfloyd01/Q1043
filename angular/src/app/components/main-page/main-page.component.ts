import { Component, OnInit } from '@angular/core';
import { Album } from 'src/app/models/album';
import { Artist } from 'src/app/models/artist';
import { Song } from 'src/app/models/song';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  constructor() {
    //For now, fill in the song array with dummy data
  }

  buttonList:string[] = ["Overall Data", "2021", "2020", "2019"];
  currentDisplay:string = "Overall Data";
  colorOptions:string[] = ["white", "lightgrey"];
  selectedSong!:Song;
  currentDataType:string = "song";

  testSongs:Song[] = [];

  ngOnInit(): void {
    this.addTestSongs();
  }

  buttonClicked(value:string) {
    this.currentDisplay = value;
  }

  listItemSelected(song:Song) {
    this.selectedSong = song;
  }

  setDataType(dataType:string) {
    this.currentDataType = dataType;

    //JUST WHILE TESTING: The below two lines should force the list to re-render.
    //Add an empty song and then delete it.
    this.addTestSongs();
  }

  addTestSongs() {
    this.testSongs = [];
    this.testSongs.push(new Song(1, "Stairway to Heaven", "Led Zepplin", new Album(1, "Led Zeppelin IV", 1971, 8, "5EyIDBAqhnlkAHqvPRwdbX", "https://i.scdn.co/image/ab67616d0000b2734509204d0860cc0cc67e83dc", new Artist(1, "Led Zeppelin", []), []), "0RO9W1xJoUEpq5MEelddFb", 61, [1, 1, 1], 1, 1, 1, 1, "Mighty"));
    this.testSongs.push(new Song(2, "Bohemian Rhapsody", "Queen", new Album(2, "A Night at the Opera", 1975, 12, "7HVoV2lgVsmuiHsjbbUJB4", "https://i.scdn.co/image/ab67616d0000b2733025a441495664948b809537", new Artist(2, "Queen", []), []), "5eIDxmWYxRA0HJBYM9bIIS", 38, [44, 16, 12], 2, 2, 2, 2, "Less Mighty"));
    
  }

}
