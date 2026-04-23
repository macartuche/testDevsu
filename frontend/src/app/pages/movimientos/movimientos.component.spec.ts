import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { MovimientosComponent } from './movimientos.component';
import { MovimientoService } from '../../services/movimiento.service';
import { CuentaService } from '../../services/cuenta.service';
import { Movimiento } from '../../models/movimiento.model';
import { Cuenta } from '../../models/cuenta.model';

describe('MovimientosComponent', () => {
  let component: MovimientosComponent;
  let fixture: ComponentFixture<MovimientosComponent>;
  let mockMovimientoService: jasmine.SpyObj<MovimientoService>;
  let mockCuentaService: jasmine.SpyObj<CuentaService>;

  const mockMovimientos: Movimiento[] = [
    { id: 1, fecha: '2024-01-15T10:00:00', cliente: 'Juan Perez', numeroCuenta: '478758', tipo: 'Ahorros', saldoInicial: 100, estado: true, movimiento: 100, saldoDisponible: 200 },
    { id: 2, fecha: '2024-01-16T10:00:00', cliente: 'Maria Garcia', numeroCuenta: '478759', tipo: 'Corriente', saldoInicial: 200, estado: true, movimiento: -50, saldoDisponible: 150 }
  ];

  const mockCuentas: Cuenta[] = [
    { id: 1, numeroCuenta: '478758', tipoCuenta: 'Ahorros', saldoInicial: 100, estado: true, clienteId: 100, clienteNombre: 'Juan Perez' },
    { id: 2, numeroCuenta: '478759', tipoCuenta: 'Corriente', saldoInicial: 200, estado: true, clienteId: 101, clienteNombre: 'Maria Garcia' }
  ];

  beforeEach(async () => {
    mockMovimientoService = jasmine.createSpyObj('MovimientoService', ['getAll', 'create']);
    mockMovimientoService.getAll.and.returnValue(of(mockMovimientos));
    mockMovimientoService.create.and.returnValue(of(mockMovimientos[0]));

    mockCuentaService = jasmine.createSpyObj('CuentaService', ['getAll']);
    mockCuentaService.getAll.and.returnValue(of(mockCuentas));

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, MovimientosComponent],
      providers: [
        FormBuilder,
        { provide: MovimientoService, useValue: mockMovimientoService },
        { provide: CuentaService, useValue: mockCuentaService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MovimientosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load movimientos on init', () => {
    expect(mockMovimientoService.getAll).toHaveBeenCalled();
    expect(component.movimientos.length).toBe(2);
  });

  it('should load cuentas on init', () => {
    expect(mockCuentaService.getAll).toHaveBeenCalled();
    expect(component.cuentas.length).toBe(2);
  });

  it('should open modal for new movimiento', () => {
    component.abrirModal();

    expect(component.modalVisible).toBe(true);
    expect(component.formError).toBe('');
    expect(component.form.pristine).toBe(true);
  });

  it('should close modal', () => {
    component.modalVisible = true;
    component.cerrarModal();

    expect(component.modalVisible).toBe(false);
  });

  it('should not save if form is invalid', () => {
    component.form.get('numeroCuenta')?.setValue('');
    component.form.get('movimiento')?.setValue(null);
    component.guardar();

    expect(mockMovimientoService.create).not.toHaveBeenCalled();
  });

  it('should create new movimiento when form is valid', () => {
    component.form.get('numeroCuenta')?.setValue('478758');
    component.form.get('movimiento')?.setValue(100);

    component.guardar();

    expect(mockMovimientoService.create).toHaveBeenCalledWith({ numeroCuenta: '478758', movimiento: 100 });
  });

  it('should reload cuentas after successful save', () => {
    mockMovimientoService.create.and.returnValue(of(mockMovimientos[0]));

    component.form.get('numeroCuenta')?.setValue('478758');
    component.form.get('movimiento')?.setValue(100);
    component.guardar();

    expect(mockCuentaService.getAll).toHaveBeenCalledTimes(2);
  });

  it('should handle error on save and show error message', () => {
    mockMovimientoService.create.and.returnValue(throwError(() => new Error('Saldo no disponible')));

    component.form.get('numeroCuenta')?.setValue('478758');
    component.form.get('movimiento')?.setValue(-1000);
    component.guardar();

    expect(component.formError).toBe('Saldo no disponible');
    expect(component.guardando).toBe(false);
  });

  it('should handle error with message from error response', () => {
    mockMovimientoService.create.and.returnValue(throwError(() => ({ message: 'Cupo diario Excedido' })));

    component.form.get('numeroCuenta')?.setValue('478758');
    component.form.get('movimiento')?.setValue(-600);
    component.guardar();

    expect(component.formError).toBe('Cupo diario Excedido');
  });

  it('should handle generic error message', () => {
    mockMovimientoService.create.and.returnValue(throwError(() => null));

    component.form.get('numeroCuenta')?.setValue('478758');
    component.form.get('movimiento')?.setValue(100);
    component.guardar();

    expect(component.formError).toBe('Error al registrar el movimiento');
  });
});