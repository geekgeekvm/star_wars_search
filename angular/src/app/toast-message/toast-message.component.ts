import { Component } from '@angular/core';
import { ToastService } from '../service/toast.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-toast-message',
  templateUrl: './toast-message.component.html',
  styleUrl: './toast-message.component.scss'
})
export class ToastMessageComponent {

  message: string = '';
  show: boolean = false;
  subscription!: Subscription;

  constructor(private toastService: ToastService) {}

  ngOnInit() {
    this.subscription = this.toastService.toastState$.subscribe(message => {
      this.message = message;
      this.show = true;
      setTimeout(() => this.show = false, 3000); // Hide after 3 seconds
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

}
