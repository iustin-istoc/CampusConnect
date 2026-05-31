import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SecurityService } from '../../services/security.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  // pas 1
  email = '';
  parola = '';
  rol = 'student';

  // pas 2 (MFA) - metode: totp | email | backup | fido2
  step: 'credentials' | 'mfa' = 'credentials';
  metoda: 'totp' | 'email' | 'backup' | 'fido2' = 'totp';
  code = '';
  pendingToken = '';
  qr = '';
  secret = '';
  isSetup = false;
  mesajEroare = '';
  mesajInfo = '';
  emailTrimis = false;

  constructor(private http: HttpClient, private router: Router, private sec: SecurityService) {}

  ngOnInit(): void {
    localStorage.clear();
  }

  // -- PAS 1 --
  login() {
    this.mesajEroare = '';
    const payload = { email: this.email, parola: this.parola, rol: this.rol };
    this.http.post<any>('https://localhost:8443/api/login', payload).subscribe({
      next: (res) => {
        this.pendingToken = res.pendingToken;
        this.isSetup = res.status === 'MFA_SETUP';
        if (this.isSetup) { this.qr = res.qr; this.secret = res.secret; }
        this.step = 'mfa';
        this.metoda = 'totp';
      },
      error: () => { this.mesajEroare = 'Email sau parola incorecte.'; }
    });
  }

  // -- Selectare metoda --
  alege(m: 'totp' | 'email' | 'backup' | 'fido2') {
    this.metoda = m;
    this.code = '';
    this.mesajEroare = '';
    this.mesajInfo = '';
    this.emailTrimis = false;
  }

  // -- Email OTP --
  trimiteCodEmail() {
    this.mesajEroare = ''; this.mesajInfo = '';
    this.http.post<any>('https://localhost:8443/api/login/email-otp/send',
      { pendingToken: this.pendingToken }).subscribe({
      next: () => { this.emailTrimis = true; this.mesajInfo = 'Codul a fost trimis pe email.'; },
      error: () => { this.mesajEroare = 'Nu s-a putut trimite codul pe email.'; }
    });
  }

  // -- Verificare cod (totp / email / backup) --
  verifyCod() {
    this.mesajEroare = '';
    let url = '';
    if (this.metoda === 'totp')   url = 'https://localhost:8443/api/login/verify';
    if (this.metoda === 'email')  url = 'https://localhost:8443/api/login/email-otp/verify';
    if (this.metoda === 'backup') url = 'https://localhost:8443/api/login/backup/verify';

    this.http.post<any>(url, { pendingToken: this.pendingToken, code: this.code }).subscribe({
      next: (res) => this.finalizeaza(res),
      error: () => { this.mesajEroare = 'Cod invalid. Mai incearca o data.'; this.code = ''; }
    });
  }

  // -- FIDO2 / WebAuthn --
  async autentificaFido2() {
    this.mesajEroare = '';
    try {
      const res = await this.sec.autentificaCuDispozitiv(this.pendingToken);
      this.finalizeaza(res);
    } catch (e) {
      this.mesajEroare = 'Autentificarea cu dispozitivul a esuat sau a fost anulata.';
    }
  }

  private finalizeaza(res: any) {
    localStorage.setItem('token', res.token);
    localStorage.setItem('email', res.email);
    localStorage.setItem('rol', res.rol);
    if (res.rol === 'student') this.router.navigate(['/dashboard-student']);
    else this.router.navigate(['/dashboard-profesor']);
  }

  inapoi() {
    this.step = 'credentials';
    this.code = ''; this.mesajEroare = ''; this.mesajInfo = ''; this.emailTrimis = false;
  }
}