import { Component ,OnInit} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  email = '';
  parola = '';
  rol = 'student';

  constructor(private http: HttpClient, private router: Router) {}

 
  ngOnInit(): void {
    localStorage.clear();
  }

  login() {
    const payload = { email: this.email, parola: this.parola, rol: this.rol };

    this.http.post<any>('http://localhost:8080/api/login', payload).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('email', response.email);
        localStorage.setItem('rol', response.rol);

        if (response.rol === 'student') {
            this.router.navigate(['/dashboard-student']);
        } else if (response.rol === 'profesor') {
            this.router.navigate(['/dashboard-profesor']);
        } else if (response.rol === 'admin') {
            this.router.navigate(['/dashboard-profesor']);
}
      },
      error: () => {
        alert('Autentificare eșuată.');
      }
    });
  }
}