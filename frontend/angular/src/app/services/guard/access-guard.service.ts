import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  GuardResult,
  MaybeAsync,
  Router,
  RouterStateSnapshot
} from '@angular/router';
import {AuthenticationResponse} from '../../models/authentication-response';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class AccessGuardService implements CanActivate {

  constructor(
    private router: Router,
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): MaybeAsync<GuardResult> {
    const storedUser = localStorage.getItem('user');
    if (!storedUser) {
      this.router.navigate(['/login']);
      return false;
    }
    const authResponse: AuthenticationResponse = JSON.parse(storedUser);
    const token = authResponse.token;
    if (!token) {
      this.router.navigate(['/login']);
      return false;
    }
    const jwtHelper = new JwtHelperService();
    try {
      const isTokenExpired = jwtHelper.isTokenExpired(token);
      if (isTokenExpired) {
        this.router.navigate(['/login']);
        return false;
      }
      return true;
    } catch (error) {
      this.router.navigate(['/login']);
      return false;
    }
  }
}
