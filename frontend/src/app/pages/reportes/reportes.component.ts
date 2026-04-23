import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReporteService } from '../../services/reporte.service';
import { ClienteService } from '../../services/cliente.service';
import { ReporteItem } from '../../models/reporte.model';
import { Cliente } from '../../models/cliente.model';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reportes.component.html'
})
export class ReportesComponent implements OnInit {
  clientes: Cliente[] = [];
  movimientos: ReporteItem[] = [];
  pdfBase64 = '';
  cargando = false;
  buscado = false;
  error = '';
  form!: FormGroup;

  constructor(
    private svc: ReporteService,
    private clienteSvc: ClienteService,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.form = this.fb.group({
      clienteId:   ['', Validators.required],
      fechaInicio: ['', Validators.required],
      fechaFin:    ['', Validators.required]
    });
    this.clienteSvc.getAll().subscribe(data => this.clientes = data);
  }

  generar() {
    if (this.form.invalid) return;
    this.cargando = true;
    this.error = '';
    this.buscado = true;
    const { clienteId, fechaInicio, fechaFin } = this.form.value;

    this.svc.getReporte(clienteId, fechaInicio, fechaFin).subscribe({
      next: res => {
        this.movimientos = res.movimientos;
        this.pdfBase64 = res.pdfBase64;
        this.cargando = false;
      },
      error: () => {
        this.error = 'Error al generar el reporte';
        this.cargando = false;
      }
    });
  }

  descargarPdf() {
    if (!this.pdfBase64) return;
    const bytes = atob(this.pdfBase64);
    const arr = new Uint8Array(bytes.length);
    for (let i = 0; i < bytes.length; i++) arr[i] = bytes.charCodeAt(i);
    const blob = new Blob([arr], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `reporte_${this.form.value.clienteId}_${this.form.value.fechaInicio}.pdf`;
    a.click();
    URL.revokeObjectURL(url);
  }
}
