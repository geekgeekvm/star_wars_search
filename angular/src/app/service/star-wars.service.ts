import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StarWarsService {
  private apiUrl = 'http://localhost:8080/search';

  constructor(private http: HttpClient) {}

  searchFilms(name: string, type: string, isOffline: boolean): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}?type=${type}&name=${name}&isOffline=${isOffline}`);
  }
}