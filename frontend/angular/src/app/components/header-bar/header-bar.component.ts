import { Component } from '@angular/core';
import {ButtonDirective} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {AvatarModule} from 'primeng/avatar';
import {MenuModule} from 'primeng/menu';
import {MenuItem, MenuItemCommandEvent} from 'primeng/api';
import {AuthenticationResponse} from '../../models/authentication-response';
import {CustomerDTO} from '../../models/customer-dto';
import {Router} from '@angular/router';

@Component({
  selector: 'app-header-bar',
  standalone: true,
  imports: [
    ButtonDirective,
    Ripple,
    AvatarModule,
    MenuModule
  ],
  providers: [Router],
  templateUrl: './header-bar.component.html',
  styleUrl: './header-bar.component.scss'
})
export class HeaderBarComponent {

  constructor(
    private router: Router
  ) {
  }

  items: MenuItem[] = [
    {
      label: 'Profile',
      icon: 'pi pi-user'
    },
    {
      label: 'Settings',
      icon: 'pi pi-cog'
    },
    {
      separator: true,
    },
    {
      label: 'Sign Out',
      icon: 'pi pi-sign-out',
      command: () => {
        localStorage.removeItem('user');
        //this.router.navigate(['/login']);
        window.location.href = '/login'; //  forces a full-page reload, because otherwise router does not work
      }
    }
  ];

  private getUser(): (CustomerDTO | undefined) {
    const storeUser = localStorage.getItem('user')
    if (storeUser) {
      const authResponse: AuthenticationResponse = JSON.parse(storeUser);
      return authResponse.customerDTO
    }
    return undefined;
  }

  get username(): string {
    const user = this.getUser()
    if (user !== undefined && user.username !== undefined) {
      return user.username
    }
    return '--';
  }

  get userRole(): string {
    const user = this.getUser()
    if (user !== undefined && user.roles !== undefined) {
      return user.roles[0]
    }
    return '--';
  }


}
