import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Anunt } from '../models/anunt.model';

@Injectable({
  providedIn: 'root'
})
export class AnuntService {
  private apiUrl = 'http://localhost:8080/api/anunturi';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${localStorage.getItem('token')}`
    });
  }

  getAnunturi(): Observable<Anunt[]> {
    return this.http.get<Anunt[]>(this.apiUrl, {
      headers: this.getAuthHeaders()
    });
  }

  adaugaAnunt(anunt: Anunt): Observable<Anunt> {
    return this.http.post<Anunt>(this.apiUrl, anunt, {
      headers: this.getAuthHeaders()
    });
  }

  deleteAnunt(id: number): Observable<void> {
    return this.http.delete<void>(`http://localhost:8080/api/anunturi/${id}`);
  }
  
  actualizeazaAnunt(anunt: Anunt): Observable<Anunt> {
    return this.http.put<Anunt>(`${this.apiUrl}/${anunt.id}`, anunt);
  }


}
