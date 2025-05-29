import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-student',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-student.component.html',
})
export class AddStudentComponent {
  student = {
    nume: '',
    email: '',
    grupa: '',
    facultate: '',
    parola: '',
    an: ''
  };

  constructor(private http: HttpClient) {}

  onSubmit() {
    this.http.post('http://localhost:8080/api/students', this.student).subscribe({
      next: () => alert('Student adăugat cu succes!'),
      error: () => alert('Eroare la adăugare.')
    });
  }
}
