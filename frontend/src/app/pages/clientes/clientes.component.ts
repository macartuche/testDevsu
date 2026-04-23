import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClienteService } from '../../services/cliente.service';
import { Cliente } from '../../models/cliente.model';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './clientes.component.html'
})
export class ClientesComponent implements OnInit {
  clientes: Cliente[] = [];
  clientesFiltrados: Cliente[] = [];
  modalVisible = false;
  editando = false;
  clienteEditando: Cliente | null = null;
  guardando = false;
  error = '';
  formError = '';
  form!: FormGroup;

  constructor(private svc: ClienteService, private fb: FormBuilder) {}

  ngOnInit() {
    this.initForm();
    this.cargar();
  }

  initForm() {
    this.form = this.fb.group({
      nombre:        ['', Validators.required],
      identificacion:['', Validators.required],
      genero:        [''],
      edad:          [null],
      telefono:      [''],
      direccion:     [''],
      contrasena:    [''],
      estado:        [true]
    });
  }

  cargar() {
    this.svc.getAll().subscribe({
      next: data => { this.clientes = data; this.clientesFiltrados = data; },
      error: () => this.error = 'Error al cargar los clientes'
    });
  }

  filtrar(event: Event) {
    const q = (event.target as HTMLInputElement).value.toLowerCase();
    this.clientesFiltrados = this.clientes.filter(c =>
      c.nombre.toLowerCase().includes(q) || c.identificacion.includes(q)
    );
  }

  abrirModal() {
    this.editando = false;
    this.clienteEditando = null;
    this.formError = '';
    this.form.reset({ estado: true });
    this.modalVisible = true;
  }

  editar(c: Cliente) {
    this.editando = true;
    this.clienteEditando = c;
    this.formError = '';
    this.form.patchValue(c);
    this.modalVisible = true;
  }

  cerrarModal() { this.modalVisible = false; }

  guardar() {
    if (this.form.invalid) return;
    this.guardando = true;
    this.formError = '';
    const payload = this.form.value as Cliente;

    const op = this.editando && this.clienteEditando?.clienteId
      ? this.svc.update(this.clienteEditando.clienteId, payload)
      : this.svc.create(payload);

    op.subscribe({
      next: () => { this.guardando = false; this.cerrarModal(); this.cargar(); },
      error: (err) => {
        this.guardando = false;
        this.formError = err?.error?.error || 'Error al guardar el cliente';
      }
    });
  }

  eliminar(c: Cliente) {
    if (!confirm(`¿Eliminar cliente "${c.nombre}"?`)) return;
    this.svc.delete(c.clienteId!).subscribe({
      next: () => this.cargar(),
      error: () => this.error = 'Error al eliminar el cliente'
    });
  }
}
