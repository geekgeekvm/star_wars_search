import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-toggle-switch',
  templateUrl: './toggle-switch.component.html',
  styleUrl: './toggle-switch.component.scss'
})
export class ToggleSwitchComponent {
  isOffline = false;

  @Output() toggleChange = new EventEmitter<boolean>();

  // Function to toggle and emit the new state
  toggle() {
    this.isOffline = !this.isOffline;
    this.toggleChange.emit(this.isOffline);
  }
}
