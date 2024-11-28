import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CardModule} from 'primeng/card';
import {BadgeModule} from 'primeng/badge';
import {ButtonDirective} from 'primeng/button';
import {CustomerDTO} from '../../models/customer-dto';
import {ConfirmationService} from 'primeng/api';
import {ConfirmDialogModule} from 'primeng/confirmdialog';

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
    private confirmationService: ConfirmationService
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

  get customerImage(): string {
    const gender = this.customer.gender === 'MALE' ? 'men' : 'women';
    return `https://randomuser.me/api/portraits/${gender}/${this.customerIndex}.jpg`
  }

  onDelete() {
    this.delete.emit(this.customer);
  }

  onUpdate() {
    this.update.emit(this.customer);
  }

}
