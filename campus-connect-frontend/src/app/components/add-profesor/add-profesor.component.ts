import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-add-profesor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-profesor.component.html'
})
export class AddProfesorComponent {
  profesor = {
    nume: '',
    email: '',
    parola: '',
    specializare: ''
  };

  constructor(private http: HttpClient, private router: Router) {}

  adaugaProfesor(): void {
    this.http.post('http://localhost:8080/api/profesori', this.profesor).subscribe({
      next: () => {
        alert('Contul a fost creat cu succes!');
        this.router.navigate(['/']); // redirect spre login
      },
      error: (err) => {
        console.error('Eroare la înregistrare profesor:', err);
        alert('Eroare la creare cont.');
      }
    });
  }
}
