import { Component } from '@angular/core';
import { StarWarsService } from '../service/star-wars.service';
import { ToastService } from '../service/toast.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent {
  name: string = '';
  type: string = '';
  count: number = 0;
  films: string[] = [];
  isOffline = false;

  constructor(private starWarsService: StarWarsService, private toastService: ToastService) {}

  search() {
    if (this.name) {
      this.starWarsService.searchFilms(this.name, this.type, this.isOffline).subscribe(
        (data) => {
          console.log(data)
          if (data == null) {
            console.warn("Empty response.")
            this.triggerError('Error fetching data: ', 'entity not found');
          }
          this.count = data.count;
          this.films = data.films; 
        },
        (error) => {
          console.error('Error fetching data: ', error);
          this.triggerError('Error fetching data: ', error);
          this.count = 0;
          this.films = [];
        }
      );
    } else {
      this.count = 0;
      this.films = [];
    }
  }

  triggerError(errorMessage: string, errorText: any) {
    this.toastService.showError(errorMessage + errorText);
  }

  onToggleChange(isOffline: boolean) {
    this.isOffline = isOffline;
  }

}
