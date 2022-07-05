import { Component, OnInit, ViewChild } from '@angular/core';
import { Album } from 'src/app/models/album';
import { Artist } from 'src/app/models/artist';
import { Song } from 'src/app/models/song';
import { Chart, ChartConfiguration, ChartEvent, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

import { default as Annotation } from 'chartjs-plugin-annotation'
import { BackendServiceService } from 'src/app/services/backend-service.service';
import { ListItemComponent } from '../list-item/list-item.component';
import { first } from 'rxjs';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  constructor(private backendService:BackendServiceService) {
    Chart.register(Annotation);
  }

  buttonList:string[] = ["Overall Data", "2021", "2020", "2019"];
  currentDisplay:string = "Overall Data";
  colorOptions:string[] = ["white", "lightgrey"];
  currentlySelectedListItem!:ListItemComponent|null;
  currentDataType:string = "song";
  jumpToNumber!:number;

  //Pagination variables
  pageNumber:number = 0; //we load a few pages at a time, this number represents the lowest page we currently have
  pageSize:number = 20;
  totalListSize:number = 0;
  totalPages:number = 0;
  paginationPadding:number = this.pageSize / 2; //get next data set when we're 10 items away from bottom
  scrollHeightTarget:number = 0;
  scrollEventComplete:boolean = true;
  maximumPages:number = 3;

  overallRankingsDirection:string = "asc";
  overallRankingsDirectionImage:string = "assets/down_arrow.png";
  averageRankingsDirection:string = "asc";
  averageRankingsDirectionImage:string = "assets/down_arrow.png";
  currentRankingType:string = "overallScore";

  //testSongs:Song[] = [];
  songs:Song[] = [];
  listStart:number = 1;
  flip:number = -1;

  ngOnInit(): void {
    //when initially loading data, we grab a few pages at the same time (denoted by the maximumPages variable)
    this.backendService.getPaginatedSongsByRank(this.pageNumber, this.maximumPages * this.pageSize, "overallScore").subscribe(res => {
      //this function gives us a Java page object, for now we really only want the 'content' portion of it.
      let foundSongs:Song[] = res['content'];
      for (let song of foundSongs) {
        this.songs.push(song);
      }

      this.totalListSize = res['totalElements'];
      this.totalPages = res['totalPages'];
    })

    this.currentlySelectedListItem = null; //start off with no selection
  }

  buttonClicked(value:string) {
    this.currentDisplay = value;
  }

  listItemSelected(clickedItem:ListItemComponent) {
    //first, we reset the background color of whatever is currently selected
    if (this.currentlySelectedListItem != null) this.currentlySelectedListItem.currentBackgroundColorValue = this.currentlySelectedListItem.backgroundColorValue;

    //Update the selected song
    this.currentlySelectedListItem = clickedItem;
    this.currentlySelectedListItem.currentBackgroundColorValue = 2;

    //in order to highlight that lower ranked songs are better than higher
    //ranked ones, we iterate over the ranking data and invert the values
    //(i.e. song 1 gets a value of 1043 and song 1043 gets a value of 1) while
    //unranked years remain at zero
    let rankingCopy = this.currentlySelectedListItem.song.rankings;
    for (let i:number = 0; i < rankingCopy.length; i++) {
      if (rankingCopy[i] > 0) rankingCopy[i] = 1044 - rankingCopy[i];
    }

    this.lineChartData = {
      datasets: [
        {
          data: rankingCopy,
          label: 'Inverse Rank by Year',
          backgroundColor: 'rgba(148,159,177,0.2)',
          borderColor: 'rgba(148,159,177,1)',
          pointBackgroundColor: 'rgba(148,159,177,1)',
          pointBorderColor: '#fff',
          pointHoverBackgroundColor: '#fff',
          pointHoverBorderColor: 'rgba(148,159,177,0.8)',
          fill: 'origin'
        }
      ],
      labels: [ '2001', '2002', '2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010',
                '2011', '2012', '2013', '2014', '2015', '2016', '2017', '2018', '2019', '2020',
                '2021']
    }

    //then update the data set for chart with ranking data from selected song
    // let lineChartDataCopy = this.lineChartData;
    // lineChartDataCopy.datasets[0].data = this.selectedSong.rankings;
    // this.lineChartData = lineChartDataCopy;
  }

  setDataType(dataType:string) {
    this.currentDataType = dataType;

    //JUST WHILE TESTING: The below two lines should force the list to re-render.
    //Add an empty song and then delete it.
    //this.addTestSongs();
  }

  jumpTo() {
    //This function give a way to quickly maneuver to a far away part of the list without scrolling.
    //It will get the appropriate pagination pages and put the jumpTo number in the middle of the
    //list.
    let requiredPage:number = Math.floor((this.jumpToNumber - 1) / this.pageSize);

    //When the maximum page number is even the middle will be counted as the first page PAST the center. So if there
    //were a maximum of 4 pages then the middle would be page 3 --> ((1. not middle), (2. not middle), (3. middle page), (4. not middle))
    let firstPage:number = (requiredPage - (this.maximumPages / 2) < 0) ? 0 : requiredPage - (this.maximumPages / 2); 

    //this if statement executes if the maximum page number is odd
    if (this.maximumPages % 2 != 0) firstPage = (requiredPage - ((this.maximumPages - 1) / 2) < 0) ? 0 : requiredPage - ((this.maximumPages - 1) / 2);
    
    let scrollDirection:string = (this.currentRankingType == "averageScore") ? this.averageRankingsDirection : this.overallRankingsDirection;
    this.backendService.getMultiplePaginatedSongsByRank(firstPage, this.pageSize, this.maximumPages, this.currentRankingType, scrollDirection).subscribe(res => {
      this.songs = []; //clear the current song list
      this.listStart = firstPage * this.pageSize + 1;
      this.pageNumber = firstPage;
      for (let page of res) {
        this.songs = this.songs.concat(page['content']);
      }

      //after adding all songs to the list, we force the list viewer to scroll to the correct location
      let list = document.getElementById('item-list') as HTMLElement;
      let listItem = list.firstChild as HTMLElement;
      let listItemHeight = listItem.offsetHeight;

      list.scrollTo({top: (this.jumpToNumber - (firstPage * this.pageSize + 1)) * listItemHeight});

    })
  }

  // addTestSongs() {
  //   this.testSongs = [];
  //   let stairwayRanks:number[] = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1];
  //   let rhapsodyRanks:number[] = [44,	16,	12,	19,	8,	16,	18,	13,	9,	12,	5,	3,	4,	8,	7,	10,	7,	2,	2,	2,	2]
  //   this.testSongs.push(new Song(1, "Stairway to Heaven", "Led Zepplin", new Album(1, "Led Zeppelin IV", 1971, 8, "5EyIDBAqhnlkAHqvPRwdbX", "https://i.scdn.co/image/ab67616d0000b2734509204d0860cc0cc67e83dc", new Artist(1, "Led Zeppelin", []), []), "0RO9W1xJoUEpq5MEelddFb", 61, stairwayRanks, 1, 1, 1, 1, "Mighty"));
  //   this.testSongs.push(new Song(2, "Bohemian Rhapsody", "Queen", new Album(2, "A Night at the Opera", 1975, 12, "7HVoV2lgVsmuiHsjbbUJB4", "https://i.scdn.co/image/ab67616d0000b2733025a441495664948b809537", new Artist(2, "Queen", []), []), "5eIDxmWYxRA0HJBYM9bIIS", 38, rhapsodyRanks, 2, 2, 2, 2, "Less Mighty"));
    
  // }

  //The below items are for rendering charts
  public lineChartData!: ChartConfiguration['data'];

  public lineChartOptions: ChartConfiguration['options'] = {
    elements: {
      line: {
        tension: 0.5
      }
    },
    scales: {
      //this empty structure is used as a placeholder for dynamic theming
      x:{},
      y:{
        beginAtZero: true,
        min: 0,
        max: 1043
      }
    }
  };

  public lineChartType:ChartType = 'line';

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  // events
  public chartClicked({ event, active }: { event?: ChartEvent, active?: {}[] }): void {
    console.log(event, active);
  }

  public chartHovered({ event, active }: { event?: ChartEvent, active?: {}[] }): void {
    console.log(event, active);
  }

  getSongsByOverallRankings() {
    if (this.currentRankingType == "overallScore") {
      //since we're already looking at the overall score, clicking this button will change the direction of the ordering
      //as well as flipping the appropriate arrow.

      //Then, change the direction variable
      this.overallRankingsDirection = (this.overallRankingsDirection == "asc") ? "desc" : "asc";
      this.overallRankingsDirectionImage = (this.overallRankingsDirectionImage == "assets/down_arrow.png") ? "assets/up_arrow.png" : "assets/down_arrow.png";
    }
    else {
      //we're currently looking at the average score so clicking the overall rankings button shouldn't swap the 
      //direction of the order.
      this.currentRankingType = "overallScore";
    }

    //First, delete the current song array
    this.songs = [];

    //reset the pagination page number
    this.pageNumber = 0;

    //Finally, make sure that the numbers in the list are going in the right direction
    this.listStart = (this.overallRankingsDirection == "asc") ? 1: this.totalListSize;
    this.flip = (this.overallRankingsDirection == "asc") ? -1 : 1;

    //and then perform the backend call
    this.backendService.getPaginatedSongsByRank(this.pageNumber, this.pageSize * this.maximumPages, this.currentRankingType, this.overallRankingsDirection).subscribe(res => {
      //this function gives us a Java page object, for now we really only want the 'content' portion of it.
      let foundSongs:Song[] = res['content'];
      for (let song of foundSongs) {
        this.songs.push(song);
      }

      //change the selected song
      this.currentlySelectedListItem = null;

      //update the total number of list elements
      this.totalListSize = res['totalElements'];
    })
  }

  getSongsByAverageRankings() {
    if (this.currentRankingType == "averageScore") {
      //since we're already looking at the average score, clicking this button will change the direction of the ordering
      //as well as flipping the appropriate arrow.

      //Then, change the direction variable
      this.averageRankingsDirection = (this.averageRankingsDirection == "asc") ? "desc" : "asc";
      this.averageRankingsDirectionImage = (this.averageRankingsDirectionImage == "assets/down_arrow.png") ? "assets/up_arrow.png" : "assets/down_arrow.png";
    }
    else {
      //we're currently looking at the overall score so clicking the average rankings button shouldn't swap the 
      //direction of the order.
      this.currentRankingType = "averageScore";
    }


    //First, delete the current song array
    this.songs = [];

    //reset the pagination page number
    this.pageNumber = 0;

    //Finally, make sure that the numbers in the list are going in the right direction
    this.listStart = (this.averageRankingsDirection == "asc") ? 1: this.totalListSize;
    this.flip = (this.averageRankingsDirection == "asc") ? -1 : 1;

    //and then perform the backend call
    this.backendService.getPaginatedSongsByRank(this.pageNumber, this.pageSize * this.maximumPages, this.currentRankingType, this.averageRankingsDirection).subscribe(res => {
      //this function gives us a Java page object, for now we really only want the 'content' portion of it.
      let foundSongs:Song[] = res['content'];
      for (let song of foundSongs) {
        this.songs.push(song);
      }

      //change the selected song
      this.currentlySelectedListItem = null;

      //update the total number of list elements
      this.totalListSize = res['totalElements'];
    })
  }

  listScrollEventHandler(event:Event) {
    if (this.scrollEventComplete) {
      let itemList = event.target as Element;

      console.log(itemList.scrollTop);

      //we want to set off the next paginated data request when we get close to the
      //bottom of the list so that scrolling seems seemless. To do this we need to
      //get the height value of the list items.
      let listItem = itemList.children.item(0) as HTMLElement;
      let itemHeight = listItem.offsetHeight

      //I'm trying out a new kind of pagination here (for me). Instead of continuosly adding to the bottom of the list as we scroll down,
      //I'm going to show a maximum number of pages in the list box. As we scroll down, every new page loaded will cause the 
      //earliest page in the list to dissapear, and likewise, when we scroll up we will grab earlier pages and erase the last
      //page in the box.
      if (itemList.scrollTop + itemList.clientHeight + this.paginationPadding * itemHeight >= itemList.scrollHeight) {
        //we're near the bottom of our current list, grab another page in the positive direction (unless we alread have the final page number loaded)
        if (this.pageNumber != (this.totalPages - this.maximumPages + 1)) {
          this.scrollEventComplete = false; //this is to prevent multiple loads in quick succession
          let scrollDirection:string = (this.currentRankingType == "averageScore") ? this.averageRankingsDirection : this.overallRankingsDirection;

          //increment the current pagination page and make call to the back end for the next appropriate page
          this.backendService.getPaginatedSongsByRank((this.pageNumber++) + this.maximumPages, this.pageSize, this.currentRankingType, scrollDirection).subscribe(res => {
            //this function gives us a Java page object, for now we really only want the 'content' portion of it.
            let foundSongs:Song[] = res['content'];
            this.listStart -= this.pageSize * this.flip;
            this.songs.splice(0, this.pageSize); //slice a page worth of items from the front of the array
            this.songs = this.songs.concat(foundSongs); //add the new items to the end of the array
            
          });

          this.scrollEventComplete = true;
        }
      }
      else if (itemList.scrollTop <= this.paginationPadding * itemHeight) {
        //we're near the top of our current list, grab another page in the negative direction (unless we already have page 0 loaded)
        if (this.pageNumber != 0) {
          this.scrollEventComplete = false; //this is to prevent multiple loads in quick succession
          let scrollDirection:string = (this.currentRankingType == "averageScore") ? this.averageRankingsDirection : this.overallRankingsDirection;

          //increment the current pagination page and make call to the back end for next data set
          this.backendService.getPaginatedSongsByRank(--this.pageNumber, this.pageSize, this.currentRankingType, scrollDirection).subscribe(res => {
            //this function gives us a Java page object, for now we really only want the 'content' portion of it.
            let foundSongs:Song[] = res['content'];
            this.songs.splice(this.songs.length - this.pageSize); //slice a page worth of items from the end of the array
            this.listStart += this.pageSize * this.flip;
            this.songs = foundSongs.concat(this.songs);
          });

          this.scrollEventComplete = true;
        }
      }
    }
  }
}
