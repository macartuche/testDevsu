import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MovimientoService } from '../../services/movimiento.service';
import { CuentaService } from '../../services/cuenta.service';
import { Movimiento } from '../../models/movimiento.model';
import { Cuenta } from '../../models/cuenta.model';

@Component({
  selector: 'app-movimientos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './movimientos.component.html'
})
export class MovimientosComponent implements OnInit {
  movimientos: Movimiento[] = [];
  cuentas: Cuenta[] = [];
  modalVisible = false;
  guardando = false;
  error = '';
  formError = '';
  form!: FormGroup;

  constructor(
    private svc: MovimientoService,
    private cuentaSvc: CuentaService,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.initForm();
    this.cargar();
    this.cuentaSvc.getAll().subscribe(data => this.cuentas = data);
  }

  initForm() {
    this.form = this.fb.group({
      numeroCuenta: ['', Validators.required],
      movimiento:   [null, Validators.required]
    });
  }

  cargar() {
    this.svc.getAll().subscribe({
      next: data => this.movimientos = data,
      error: () => this.error = 'Error al cargar los movimientos'
    });
  }

  abrirModal() {
    this.formError = '';
    this.form.reset();
    this.modalVisible = true;
  }

  cerrarModal() { this.modalVisible = false; }

  guardar() {
    if (this.form.invalid) return;
    this.guardando = true;
    this.formError = '';

    this.svc.create(this.form.value).subscribe({
      next: () => {
        this.guardando = false;
        this.cerrarModal();
        this.cargar();
        this.cuentaSvc.getAll().subscribe(d => this.cuentas = d);
      },
      error: (err: any) => {
        this.guardando = false;
        const mensaje = err?.error?.details || err?.error?.error || err?.message || 'Error al registrar el movimiento';
        this.formError = mensaje;
      }
    });
  }
}
