import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Profesor {
  nume: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfesorService {

  private apiUrl = 'http://localhost:8080/api/profesori';

  constructor(private http: HttpClient) {}

  getProfesorInfo(email: string): Observable<Profesor> {
    return this.http.get<Profesor>(`${this.apiUrl}/${email}`);
  }
}
