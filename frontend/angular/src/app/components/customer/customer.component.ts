import {Component, OnInit} from '@angular/core';
import {MenuBarComponent} from '../menu-bar/menu-bar.component';
import {HeaderBarComponent} from '../header-bar/header-bar.component';
import {ButtonDirective} from 'primeng/button';
import {SidebarModule} from 'primeng/sidebar';
import {ManageCustomerComponent} from '../manage-customer/manage-customer.component';
import {CustomerDTO} from '../../models/customer-dto';
import {CustomerService} from '../../services/customer/customer.service';
import {NgForOf} from '@angular/common';
import {CustomerCardComponent} from '../customer-card/customer-card.component';
import {CustomerRegistrationRequest} from '../../models/customer-registration-request';
import {ToastModule} from 'primeng/toast';
import {ConfirmationService, MessageService} from 'primeng/api';
import {ConfirmDialogModule} from 'primeng/confirmdialog';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [
    MenuBarComponent,
    HeaderBarComponent,
    ButtonDirective,
    SidebarModule,
    ManageCustomerComponent,
    NgForOf,
    CustomerCardComponent,
    ToastModule,
    ConfirmDialogModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.scss'
})
export class CustomerComponent implements OnInit {
  display: boolean = false;
  customers: CustomerDTO[] = [];
  customer: CustomerRegistrationRequest = {}
  operation: 'create' | 'update' | 'register' = 'create';

  constructor(
    private customerService: CustomerService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {
  }

  ngOnInit(): void {
    this.getAllCustomers()
  }

  private getAllCustomers(): void {
    this.customerService.getAllCustomers().subscribe({
      next: (data: CustomerDTO[]) => {
        this.customers = data;
        this.display = false;
      }
    })
  }

  save(customer: CustomerRegistrationRequest) {
    if (customer) {
      if (this.operation === 'create') {
        this.customerService.registerCustomer(customer).subscribe({
          next: () => {
            this.getAllCustomers()
            this.customer = {}
            this.messageService.add({
              severity: 'success',
              summary: 'Customer saved',
              detail: `Customer ${customer.name} was successfully saved.`,
            });
          }
        })
      } else if (this.operation === 'update') {
        this.customerService.updateCustomer(customer.id, customer).subscribe({
          next: () => {
            this.getAllCustomers()
            this.customer = {}
            this.messageService.add({
              severity: 'success',
              summary: 'Customer updated',
              detail: `Customer ${customer.name} was successfully updated.`,
            });
          }
        })
      }
    }
  }

  delete(customer: CustomerDTO) {
    if (customer) {
      this.confirmationService.confirm({
        message: `Are you sure you want to delete ${this.customer.name}? You can\'t undo this action afterwards.`,
        header: 'Delete Customer',
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: 'Delete',
        acceptButtonStyleClass:"p-button-danger p-button-rounded",
        rejectButtonStyleClass:"p-button-outlined p-button-rounded",
        acceptIcon:"none",
        rejectIcon:"none",

        accept: () => {
          this.customerService.deleteCustomer(customer.id)
            .subscribe({
              next: () => {
                this.getAllCustomers()
                this.messageService.add({
                  severity: 'success',
                  summary: 'Customer deleted',
                  detail: `Customer ${customer.name} was successfully deleted.`,
                });
              }
            })
        }
      })
    }
  }

  update(customer: CustomerDTO) {
    this.display = true;
    this.customer = customer;
    this.operation = 'update';
  }

  createCustomer() {
    this.display = true;
    this.customer = {}
    this.operation = 'create';
  }

  cancel() {
    this.display = false;
    this.customer = {}
    this.operation = 'create';
  }
}
