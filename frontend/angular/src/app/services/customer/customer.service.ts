import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CustomerDTO} from '../../models/customer-dto';
import {environment} from '../../../environments/environment';
import {CustomerRegistrationRequest} from '../../models/customer-registration-request';
import {CustomerUpdateRequest} from '../../models/customer-update-request';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly customersUrl = `${environment.api.baseUrl}/${environment.api.customerUrl}`

  constructor(
    private http: HttpClient
  ) { }

  getAllCustomers(): Observable<CustomerDTO[]> {
    return this.http.get<CustomerDTO[]>(this.customersUrl);
  }

  registerCustomer(customer: CustomerRegistrationRequest): Observable<void> {
    return this.http.post<void>(this.customersUrl, customer);
  }

  deleteCustomer(id: number | undefined): Observable<void> {
    return this.http.delete<void>(this.customersUrl + `/${id}`);
  }

  updateCustomer(id: number | undefined, customer: CustomerUpdateRequest): Observable<void> {
    return this.http.put<void>(this.customersUrl + `/${id}`, customer);
  }
}
