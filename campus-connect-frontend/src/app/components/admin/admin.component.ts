import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

interface AccessAlert {
  id: number;
  email: string;
  rol: string;
  motiv: string;
  ip: string;
  moment: string;
}

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  imports: [RouterModule, CommonModule],
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  isAdmin = false;
  alerte: AccessAlert[] = [];
  loading = false;

  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit(): void {
    const rol = localStorage.getItem('rol');
    if (rol === 'admin') {
      this.isAdmin = true;
      this.incarcaAlerte();
    } else {
      this.router.navigate(['/']);
    }
  }

  incarcaAlerte(): void {
    this.loading = true;
    this.http.get<AccessAlert[]>('https://localhost:8443/api/alerts').subscribe({
      next: (data) => {
        this.alerte = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Eroare la preluarea alertelor', err);
        this.loading = false;
      }
    });
  }

  /** Evidențiază tentativele repetate (posibil brute-force) pe același email. */
  esteCritica(alerta: AccessAlert): boolean {
    return this.alerte.filter(a => a.email === alerta.email).length >= 3;
  }
}