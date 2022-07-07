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
  descriptionHeader:string = "Top Songs"
  descriptionBody!:string;
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

  rankingsValue:number = 0; //this number can either be 0, or 1
  rankingsDirections:string[] = ["asc", "desc"];
  rankingsDirectionImages:string[] = ["assets/down_arrow.png", "assets/up_arrow.png"];

  currentRankingType:string = "overallScore";
  rankingTypes:string[] = ["overallScore", "averageScore", "mostSongs"];

  //testSongs:Song[] = [];
  data:any[] = [];
  listStart:number = 1;
  flip:number = -1;

  ngOnInit(): void {
    //when initially loading data, we grab a few pages at the same time for song data (denoted by the maximumPages variable)
    this.backendService.getPaginatedSongsByRank(this.pageNumber, this.maximumPages * this.pageSize, "overallScore").subscribe(res => {
      //this function gives us a Java page object, for now we really only want the 'content' portion of it.
      let foundSongs:Song[] = res['content'];
      for (let song of foundSongs) {
        this.data.push(song);
      }

      this.totalListSize = res['totalElements'];
      this.totalPages = res['totalPages'];
    })

    this.currentlySelectedListItem = null; //start off with no selection
    this.setDescription();
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
    if (this.currentDataType == 'song') {
      let rankingCopy:number[] = this.currentlySelectedListItem.data['rankings'];
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
    }
  }

  setDataType(dataType:string) {
    if (this.currentDataType == dataType) {
      //clicking the currently highlighted data type button has the effect
      //of swapping the order of the data
      this.rankingsValue = (this.rankingsValue + 1) % 2; //can only be 0 or 1
      this.flip *= -1;
    }

    this.changeDataTypeButtonColor(dataType);
    this.setDescription();
    this.currentDataType = dataType;

    //reset the jump to input box
    let jumpToInput = document.getElementById('jump-to') as HTMLInputElement;
    jumpToInput.value = "";

    //clear out any data currently in the data array and set current selection to null
    this.data = [];
    this.currentlySelectedListItem = null;

    if (dataType == "song") this.getSongs(0, this.maximumPages, "", this.listStart);
    else if (dataType == "album")  this.getAlbums(0, this.maximumPages, "", this.listStart);

  }

  setRankingType(rankingType:string) {
    if (this.currentRankingType == rankingType) return; //no need to change anything

    this.changeOrderingButtonColor(rankingType);
    this.currentRankingType = rankingType;

    //When changing the ranking type we always jump back to the 
    //beginning of the list, so we need to reset the listStart variable.
    this.listStart = (this.rankingsValue == 0) ? 1 : this.totalListSize;

    if (this.currentDataType == 'song') this.getSongs(0, this.maximumPages, "", this.listStart);
    else if (this.currentDataType == 'album') this.getAlbums(0, this.maximumPages, "", this.listStart);
  }

  jumpTo() {
    //This function give a way to quickly maneuver to a far away part of the list without scrolling.
    //It will get the appropriate pagination pages and put the jumpTo number in the middle of the
    //list.

    //First make sure that the jump to number is valid
    if (this.jumpToNumber < 1) this.jumpToNumber = 1;
    else if (this.jumpToNumber > this.totalListSize) this.jumpToNumber = this.totalListSize;

    let orderedJumpTo = (this.flip == -1) ? this.jumpToNumber : (this.totalListSize - this.jumpToNumber + 1);
    let requiredPage:number = Math.floor((orderedJumpTo - 1) / this.pageSize); //the page that our data item is on

    //Define the first page of the pages we'll be grabbing. The first page will be a number of pages before the required page.
    //When the maximum page number is even the middle will be counted as the first page PAST the center. So if there
    //were a maximum of 4 pages then the middle would be page 3 --> ((1. not middle), (2. not middle), (3. middle page), (4. not middle)).
    //We also need to make sure that the first page isn't less than 0, or less than a distance of this.maximumPages from the
    //final page

    //check for less than 0
    let firstPage:number = (requiredPage - (this.maximumPages / 2) < 0) ? 0 : requiredPage - (this.maximumPages / 2);

    //check for being too close to the final page
    let finalPage:number = this.totalListSize / this.pageSize;
    if (finalPage - this.maximumPages < firstPage) firstPage = finalPage - this.maximumPages;

    //this if statement executes if the maximum page number is odd
    if (this.maximumPages % 2 != 0) firstPage = (requiredPage - ((this.maximumPages - 1) / 2) < 0) ? 0 : requiredPage - ((this.maximumPages - 1) / 2);

    //Set the appropriate start number for the list
    if (this.flip == -1) this.listStart = firstPage * this.pageSize - this.flip;
    else this.listStart = this.totalListSize - firstPage * this.pageSize;
    
    if (this.currentDataType == 'song') this.getSongs(firstPage, this.maximumPages, "", this.listStart);
    else if (this.currentDataType == 'album') this.getAlbums(firstPage, this.maximumPages, "", this.listStart);
    
    //after adding all songs to the list, we force the list viewer to scroll to the correct location
    let list = document.getElementById('item-list') as HTMLElement;
    let listItem = list.firstChild as HTMLElement;
    let listItemHeight = listItem.offsetHeight;

    list.scrollTo({top: (orderedJumpTo - (firstPage * this.pageSize + 1)) * listItemHeight});
  }

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

  getSongs(firstPageNumber:number, totalPages:number, scrollDirection:string, listStart:number) {
    //With this function we can either grab a single new page, which will happen when we trigger the infinite scroll
    //event, or we can grab multiple pages, which will happen when the page is first load, we change the data source
    //or we use the jump to function.
    if (totalPages == 1) {
      //this means we've triggered the infinite scroll event, which requires different logic for scrolling upwards
      //and downwards. The scrollDirection variable indicates which direction we need to grab data in.
      this.backendService.getPaginatedSongsByRank(firstPageNumber, this.pageSize, this.currentRankingType, this.rankingsDirections[this.rankingsValue]).subscribe(res => {
        //this function gives us a Java page object, for now we really only want the 'content' portion of it.
        let foundSongs:Song[] = res['content'];
        if (foundSongs.length == 0) return; //we've reached the final page so there's no need to update
        this.listStart = listStart;

        if (scrollDirection == "down") {
          this.data.splice(0, this.pageSize); //slice a page worth of items from the front of the array
          this.data = this.data.concat(foundSongs); //add the new items to the end of the array
        }
        else {
          this.data.splice(this.data.length - this.pageSize); //slice a page worth of items from the end of the array
          this.data = foundSongs.concat(this.data);
        }
        this.scrollEventComplete = true;
      });
    }
    else {
      this.backendService.getMultiplePaginatedSongsByRank(firstPageNumber, this.pageSize, totalPages, this.currentRankingType, this.rankingsDirections[this.rankingsValue]).subscribe(res => {
        //We need to grab multiple pages which means that our current song list will need to be deleted.
        //and total list size reset (in case we switched data types).
        if (res[0]['content'].length == 0) return; //we've reached the final page so there's no need to update

        this.data = [];
        this.totalListSize = res[0]['totalElements'];

        if (this.flip == -1) this.listStart = firstPageNumber * this.pageSize - this.flip;
        else this.listStart = this.totalListSize - firstPageNumber * this.pageSize;

        this.pageNumber = firstPageNumber;
        for (let page of res) {
          this.data = this.data.concat(page['content']);
        }
      })
    }
  }

  getAlbums(firstPageNumber:number, totalPages:number, scrollDirection:string, listStart:number) {
    //Same basic idea as the getSongs() function above.
    if (totalPages == 1) {
      //This get's called when we've triggered the infinite scroll event of the list
      this.backendService.getPaginatedAlbumsByRank(firstPageNumber, this.pageSize, this.rankingsDirections[this.rankingsValue]).subscribe(res => {
        let foundAlbums:Album[] = res['content']; //this function gives us a Java 'Page' object
        this.totalListSize = res['totalElements'];
        this.listStart = listStart;
  
        if (scrollDirection == "down") {
          this.data.splice(0, this.pageSize); //slice a page worth of items from the front of the array
          this.data = this.data.concat(foundAlbums); //add the new items to the end of the array
        }
        else {
          this.data.splice(this.data.length - this.pageSize); //slice a page worth of items from the end of the array
          this.data = foundAlbums.concat(this.data);
        }
        this.scrollEventComplete = true;
      });
    }
    else {
      this.backendService.getMultiplePaginatedAlbumsByRank(firstPageNumber, this.pageSize, totalPages, this.rankingsDirections[this.rankingsValue]).subscribe(res => {
        //We need to grab multiple pages which means that our current song list will need to be deleted.
        //and total list size reset (in case we switched data types).
        this.data = [];
        this.totalListSize = res[0]['totalElements'];

        if (this.flip == -1) this.listStart = firstPageNumber * this.pageSize - this.flip;
        else this.listStart = this.totalListSize - firstPageNumber * this.pageSize;

        this.pageNumber = firstPageNumber;
        for (let page of res) {
          this.data = this.data.concat(page['content']);
        }
      })
    }
  }

  listScrollEventHandler(event:Event) {
    if (this.scrollEventComplete) {
      let itemList = event.target as Element;

      //console.log(itemList.scrollTop);

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

          //increment the current pagination page and make call to the back end for the next appropriate page
          if (this.currentDataType == 'song') this.getSongs((this.pageNumber++) + this.maximumPages, 1, "down", this.listStart - this.pageSize * this.flip);
          else if (this.currentDataType == 'album') this.getAlbums((this.pageNumber++) + this.maximumPages, 1, 'down', this.listStart - this.pageSize * this.flip);
        }
      }
      else if (itemList.scrollTop <= this.paginationPadding * itemHeight) {
        //we're near the top of our current list, grab another page in the negative direction (unless we already have page 0 loaded)
        if (this.pageNumber != 0) {
          this.scrollEventComplete = false; //this is to prevent multiple loads in quick succession

          //increment the current pagination page and make call to the back end for next data set
          if (this.currentDataType == 'song') this.getSongs(--this.pageNumber, 1, "up", this.listStart + this.pageSize * this.flip);
          else if (this.currentDataType == 'album') this.getAlbums(--this.pageNumber, 1, 'up', this.listStart + this.pageSize * this.flip);
        }
      }
    }
  }

  setDescription() {
    if (this.currentDataType == "song") {
      this.descriptionHeader = "Top Songs"
      this.descriptionBody = "This list combines all of the data from the individual years into a single place, all in all there are just under " +
      "2000 songs that appear on the lists over the various years. This combined list can be ordered in two different ways, by 'Average Score' and " +
      "by 'Overall Score'. The average score for a song is determined by taking the average rank of a song in every year that it made the list. The " +
      "overall score for a song is determined by dividing the average score by the number of years the song has actually made a list. The overall " +
      "rank is an attempt to reward songs that have been on the list more times. The buttons on the left can be used to change between data set and to " +
      "change the data from ascending to descending order. The text box can be used to jump to a specific song number. Click on a song for more information about it."
    }
    else if (this.currentDataType == "album") {
      this.descriptionHeader = "Top Albums";
      this.descriptionBody = "This list shows all of the different albums that have at least one of their songs appear on the combined song list. The ordering is " + 
      "the combination of two factors. First, what ratio of their songs are on the list? If an album with only 4 songs on it has all of its songs make the list " + 
      "then this should be considered better than an album with 15 songs where only 4 of them make the list. Second, what is the average rank of list songs on the album? " +
      "If there are two different albums that have 50% of their songs on the list, but the average rank of list songs on the first album is 100 while the second " +
      "album is 300, then the first album should be considered better."
    }
  }

  changeOrderingButtonColor(clickedButton:string) {
    if (clickedButton != this.currentRankingType) {
      //we only change the button color if a different button is pressed
      let buttonDiv = document.getElementById("data-ordering-buttons") as HTMLElement;

      let overallButton = buttonDiv.childNodes[0] as HTMLElement;
      let averageButton = buttonDiv.childNodes[1] as HTMLElement;

      overallButton.classList.replace('pressed-button', 'nonpressed-button');
      averageButton.classList.replace('pressed-button', 'nonpressed-button');

      if (clickedButton == "overallScore") overallButton.classList.replace('nonpressed-button','pressed-button');
      else averageButton.classList.replace('nonpressed-button','pressed-button');
    }
  }

  changeDataTypeButtonColor(dataType:string) {
    //we only change the button color if a different button is pressed
    if (dataType != this.currentDataType) {
      
      //changing of the button colors happens by altering the classes for the buttons
      let buttonDiv = document.getElementById("data-type-buttons") as HTMLElement;

      let songButton = buttonDiv.childNodes[0] as HTMLElement;
      let albumButton = buttonDiv.childNodes[1] as HTMLElement;
      let artistButton = buttonDiv.childNodes[2] as HTMLElement;
      let yearButton = buttonDiv.childNodes[3] as HTMLElement;

      songButton.classList.replace('pressed-button', 'nonpressed-button');
      albumButton.classList.replace('pressed-button', 'nonpressed-button');
      artistButton.classList.replace('pressed-button', 'nonpressed-button');
      yearButton.classList.replace('pressed-button', 'nonpressed-button');

      
      if (dataType == "song") songButton.classList.replace('nonpressed-button','pressed-button');
      else if (dataType == "album") albumButton.classList.replace('nonpressed-button','pressed-button');
      else if (dataType == "artist") artistButton.classList.replace('nonpressed-button','pressed-button');
      else if (dataType == "year") yearButton.classList.replace('nonpressed-button','pressed-button');
    }

  }
}
