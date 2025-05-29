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



@Component({
  selector: 'app-dashboard-student',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-student.component.html',
  styleUrls: ['./dashboard-student.component.css']
})
export class DashboardStudentComponent implements OnInit {
  studentEmail: string = 'iustinistoc1@gmail.com'; 
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
    private anuntService: AnuntService 
  ) {}

  ngOnInit(): void {
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
}
