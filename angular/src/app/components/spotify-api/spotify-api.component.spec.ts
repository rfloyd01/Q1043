import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpotifyAPIComponent } from './spotify-api.component';

describe('SpotifyAPIComponent', () => {
  let component: SpotifyAPIComponent;
  let fixture: ComponentFixture<SpotifyAPIComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpotifyAPIComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpotifyAPIComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
