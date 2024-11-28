import { Component } from '@angular/core';
import {AvatarModule} from 'primeng/avatar';
import {InputTextModule} from 'primeng/inputtext';
import {ButtonDirective} from 'primeng/button';
import {AuthenticationRequest} from '../../models/authentication-request';
import {FormsModule} from '@angular/forms';
import {AuthenticationService} from '../../services/authentication/authentication.service';
import {MessageModule} from 'primeng/message';
import {NgIf} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    AvatarModule,
    InputTextModule,
    ButtonDirective,
    FormsModule,
    MessageModule,
    NgIf
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  authenticationRequest: AuthenticationRequest = {};
  errorMsg: string = '';

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {
  }
  login() {
    this.errorMsg = ''
    this.authenticationService.login(this.authenticationRequest)
      .subscribe({
        next: (authenticationResponse) => {
          this.router.navigate(['/customers']);
          localStorage.setItem('user', JSON.stringify(authenticationResponse));
        },
        error: (err) => {
          if (err.status === 401) {
            this.errorMsg = 'Email and / or Password is incorrect';
          }
        }
      })
  }

  register() {
    console.log('Register button clicked')
    this.router.navigate(['/register']);
  }
}
