import {Component, Input, Output, EventEmitter} from '@angular/core';
import {InputTextModule} from 'primeng/inputtext';
import {ButtonDirective} from 'primeng/button';
import {CustomerRegistrationRequest} from '../../models/customer-registration-request';
import {FormsModule} from '@angular/forms';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-manage-customer',
  standalone: true,
  imports: [
    InputTextModule,
    ButtonDirective,
    FormsModule,
    NgIf
  ],
  templateUrl: './manage-customer.component.html',
  styleUrl: './manage-customer.component.scss'
})
export class ManageCustomerComponent {

  @Input()
  customer: CustomerRegistrationRequest = {}
  @Input()
  operation: 'create'|'update' | 'register' = 'create';

  @Output()
  submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>();
  @Output()
  cancel: EventEmitter<void> = new EventEmitter<void>();
  @Output()
  login: EventEmitter<void> = new EventEmitter<void>();

  get isCustomerValid(): boolean {
    return this.isValid(this.customer.name) &&
      this.isValid(this.customer.email) && this.isValidEmail(<string>this.customer.email) &&
      this.customer.age !== null && this.customer.age !== undefined && this.customer.age > 0 &&
      (
        this.operation === 'update' ||
        this.isValid(this.customer.gender) &&
        this.isValid(this.customer.password)
      )
  }
  private isValid(input: string | undefined ): boolean {
    return input !== null && input !== undefined && input.length > 0;
  }

  private isValidEmail(email: string ): boolean {
    const regexp = new RegExp("[a-zA-Z0-9.*%±]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}");
    return regexp.test(email);
  }

  onSubmit() {
    this.submit.emit(this.customer);
  }

  onCancel() {
    this.cancel.emit();
  }

  onLogin() {
    this.login.emit();
  }
}
