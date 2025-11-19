package com.jugueteria.api.services.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment; // Importar TextAlignment
import com.itextpdf.layout.properties.UnitValue;
import com.jugueteria.api.entity.DetallePedido;
import com.jugueteria.api.entity.Pedido;
import com.jugueteria.api.services.PdfService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal; // Importar BigDecimal
import java.time.format.DateTimeFormatter;

@Service // <-- ¡AÑADIR ESTA ANOTACIÓN!
public class PdfServiceImpl implements PdfService {

    @Override
    public byte[] generateOrderInvoicePdf(Pedido pedido) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // --- Contenido del PDF ---
            document.add(new Paragraph("Boleta de Venta - Juguetería Fantasía").setBold().setFontSize(20));
            document.add(new Paragraph("Pedido N°: " + pedido.getId()));
            document.add(new Paragraph("Fecha: " + pedido.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph("Cliente: " + pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido()));
            document.add(new Paragraph("Dirección de Envío: " + pedido.getDireccionEnvio()));
            document.add(new Paragraph("\n"));

            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell("Producto");
            table.addHeaderCell("Cant.");
            table.addHeaderCell("P. Unit.");
            table.addHeaderCell("Subtotal");

            for (DetallePedido detalle : pedido.getDetalles()) {
                table.addCell(detalle.getProducto().getNombre());
                table.addCell(String.valueOf(detalle.getCantidad()));
                table.addCell("S/. " + detalle.getPrecioUnitario());
                table.addCell("S/. " + detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())));
            }
            
            document.add(table);
            document.add(new Paragraph("Total: S/. " + pedido.getTotal()).setBold().setFontSize(14).setTextAlignment(TextAlignment.RIGHT));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}