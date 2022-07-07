import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpotifyTokenComponent } from './spotify-token.component';

describe('SpotifyTokenComponent', () => {
  let component: SpotifyTokenComponent;
  let fixture: ComponentFixture<SpotifyTokenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpotifyTokenComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpotifyTokenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
