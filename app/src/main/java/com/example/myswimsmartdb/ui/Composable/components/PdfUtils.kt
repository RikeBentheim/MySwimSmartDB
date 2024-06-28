package com.example.myswimsmartdb.ui.Composable.components

import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.io.IOException

object PdfUtils {

    @Throws(IOException::class)
    fun createPdf(dest: String, memberName: String, performanceDetails: String) {
        val writer = PdfWriter(dest)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc)

        document.add(Paragraph("Mitglied: $memberName"))
        document.add(Paragraph("Leistungen: $performanceDetails"))

        document.close()
    }
}
