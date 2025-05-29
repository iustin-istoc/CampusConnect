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

  private baseUrl = 'http://localhost:8080/api/student'; 

  constructor(private http: HttpClient) { }

  getStudentInfo(email: string): Observable<Student> {
  return this.http.get<Student>(`http://localhost:8080/api/students/${email}`);
}
}