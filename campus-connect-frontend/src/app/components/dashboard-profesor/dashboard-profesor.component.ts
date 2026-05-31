import { Component, OnInit } from '@angular/core';
import { NotaService } from '../../services/nota.service';
import { AnuntService } from '../../services/anunt.service';
import { Nota } from '../../models/nota.model';
import { Anunt } from '../../models/anunt.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { OrarService } from '../../services/orar.service';
import { Orar } from '../../models/orar.model';
import { ProfesorService } from '../../services/profesor.service';

@Component({
  selector: 'app-dashboard-profesor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard-profesor.component.html',
  styleUrls: ['./dashboard-profesor.component.css']
})
export class DashboardProfesorComponent implements OnInit {
  note: Nota[] = [];
  notaNoua: Nota = { studentEmail: '', materie: '', valoare: 0 };
  ore: Orar[] = [];
  anunturi: Anunt[] = [];
  editOraId: number | null = null;
  editAnuntId: number | null = null;

  loading = false;
  profesorNume: string = '';
  email: string = '';

  anuntNou: Anunt = {
    titlu: '',
    continut: '',
    data: '',
    profesorEmail: 'prof@email.com' // actualizat automat la submit
  };

  oraNoua: any = {
    zi: '',
    oraInceput: '',
    oraSfarsit: '',
    an: 1,
    grupa: 1,
    materieId: 0,
    specializareId: 0
  };

  oraEdit: any = {
    id: 0,
    zi: '',
    oraInceput: '',
    oraSfarsit: '',
    an: 1,
    grupa: 1
  };

  anuntEdit: Anunt = {
    id: 0,
    titlu: '',
    continut: '',
    data: '',
    profesorEmail: '',
    profesorNume: ''
  };

  constructor(
    private notaService: NotaService,
    private anuntService: AnuntService,
    private router: Router,
    private orarService: OrarService,
    private profesorService: ProfesorService
  ) {}

ngOnInit(): void {
  this.email = localStorage.getItem('email') || '';
  if (this.email) {
    this.profesorService.getProfesorInfo(this.email).subscribe({
      next: (profesor) => {
        this.profesorNume = profesor.nume;
        this.email = profesor.email;

        this.notaService.getNoteProfesor(this.email).subscribe({
          next: (data) => {
            this.note = data;
          },
          error: (err) => {
            console.error('Eroare la preluare note', err);
          }
        });
      },
      error: (err) => {
        console.error('Eroare la încărcarea info profesor', err);
      }
    });
  } else {
    console.error('Email profesor negasit în localStorage');
  }

  this.anuntService.getAnunturi().subscribe({
    next: (data) => this.anunturi = data,
    error: (err) => console.error('Eroare la preluare anunțuri', err)
  });

  this.orarService.getOreProfesor().subscribe({
    next: (data) => this.ore = data,
    error: (err) => console.error('Eroare la preluare orar', err)
  });
}

  adaugaNota(): void {
    this.notaService.adaugaNota(this.notaNoua).subscribe({
      next: () => {
        this.notaNoua = { studentEmail: '', materie: '', valoare: 0 };
        this.ngOnInit();
      },
      error: (err) => console.error('Eroare la adăugare notă', err)
    });
  }

  stergeNota(id: number): void {
    this.notaService.stergeNota(id).subscribe({
      next: () => {
        this.note = this.note.filter(n => n.id !== id);
      },
      error: (err) => {
        console.error('Eroare la ștergere notă', err);
      }
    });
  }

  adaugaAnunt(): void {
    this.anuntNou.data = new Date().toISOString().split('T')[0];

    this.anuntService.adaugaAnunt(this.anuntNou).subscribe({
      next: () => {
        this.anuntNou = {
          titlu: '',
          continut: '',
          data: '',
          profesorEmail: this.anuntNou.profesorEmail
        };
        this.ngOnInit();
      },
      error: (err) => console.error('Eroare la adăugare anunț', err)
    });
  }

  stergeAnunt(id: number): void {
    if (!id) {
      console.error('ID invalid pentru anunț');
      return;
    }

    if (confirm('Sigur vrei să ștergi acest anunț?')) {
      this.anuntService.deleteAnunt(id).subscribe({
        next: () => {
          this.anunturi = this.anunturi.filter(a => a.id !== id);
          this.loading = false;
        },
        error: (err) => {
          console.error('Eroare la ștergerea anunțului', err);
        }
      });
    }
  }

  stergeOra(id: number): void {
    this.orarService.stergeOra(id).subscribe({
      next: () => {
        this.ore = this.ore.filter(o => o.id !== id);
      },
      error: (err) => {
        console.error('Eroare la ștergere oră', err);
      }
    });
  }

  editeazaOra(ora: any): void {
    this.editOraId = ora.id;
    this.oraEdit = { ...ora };
  }

  salveazaEditareOra(): void {
    this.orarService.actualizeazaOra(this.oraEdit).subscribe({
      next: () => {
        this.editOraId = null;
        this.ngOnInit();
      },
      error: (err) => console.error('Eroare la actualizare oră', err)
    });
  }

  anuleazaEditareOra(): void {
    this.editOraId = null;
  }

  editeazaAnunt(a: Anunt): void {
    this.editAnuntId = a.id!;
    this.anuntEdit = { ...a };
  }

  anuleazaEditareAnunt(): void {
    this.editAnuntId = null;
  }

  salveazaEditareAnunt(): void {
    this.anuntService.actualizeazaAnunt(this.anuntEdit).subscribe({
      next: () => {
        this.editAnuntId = null;
        this.ngOnInit();
      },
      error: (err) => console.error('Eroare la actualizarea anunțului', err)
    });
  }

  adaugaOra(): void {
    fetch('https://localhost:8443/api/profesori/user-info', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
      .then(res => {
        if (!res.ok) throw new Error('Autentificare eșuată');
        return res.json();
      })
      .then(prof => {
        this.oraNoua.materieId = prof.materie.id;
        this.oraNoua.specializareId = prof.specializare.id;

        return fetch('https://localhost:8443/api/orar', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
          body: JSON.stringify(this.oraNoua)
        });
      })
      .then(res => {
        if (res.ok) {
          alert('Ora adăugată cu succes!');
          this.oraNoua = {
            zi: '',
            oraInceput: '',
            oraSfarsit: '',
            an: 1,
            grupa: 1,
            materieId: 0,
            specializareId: 0
          };
          return;
        } else {
          return res.text().then(msg => alert('Eroare: ' + msg));
        }
      })
      .catch(err => {
        console.error('Eroare la obținerea profesorului:', err);
        alert('Eroare la autentificare. Reîncearcă.');
      });
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/']);
  }
}
