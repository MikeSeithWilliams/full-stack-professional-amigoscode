import { Routes } from '@angular/router';
import {CustomerComponent} from './components/customer/customer.component';
import {LoginComponent} from './components/login/login.component';
import {AccessGuardService} from './services/guard/access-guard.service';
import {RegisterComponent} from './components/register/register.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full',
  },
  {
    path: 'customers',
    component: CustomerComponent,
    canActivate: [AccessGuardService]
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  }
];
