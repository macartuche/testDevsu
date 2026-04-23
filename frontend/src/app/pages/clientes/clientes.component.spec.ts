import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { ClientesComponent } from './clientes.component';
import { ClienteService } from '../../services/cliente.service';
import { Cliente } from '../../models/cliente.model';

describe('ClientesComponent', () => {
  let component: ClientesComponent;
  let fixture: ComponentFixture<ClientesComponent>;
  let mockClienteService: jasmine.SpyObj<ClienteService>;

  const mockClientes: Cliente[] = [
    { id: 1, nombre: 'Juan Perez', identificacion: '1234567890', clienteId: 100, estado: true },
    { id: 2, nombre: 'Maria Garcia', identificacion: '0987654321', clienteId: 101, estado: true }
  ];

  beforeEach(async () => {
    mockClienteService = jasmine.createSpyObj('ClienteService', ['getAll', 'create', 'update', 'delete']);
    mockClienteService.getAll.and.returnValue(of(mockClientes));
    mockClienteService.create.and.returnValue(of(mockClientes[0]));
    mockClienteService.update.and.returnValue(of(mockClientes[0]));
    mockClienteService.delete.and.returnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, ClientesComponent],
      providers: [
        FormBuilder,
        { provide: ClienteService, useValue: mockClienteService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClientesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load clientes on init', () => {
    expect(mockClienteService.getAll).toHaveBeenCalled();
    expect(component.clientes.length).toBe(2);
    expect(component.clientesFiltrados.length).toBe(2);
  });

  it('should filter clientes by nombre or identificacion', () => {
    const event = { target: { value: 'Juan' } } as unknown as Event;
    component.filtrar(event);

    expect(component.clientesFiltrados.length).toBe(1);
    expect(component.clientesFiltrados[0].nombre).toBe('Juan Perez');
  });

  it('should filter clientes by identificacion', () => {
    const event = { target: { value: '1234567890' } } as unknown as Event;
    component.filtrar(event);

    expect(component.clientesFiltrados.length).toBe(1);
    expect(component.clientesFiltrados[0].identificacion).toBe('1234567890');
  });

  it('should open modal for new cliente', () => {
    component.abrirModal();

    expect(component.modalVisible).toBe(true);
    expect(component.editando).toBe(false);
    expect(component.clienteEditando).toBeNull();
    expect(component.formError).toBe('');
  });

  it('should open modal for editing cliente', () => {
    component.editar(mockClientes[0]);

    expect(component.modalVisible).toBe(true);
    expect(component.editando).toBe(true);
    expect(component.clienteEditando).toEqual(mockClientes[0]);
  });

  it('should close modal', () => {
    component.modalVisible = true;
    component.cerrarModal();

    expect(component.modalVisible).toBe(false);
  });

  it('should not save if form is invalid', () => {
    component.form.get('nombre')?.setValue('');
    component.form.get('identificacion')?.setValue('');
    component.guardar();

    expect(mockClienteService.create).not.toHaveBeenCalled();
    expect(mockClienteService.update).not.toHaveBeenCalled();
  });

  it('should create new cliente when editando is false', () => {
    component.abrirModal();
    component.form.get('nombre')?.setValue('Juan Perez');
    component.form.get('identificacion')?.setValue('1234567890');
    component.form.get('genero')?.setValue('M');
    component.form.get('edad')?.setValue(30);
    component.form.get('telefono')?.setValue('0991234567');
    component.form.get('direccion')?.setValue('Quito');
    component.form.get('contrasena')?.setValue('password');
    component.form.get('estado')?.setValue(true);

    component.guardar();

    expect(mockClienteService.create).toHaveBeenCalled();
  });

  it('should update existing cliente when editando is true', () => {
    component.editar(mockClientes[0]);
    component.form.get('nombre')?.setValue('Juan Perez Updated');

    component.guardar();

    expect(mockClienteService.update).toHaveBeenCalledWith(100, jasmine.any(Object));
  });

  it('should handle error on save', () => {
    mockClienteService.create.and.returnValue(throwError(() => new Error('Error de servidor')));

    component.abrirModal();
    component.form.get('nombre')?.setValue('Juan Perez');
    component.form.get('identificacion')?.setValue('1234567890');
    component.guardar();

    expect(component.formError).toBe('Error al guardar el cliente');
  });

  it('should delete cliente after confirmation', () => {
    spyOn(window, 'confirm').and.returnValue(true);

    component.eliminar(mockClientes[0]);

    expect(mockClienteService.delete).toHaveBeenCalledWith(100);
  });

  it('should not delete cliente if confirmation is cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);

    component.eliminar(mockClientes[0]);

    expect(mockClienteService.delete).not.toHaveBeenCalled();
  });
});
