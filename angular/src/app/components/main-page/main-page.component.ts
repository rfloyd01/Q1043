import { Component, OnInit, ViewChild } from '@angular/core';
import { Album } from 'src/app/models/album';
import { Artist } from 'src/app/models/artist';
import { Song } from 'src/app/models/song';
import { Chart, ChartConfiguration, ChartEvent, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

import { default as Annotation } from 'chartjs-plugin-annotation'
import { BackendServiceService } from 'src/app/services/backend-service.service';

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
  selectedSong!:Song;
  currentDataType:string = "song";

  //Pagination variables
  pageNumber:number = 0;
  pageSize:number = 20;
  totalListSize:number = 0;

  overallRankingsDirection:string = "asc";
  overallRankingsDirectionImage:string = "assets/down_arrow.png";
  averageRankingsDirection:string = "asc";
  currentRankingType:string = "overallScore";

  //testSongs:Song[] = [];
  songs:Song[] = [];
  listStart:number = 1;
  flip:number = -1;

  ngOnInit(): void {
    //this.addTestSongs();
    this.backendService.getPaginatedSongsByRank(this.pageNumber, this.pageSize, "overallScore").subscribe(res => {
      //this function gives us a Java page object, for now we really only want the 'content' portion of it.
      let foundSongs:Song[] = res['content'];
      for (let song of foundSongs) {
        this.songs.push(song);
        console.log(song);
      }

      this.totalListSize = res['totalElements'];
    })
  }

  buttonClicked(value:string) {
    this.currentDisplay = value;
  }

  listItemSelected(song:Song) {
    //first update the selected song
    this.selectedSong = song;
    this.lineChartData = {
      datasets: [
        {
          data: this.selectedSong.rankings,
          label: 'Rank by Year',
          backgroundColor: 'rgba(148,159,177,0.2)',
          borderColor: 'rgba(148,159,177,1)',
          pointBackgroundColor: 'rgba(148,159,177,1)',
          pointBorderColor: '#fff',
          pointHoverBackgroundColor: '#fff',
          pointHoverBorderColor: 'rgba(148,159,177,0.8)',
          fill: 'origin',
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
      'y-axis-0': {
        position: 'left',
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
      
      //First, delete the current song array
      this.songs = [];

      //Then, change the direction variable
      this.overallRankingsDirection = (this.overallRankingsDirection == "asc") ? "desc" : "asc";
      this.overallRankingsDirectionImage = (this.overallRankingsDirectionImage == "assets/down_arrow.png") ? "assets/up_arrow.png" : "assets/down_arrow.png";
      this.listStart = (this.listStart == 1) ? this.totalListSize : 1;
      this.flip *= -1;
    }
    else {
      //we're currently looking at the average score so clicking the overall rankings button shouldn't swap the 
      //direction of the order.
      this.currentRankingType = "overallScore";
    }

    //reset the pagination page number
    this.pageNumber = 0;

    //and then perform the backend call
    this.backendService.getPaginatedSongsByRank(this.pageNumber, this.pageSize, this.currentRankingType, this.overallRankingsDirection).subscribe(res => {
      //this function gives us a Java page object, for now we really only want the 'content' portion of it.
      let foundSongs:Song[] = res['content'];
      for (let song of foundSongs) {
        this.songs.push(song);
        console.log(song);
      }

      //change the selected song
      this.selectedSong = this.songs[0];
      
    })
  }

}
