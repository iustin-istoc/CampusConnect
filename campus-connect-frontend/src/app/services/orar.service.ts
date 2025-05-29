import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Orar } from '../models/orar.model';

@Injectable({ providedIn: 'root' })
export class OrarService {
  private apiUrl = 'http://localhost:8080/api/orar';

  constructor(private http: HttpClient) {}

  getOreProfesor(): Observable<Orar[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    return this.http.get<Orar[]>(`${this.apiUrl}/profesor`, { headers });
  }

  getOrarStudent(): Observable<Orar[]> {
  return this.http.get<Orar[]>('http://localhost:8080/api/orar/student', {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
    }); 
  }

  stergeOra(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  actualizeazaOra(ora: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${ora.id}`, ora);
  }
}
