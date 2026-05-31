import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Nota } from '../models/nota.model';

@Injectable({
  providedIn: 'root'
})
export class NotaService {
  private apiUrl = 'https://localhost:8443/api/note';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${localStorage.getItem('token')}`
    });
  }

  getNote(): Observable<Nota[]> {
    return this.http.get<Nota[]>(this.apiUrl, {
      headers: this.getAuthHeaders()
    });
  }

  adaugaNota(nota: Nota): Observable<Nota> {
    return this.http.post<Nota>(this.apiUrl, nota, {
      headers: this.getAuthHeaders()
    });
  }

  getNoteByEmail(email: string): Observable<Nota[]> {
    return this.http.get<Nota[]>(`${this.apiUrl}/student/${email}`, {
      headers: this.getAuthHeaders()
    });
  }

  getNoteProfesor(email: string, options?: { headers: HttpHeaders }): Observable<Nota[]> {
    return this.http.get<Nota[]>(`${this.apiUrl}/profesor/${email}`, options);
  }

stergeNota(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/${id}`, {
    headers: this.getAuthHeaders()
  });
}

}
