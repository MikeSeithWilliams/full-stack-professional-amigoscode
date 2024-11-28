import { Component } from '@angular/core';
import {AvatarModule} from 'primeng/avatar';
import {InputTextModule} from 'primeng/inputtext';
import {MessageModule} from 'primeng/message';
import {PaginatorModule} from 'primeng/paginator';
import {ManageCustomerComponent} from '../manage-customer/manage-customer.component';
import {CustomerRegistrationRequest} from '../../models/customer-registration-request';
import {Router} from '@angular/router';
import {CustomerService} from '../../services/customer/customer.service';
import {NgIf} from '@angular/common';
import {AuthenticationRequest} from '../../models/authentication-request';
import {AuthenticationService} from '../../services/authentication/authentication.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    AvatarModule,
    InputTextModule,
    MessageModule,
    PaginatorModule,
    ManageCustomerComponent,
    NgIf
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  operation: 'create' | 'update' | 'register' = 'register';
  errorMsg = ''

  constructor(
    private authenticationService: AuthenticationService,
    private customerService: CustomerService,
    private router: Router
  ) {
  }

  createAccount(customer: CustomerRegistrationRequest) {
    if (customer) {
      this.customerService.registerCustomer(customer).subscribe({
        next: () => {
          const authRequest : AuthenticationRequest = {
            username: customer.email,
            password: customer.password,
          }
          this.authenticationService.login(authRequest).subscribe({
            next: (authenticationResponse) => {
              this.router.navigate(['/customers']);
              localStorage.setItem('user', JSON.stringify(authenticationResponse));
            }
          })
        },
        error: (err) => {
          if (err.status === 500) {
            this.errorMsg = 'Email is already in taken';
          }
        }
      })
    }
  }

  login() {
    this.router.navigate(['/login']);
  }
}
