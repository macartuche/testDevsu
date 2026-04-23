import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CuentaService } from '../../services/cuenta.service';
import { ClienteService } from '../../services/cliente.service';
import { Cuenta } from '../../models/cuenta.model';
import { Cliente } from '../../models/cliente.model';

@Component({
  selector: 'app-cuentas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cuentas.component.html'
})
export class CuentasComponent implements OnInit {
  cuentas: Cuenta[] = [];
  cuentasFiltradas: Cuenta[] = [];
  clientes: Cliente[] = [];
  modalVisible = false;
  editando = false;
  cuentaEditando: Cuenta | null = null;
  guardando = false;
  error = '';
  formError = '';
  form!: FormGroup;

  constructor(
    private svc: CuentaService,
    private clienteSvc: ClienteService,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.initForm();
    this.cargar();
    this.clienteSvc.getAll().subscribe(data => this.clientes = data);
  }

  initForm() {
    this.form = this.fb.group({
      numeroCuenta: ['', Validators.required],
      tipoCuenta:   ['', Validators.required],
      saldoInicial: [0, [Validators.required, Validators.min(0)]],
      clienteId:    ['', Validators.required],
      estado:       [true]
    });
  }

  cargar() {
    this.svc.getAll().subscribe({
      next: data => { this.cuentas = data; this.cuentasFiltradas = data; },
      error: () => this.error = 'Error al cargar las cuentas'
    });
  }

  filtrar(event: Event) {
    const q = (event.target as HTMLInputElement).value.toLowerCase();
    this.cuentasFiltradas = this.cuentas.filter(c =>
      c.numeroCuenta.includes(q) || (c.clienteNombre || '').toLowerCase().includes(q)
    );
  }

  abrirModal() {
    this.editando = false;
    this.cuentaEditando = null;
    this.formError = '';
    this.form.reset({ estado: true, saldoInicial: 0 });
    this.modalVisible = true;
  }

  editar(c: Cuenta) {
    this.editando = true;
    this.cuentaEditando = c;
    this.formError = '';
    this.form.patchValue(c);
    this.modalVisible = true;
  }

  cerrarModal() { this.modalVisible = false; }

  guardar() {
    if (this.form.invalid) return;
    this.guardando = true;
    this.formError = '';
    const payload = this.form.value as Cuenta;

    const op = this.editando && this.cuentaEditando?.numeroCuenta
      ? this.svc.update(this.cuentaEditando.numeroCuenta, payload)
      : this.svc.create(payload);

    op.subscribe({
      next: () => { this.guardando = false; this.cerrarModal(); this.cargar(); },
      error: (err) => {
        this.guardando = false;
        this.formError = err?.error?.error || 'Error al guardar la cuenta';
      }
    });
  }

  eliminar(c: Cuenta) {
    if (!confirm(`¿Eliminar cuenta ${c.numeroCuenta}?`)) return;
    this.svc.delete(c.numeroCuenta).subscribe({
      next: () => this.cargar(),
      error: () => this.error = 'Error al eliminar la cuenta'
    });
  }
}
