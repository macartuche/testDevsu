import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { ReportesComponent } from './reportes.component';
import { ReporteService } from '../../services/reporte.service';
import { ClienteService } from '../../services/cliente.service';
import { ReporteResponse } from '../../models/reporte.model';
import { Cliente } from '../../models/cliente.model';

describe('ReportesComponent', () => {
  let component: ReportesComponent;
  let fixture: ComponentFixture<ReportesComponent>;
  let mockReporteService: jasmine.SpyObj<ReporteService>;
  let mockClienteService: jasmine.SpyObj<ClienteService>;

  const mockClientes: Cliente[] = [
    { id: 1, nombre: 'Juan Perez', identificacion: '1234567890', clienteId: 100, estado: true },
    { id: 2, nombre: 'Maria Garcia', identificacion: '0987654321', clienteId: 101, estado: true }
  ];

  const mockReporteResponse: ReporteResponse = {
    movimientos: [
      { fecha: '2024-01-15', cliente: 'Juan Perez', numeroCuenta: '478758', tipo: 'Ahorros', saldoInicial: 100, estado: true, movimiento: 100, saldoDisponible: 200 },
      { fecha: '2024-01-16', cliente: 'Juan Perez', numeroCuenta: '478758', tipo: 'Ahorros', saldoInicial: 200, estado: true, movimiento: -50, saldoDisponible: 150 }
    ],
    pdfBase64: 'JVBERi0xLjQKJeLjz9MKMSAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXMgMiAwIFI+PgplbmRvYmoK'
  };

  beforeEach(async () => {
    mockReporteService = jasmine.createSpyObj('ReporteService', ['getReporte']);
    mockReporteService.getReporte.and.returnValue(of(mockReporteResponse));

    mockClienteService = jasmine.createSpyObj('ClienteService', ['getAll']);
    mockClienteService.getAll.and.returnValue(of(mockClientes));

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, ReportesComponent],
      providers: [
        FormBuilder,
        { provide: ReporteService, useValue: mockReporteService },
        { provide: ClienteService, useValue: mockClienteService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReportesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load clientes on init', () => {
    expect(mockClienteService.getAll).toHaveBeenCalled();
    expect(component.clientes.length).toBe(2);
  });

  it('should not generate report if form is invalid', () => {
    component.form.get('clienteId')?.setValue('');
    component.form.get('fechaInicio')?.setValue('');
    component.form.get('fechaFin')?.setValue('');
    component.generar();

    expect(mockReporteService.getReporte).not.toHaveBeenCalled();
  });

  it('should generate report when form is valid', () => {
    component.form.get('clienteId')?.setValue(100);
    component.form.get('fechaInicio')?.setValue('2024-01-01');
    component.form.get('fechaFin')?.setValue('2024-01-31');
    component.generar();

    expect(mockReporteService.getReporte).toHaveBeenCalledWith(100, '2024-01-01', '2024-01-31');
  });

  it('should set buscado to true when form is valid', () => {
    component.form.get('clienteId')?.setValue(100);
    component.form.get('fechaInicio')?.setValue('2024-01-01');
    component.form.get('fechaFin')?.setValue('2024-01-31');

    component.generar();

    expect(component.buscado).toBe(true);
  });

  it('should populate movimientos and pdfBase64 on successful response', () => {
    component.form.get('clienteId')?.setValue(100);
    component.form.get('fechaInicio')?.setValue('2024-01-01');
    component.form.get('fechaFin')?.setValue('2024-01-31');
    component.generar();

    expect(component.movimientos.length).toBe(2);
    expect(component.pdfBase64).toBe(mockReporteResponse.pdfBase64);
    expect(component.cargando).toBe(false);
    expect(component.buscado).toBe(true);
  });

  it('should handle error on report generation', () => {
    mockReporteService.getReporte.and.returnValue(throwError(() => new Error('Error de servidor')));

    component.form.get('clienteId')?.setValue(100);
    component.form.get('fechaInicio')?.setValue('2024-01-01');
    component.form.get('fechaFin')?.setValue('2024-01-31');
    component.generar();

    expect(component.error).toBe('Error al generar el reporte');
    expect(component.cargando).toBe(false);
  });

  it('should not download PDF if pdfBase64 is empty', () => {
    component.pdfBase64 = '';
    spyOn(document, 'createElement');
    component.descargarPdf();

    expect(document.createElement).not.toHaveBeenCalled();
  });

  it('should create anchor element to download PDF', () => {
    component.form.get('clienteId')?.setValue(100);
    component.form.get('fechaInicio')?.setValue('2024-01-01');
    component.form.get('fechaFin')?.setValue('2024-01-31');
    component.generar();

    const mockAnchor = { href: '', download: '', click: jasmine.createSpy('click') };
    spyOn(document, 'createElement').and.returnValue(mockAnchor as unknown as HTMLElement);
    spyOn(URL, 'createObjectURL').and.returnValue('blob:http://test');
    spyOn(URL, 'revokeObjectURL');

    component.descargarPdf();

    expect(document.createElement).toHaveBeenCalledWith('a');
    expect(mockAnchor.download).toContain('reporte_');
  });
});