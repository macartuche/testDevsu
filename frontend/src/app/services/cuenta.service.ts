import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cuenta } from '../models/cuenta.model';

@Injectable({ providedIn: 'root' })
export class CuentaService {
  private readonly url = '/api/cuentas';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Cuenta[]> {
    return this.http.get<Cuenta[]>(this.url);
  }

  getByNumero(numeroCuenta: string): Observable<Cuenta> {
    return this.http.get<Cuenta>(`${this.url}/${numeroCuenta}`);
  }

  create(cuenta: Cuenta): Observable<Cuenta> {
    return this.http.post<Cuenta>(this.url, cuenta);
  }

  update(numeroCuenta: string, cuenta: Cuenta): Observable<Cuenta> {
    return this.http.put<Cuenta>(`${this.url}/${numeroCuenta}`, cuenta);
  }

  delete(numeroCuenta: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${numeroCuenta}`);
  }
}
