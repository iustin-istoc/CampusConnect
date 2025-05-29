import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  imports: [RouterModule, CommonModule],
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  isAdmin: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    const rol = localStorage.getItem('rol');
    if (rol === 'admin') {
      this.isAdmin = true;
    } else {
      this.router.navigate(['/login']);
    }
  }
}
