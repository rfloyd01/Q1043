import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-side-navbar',
  templateUrl: './side-navbar.component.html',
  styleUrls: ['./side-navbar.component.css']
})
export class SideNavbarComponent implements OnInit {

  constructor() { }

  @Input() buttonList:string[] = [];
  @Output() buttonClickEvent = new EventEmitter<string>();

  ngOnInit(): void {
  }

  buttonClicked(value:string) {
    this.buttonClickEvent.emit(value);
  }

}
