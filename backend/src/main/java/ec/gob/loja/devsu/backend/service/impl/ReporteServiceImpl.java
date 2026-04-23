package ec.gob.loja.devsu.backend.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ec.gob.loja.devsu.backend.domain.entity.Movimiento;
import ec.gob.loja.devsu.backend.domain.repository.MovimientoRepository;
import ec.gob.loja.devsu.backend.dto.MovimientoDTO;
import ec.gob.loja.devsu.backend.dto.ReporteResponseDTO;
import ec.gob.loja.devsu.backend.service.ReporteService;
import ec.gob.loja.devsu.backend.mapper.MovimientoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;

    public ReporteServiceImpl(MovimientoRepository movimientoRepository, MovimientoMapper movimientoMapper) {
        this.movimientoRepository = movimientoRepository;
        this.movimientoMapper = movimientoMapper;
    }

@Override
    @Transactional(readOnly = true)
    public ReporteResponseDTO generarReporte(Long clienteId, String fechaInicio, String fechaFin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(fechaInicio, formatter);
        LocalDate end = LocalDate.parse(fechaFin, formatter);

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        List<Movimiento> movimientos = movimientoRepository
                .findByCuenta_Cliente_IdAndFechaBetween(clienteId, startDateTime, endDateTime);

        List<MovimientoDTO> movimientosDTO = movimientos.stream()
                .map(movimientoMapper::toDto)
                .collect(Collectors.toList());

        String pdfBase64 = generarPdf(fechaInicio, fechaFin, movimientosDTO);

        return new ReporteResponseDTO(movimientosDTO, pdfBase64);
    }

    private String generarPdf(String fechaInicio, String fechaFin, List<MovimientoDTO> movimientos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);

            Paragraph title = new Paragraph("Reporte de Estado de Cuenta", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            Paragraph dateRange = new Paragraph(
                "Período: " + fechaInicio + " - " + fechaFin, headerFont);
            dateRange.setAlignment(Element.ALIGN_CENTER);
            document.add(dateRange);
            document.add(new Paragraph(" "));

            if (movimientos.isEmpty()) {
                document.add(new Paragraph("No existen movimientos en el período indicado.", normalFont));
            } else {
                PdfPTable table = new PdfPTable(8);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{1.5f, 1.8f, 1.2f, 1.2f, 1.3f, 1.3f, 1.3f, 1.5f});

                addTableHeader(table, boldFont);

                for (MovimientoDTO m : movimientos) {
                    addCell(table, m.fecha() != null ? m.fecha().toLocalDate().toString() : "", normalFont);
                    addCell(table, m.cliente() != null ? m.cliente() : "", normalFont);
                    addCell(table, m.numeroCuenta() != null ? m.numeroCuenta() : "", normalFont);
                    addCell(table, m.tipo() != null ? m.tipo() : "", normalFont);
                    addCell(table, formatCurrency(m.saldoInicial()), normalFont);
                    addCell(table, formatCurrency(m.movimiento()), normalFont);
                    addCell(table, formatCurrency(m.saldoDisponible()), normalFont);
                    addCell(table, m.estado() ? "Activo" : "Inactivo", normalFont);
                }

                table.completeRow();
                document.add(table);

                document.add(new Paragraph(" "));
                BigDecimal totalCreditos = BigDecimal.ZERO;
                BigDecimal totalDebitos = BigDecimal.ZERO;
                BigDecimal saldoFinal = BigDecimal.ZERO;
                for (MovimientoDTO m : movimientos) {
                    if (m.movimiento().signum() > 0) totalCreditos = totalCreditos.add(m.movimiento());
                    if (m.movimiento().signum() < 0) totalDebitos = totalDebitos.add(m.movimiento());
                    saldoFinal = m.saldoDisponible();
                }

                Paragraph totales = new Paragraph("\nTotales:", boldFont);
                document.add(totales);
                document.add(new Paragraph("Total Créditos: $" + totalCreditos, normalFont));
                document.add(new Paragraph("Total Débitos: $" + totalDebitos, normalFont));
                document.add(new Paragraph("Saldo Final: $" + saldoFinal, normalFont));
            }

            document.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    private void addTableHeader(PdfPTable table, Font font) {
        String[] headers = {"Fecha", "Cliente", "Nro. Cuenta", "Tipo", "Saldo Inicial", "Movimiento", "Saldo Disp.", "Estado"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, font));
            cell.setGrayFill(0.9f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private String formatCurrency(BigDecimal value) {
        return value != null ? "$" + value.toString() : "$0.00";
    }
}
