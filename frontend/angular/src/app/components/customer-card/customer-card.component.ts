import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CardModule} from 'primeng/card';
import {BadgeModule} from 'primeng/badge';
import {ButtonDirective} from 'primeng/button';
import {CustomerDTO} from '../../models/customer-dto';
import {ConfirmationService} from 'primeng/api';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {CustomerService} from '../../services/customer/customer.service';

@Component({
  selector: 'app-customer-card',
  standalone: true,
  imports: [
    CardModule,
    BadgeModule,
    ButtonDirective,
    ConfirmDialogModule
  ],
  providers: [ConfirmationService],
  templateUrl: './customer-card.component.html',
  styleUrl: './customer-card.component.scss'
})
export class CustomerCardComponent {

  constructor(
    private confirmationService: ConfirmationService,
    private customerService: CustomerService
  ) {
  }

  @Input()
  customer: CustomerDTO = {}
  @Input()
  customerIndex: number = 0;
  @Input()
  customerGender: 'MALE' | 'FEMALE' = 'MALE';
  @Output()
  delete: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();
  @Output()
  update: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();

  get profileImage(): string {
    return this.customerService.getCustomerProfilePicture(this.customer.id)
  }

  onDelete() {
    this.delete.emit(this.customer);
  }

  onUpdate() {
    this.update.emit(this.customer);
  }

}
