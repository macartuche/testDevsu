import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClienteService } from './cliente.service';
import { Cliente } from '../models/cliente.model';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClienteService]
    });
    service = TestBed.inject(ClienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  const mockCliente: Cliente = {
    id: 1,
    nombre: 'Juan Perez',
    identificacion: '1234567890',
    genero: 'M',
    edad: 30,
    telefono: '0991234567',
    direccion: 'Quito',
    clienteId: 100,
    contrasena: 'password123',
    estado: true
  };

  describe('getAll', () => {
    it('should return an array of clientes', () => {
      const mockClientes: Cliente[] = [mockCliente];

      service.getAll().subscribe(clientes => {
        expect(clientes).toEqual(mockClientes);
      });

      const req = httpMock.expectOne('/api/clientes');
      expect(req.request.method).toBe('GET');
      req.flush(mockClientes);
    });
  });

  describe('getById', () => {
    it('should return a single cliente by id', () => {
      service.getById(1).subscribe(cliente => {
        expect(cliente).toEqual(mockCliente);
      });

      const req = httpMock.expectOne('/api/clientes/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockCliente);
    });
  });

  describe('create', () => {
    it('should create a new cliente', () => {
      service.create(mockCliente).subscribe(cliente => {
        expect(cliente).toEqual(mockCliente);
      });

      const req = httpMock.expectOne('/api/clientes');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockCliente);
      req.flush(mockCliente);
    });
  });

  describe('update', () => {
    it('should update an existing cliente', () => {
      const updatedCliente = { ...mockCliente, nombre: 'Juan Perez Updated' };

      service.update(1, updatedCliente).subscribe(cliente => {
        expect(cliente).toEqual(updatedCliente);
      });

      const req = httpMock.expectOne('/api/clientes/1');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedCliente);
      req.flush(updatedCliente);
    });
  });

  describe('delete', () => {
    it('should delete a cliente', () => {
      service.delete(1).subscribe();

      const req = httpMock.expectOne('/api/clientes/1');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
