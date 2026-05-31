import { Component, OnInit } from '@angular/core';
import { NotaService } from '../../services/nota.service';
import { OrarService } from '../../services/orar.service';
import { Nota } from '../../models/nota.model';
import { Orar } from '../../models/orar.model';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Anunt } from '../../models/anunt.model';
import { AnuntService } from '../../services/anunt.service';
import { StudentService } from '../../services/student.service';
import { ChatbotComponent } from '../chatbot/chatbot.component';
import { SecurityService } from '../../services/security.service';


@Component({
  selector: 'app-dashboard-student',
  standalone: true,
  imports: [CommonModule,ChatbotComponent],
  templateUrl: './dashboard-student.component.html',
  styleUrls: ['./dashboard-student.component.css']
})
export class DashboardStudentComponent implements OnInit {
  studentEmail: string = ''; 
  note: Nota[] = [];
  orar: Orar[] = [];
  anunturi: Anunt[] = [];
  loading = false;
  studentNume: string = '';


  constructor(
    private notaService: NotaService,
    private orarService: OrarService,
    private studentService: StudentService,
    private router: Router,
    private anuntService: AnuntService,
    private sec: SecurityService 
  ) {}

  ngOnInit(): void {
  this.studentEmail = localStorage.getItem('email') || '';
  this.loading = true;
  let completedCalls = 0;
  const totalCalls = 3;
  
  const checkLoadingComplete = () => {
    completedCalls++;
    if (completedCalls === totalCalls) {
      this.loading = false;
    }
  };
  
  this.studentService.getStudentInfo(this.studentEmail).subscribe({
    next: (data) => {
      this.studentNume = data.nume;  // sau cum se numește câmpul
      checkLoadingComplete();
    },
    error: (err) => {
      console.error('Eroare la preluarea info student', err);
      checkLoadingComplete();
    }
  });

  this.notaService.getNote().subscribe({
    next: (data) => {
      this.note = data;
      checkLoadingComplete();
    },
    error: (err) => {
      console.error('Eroare la preluarea notelor', err);
      checkLoadingComplete();
    }
  });

  this.orarService.getOrarStudent().subscribe({
    next: (data) => {
      this.orar = data;
      checkLoadingComplete();
    },
    error: (err) => {
      console.error('Eroare la preluarea orarului', err);
      checkLoadingComplete();
    }
  });

  this.anuntService.getAnunturi().subscribe({
    next: (data) => {
      this.anunturi = data;
      checkLoadingComplete();
    },
    error: (err) => {
      console.error('Eroare la preluarea anunțurilor', err);
      checkLoadingComplete();
    }
  });
}
logout(): void {
    localStorage.clear(); 
    this.router.navigate(['/']); 
  }
 async adaugaDispozitiv() {
    try {
      await this.sec.inregistreazaDispozitiv();
      alert('Dispozitiv înregistrat cu succes! Acum poți folosi FIDO2 la login.');
    } catch (e) {
      alert('Înregistrare anulată sau eșuată.');
      console.error(e);
    }
  }
   async genereazaCoduri() {
    try {
      const coduri = await this.sec.genereazaBackupCodes();
      alert('Coduri de rezervă (notează-le, fiecare se folosește o singură dată):\n\n' + coduri.join('\n'));
    } catch (e) {
      alert('Nu s-au putut genera codurile.');
      console.error(e);
    }
  }
}
