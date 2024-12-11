import {Component, Input} from '@angular/core';
import {ToastModule} from 'primeng/toast';
import {FileUploadHandlerEvent, FileUploadModule} from 'primeng/fileupload';
import {MessageService} from 'primeng/api';
import {BadgeModule} from 'primeng/badge';
import {CustomerService} from '../../services/customer/customer.service';

@Component({
  selector: 'app-upload-picture',
  standalone: true,
  imports: [
    ToastModule,
    FileUploadModule,
    BadgeModule
  ],
  providers: [MessageService],
  templateUrl: './upload-picture.component.html',
  styleUrl: './upload-picture.component.scss'
})
export class UploadPictureComponent {

  @Input()
  customerId: number | undefined = 0;

  formData: FormData = new FormData();

  constructor(
    private messageService: MessageService,
    private customerService: CustomerService
  ) {}

  onUpload(event: FileUploadHandlerEvent) {
    const file =  event.files.at(0)
    if (file) {
      this.formData.append('file', file); // 'file' is the expected key by your backend
    }

    this.customerService.uploadCustomerProfilePicture(this.customerId, this.formData).subscribe({
      next: () => {
        this.messageService.add({severity: 'info', summary: 'File Uploaded', detail: ''});
      },
      error: () => {
        this.messageService.add({severity: 'danger', summary: 'Failed to upload File', detail: ''})
      }
    })

  }
}
