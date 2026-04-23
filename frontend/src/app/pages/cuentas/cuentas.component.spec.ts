import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { CuentasComponent } from './cuentas.component';
import { CuentaService } from '../../services/cuenta.service';
import { ClienteService } from '../../services/cliente.service';
import { Cuenta } from '../../models/cuenta.model';
import { Cliente } from '../../models/cliente.model';

describe('CuentasComponent', () => {
  let component: CuentasComponent;
  let fixture: ComponentFixture<CuentasComponent>;
  let mockCuentaService: jasmine.SpyObj<CuentaService>;
  let mockClienteService: jasmine.SpyObj<ClienteService>;

  const mockCuentas: Cuenta[] = [
    { id: 1, numeroCuenta: '478758', tipoCuenta: 'Ahorros', saldoInicial: 100, estado: true, clienteId: 100, clienteNombre: 'Juan Perez' },
    { id: 2, numeroCuenta: '478759', tipoCuenta: 'Corriente', saldoInicial: 200, estado: true, clienteId: 101, clienteNombre: 'Maria Garcia' }
  ];

  const mockClientes: Cliente[] = [
    { id: 1, nombre: 'Juan Perez', identificacion: '1234567890', clienteId: 100, estado: true },
    { id: 2, nombre: 'Maria Garcia', identificacion: '0987654321', clienteId: 101, estado: true }
  ];

  beforeEach(async () => {
    mockCuentaService = jasmine.createSpyObj('CuentaService', ['getAll', 'create', 'update', 'delete']);
    mockCuentaService.getAll.and.returnValue(of(mockCuentas));
    mockCuentaService.create.and.returnValue(of(mockCuentas[0]));
    mockCuentaService.update.and.returnValue(of(mockCuentas[0]));
    mockCuentaService.delete.and.returnValue(of(void 0));

    mockClienteService = jasmine.createSpyObj('ClienteService', ['getAll']);
    mockClienteService.getAll.and.returnValue(of(mockClientes));

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, CuentasComponent],
      providers: [
        FormBuilder,
        { provide: CuentaService, useValue: mockCuentaService },
        { provide: ClienteService, useValue: mockClienteService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CuentasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load cuentas on init', () => {
    expect(mockCuentaService.getAll).toHaveBeenCalled();
    expect(component.cuentas.length).toBe(2);
  });

  it('should load clientes on init', () => {
    expect(mockClienteService.getAll).toHaveBeenCalled();
    expect(component.clientes.length).toBe(2);
  });

  it('should filter cuentas by numero or cliente nombre', () => {
    const event = { target: { value: '478758' } } as unknown as Event;
    component.filtrar(event);

    expect(component.cuentasFiltradas.length).toBe(1);
    expect(component.cuentasFiltradas[0].numeroCuenta).toBe('478758');
  });

  it('should filter cuentas by cliente nombre', () => {
    const event = { target: { value: 'Maria' } } as unknown as Event;
    component.filtrar(event);

    expect(component.cuentasFiltradas.length).toBe(1);
    expect(component.cuentasFiltradas[0].clienteNombre).toBe('Maria Garcia');
  });

  it('should open modal for new cuenta', () => {
    component.abrirModal();

    expect(component.modalVisible).toBe(true);
    expect(component.editando).toBe(false);
    expect(component.cuentaEditando).toBeNull();
  });

  it('should open modal for editing cuenta', () => {
    component.editar(mockCuentas[0]);

    expect(component.modalVisible).toBe(true);
    expect(component.editando).toBe(true);
    expect(component.cuentaEditando).toEqual(mockCuentas[0]);
  });

  it('should close modal', () => {
    component.modalVisible = true;
    component.cerrarModal();

    expect(component.modalVisible).toBe(false);
  });

  it('should not save if form is invalid', () => {
    component.form.get('numeroCuenta')?.setValue('');
    component.form.get('tipoCuenta')?.setValue('');
    component.form.get('clienteId')?.setValue('');
    component.guardar();

    expect(mockCuentaService.create).not.toHaveBeenCalled();
  });

  it('should create new cuenta when editando is false', () => {
    component.abrirModal();
    component.form.get('numeroCuenta')?.setValue('478760');
    component.form.get('tipoCuenta')?.setValue('Ahorros');
    component.form.get('saldoInicial')?.setValue(100);
    component.form.get('clienteId')?.setValue(100);
    component.form.get('estado')?.setValue(true);

    component.guardar();

    expect(mockCuentaService.create).toHaveBeenCalled();
  });

  it('should update existing cuenta when editando is true', () => {
    component.editar(mockCuentas[0]);
    component.form.get('saldoInicial')?.setValue(500);

    component.guardar();

    expect(mockCuentaService.update).toHaveBeenCalledWith('478758', jasmine.any(Object));
  });

  it('should handle error on save', () => {
    mockCuentaService.create.and.returnValue(throwError(() => new Error('Error de servidor')));

    component.abrirModal();
    component.form.get('numeroCuenta')?.setValue('478760');
    component.form.get('tipoCuenta')?.setValue('Ahorros');
    component.form.get('saldoInicial')?.setValue(100);
    component.form.get('clienteId')?.setValue(100);
    component.guardar();

    expect(component.formError).toBe('Error al guardar la cuenta');
  });

  it('should delete cuenta after confirmation', () => {
    spyOn(window, 'confirm').and.returnValue(true);

    component.eliminar(mockCuentas[0]);

    expect(mockCuentaService.delete).toHaveBeenCalledWith('478758');
  });

  it('should not delete cuenta if confirmation is cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);

    component.eliminar(mockCuentas[0]);

    expect(mockCuentaService.delete).not.toHaveBeenCalled();
  });
});