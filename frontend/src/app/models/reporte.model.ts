export interface ReporteItem {
  fecha: string;
  cliente: string;
  numeroCuenta: string;
  tipo: string;
  saldoInicial: number;
  estado: boolean;
  movimiento: number;
  saldoDisponible: number;
}

export interface ReporteResponse {
  movimientos: ReporteItem[];
  pdfBase64: string;
}
