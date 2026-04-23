import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReporteResponse } from '../models/reporte.model';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly url = '/api/reportes';

  constructor(private http: HttpClient) {}

  getReporte(clienteId: number, fechaInicio: string, fechaFin: string): Observable<ReporteResponse> {
    const params = new HttpParams()
      .set('clienteId', clienteId.toString())
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin);
    return this.http.get<ReporteResponse>(this.url, { params });
  }
}
