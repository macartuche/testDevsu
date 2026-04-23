export interface Cuenta {
  id?: number;
  numeroCuenta: string;
  tipoCuenta: string;
  saldoInicial: number;
  estado?: boolean;
  clienteNombre?: string;
  clienteId?: number;
}
