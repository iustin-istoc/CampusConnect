import { Routes } from '@angular/router';
import { AddStudentComponent } from './components/add-student/add-student.component';
import { LoginComponent } from './components/login/login.component';
import { DashboardStudentComponent } from './components/dashboard-student/dashboard-student.component';
import { DashboardProfesorComponent } from './components/dashboard-profesor/dashboard-profesor.component';
import { AddProfesorComponent } from './components/add-profesor/add-profesor.component';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'add-student', component: AddStudentComponent },
  { path: 'dashboard-student', component: DashboardStudentComponent },
  { path: 'dashboard-profesor', component: DashboardProfesorComponent },
  { path: 'add-profesor', component: AddProfesorComponent }
];
