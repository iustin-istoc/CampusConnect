import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student } from '../models/student.model';

export interface StudentInfo {
  nume: string;
  email: string;

}

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private baseUrl = 'https://localhost:8443/api/student'; 

  constructor(private http: HttpClient) { }

  getStudentInfo(email: string): Observable<Student> {
  return this.http.get<Student>(`https://localhost:8443/api/students/${email}`);
}
}