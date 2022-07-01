import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CallbackComponent } from './components/callback/callback.component';
import { LoginComponent } from './components/login/login.component';
import { MainPageComponent } from './components/main-page/main-page.component';
import { SongDisplayComponent } from './components/song-display/song-display.component';

const routes: Routes = [
  {path:'login', component:LoginComponent},
  {path:'', component:MainPageComponent},
  {path:'callback', component:CallbackComponent},
  {path:'songDisplayTest', component:SongDisplayComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
