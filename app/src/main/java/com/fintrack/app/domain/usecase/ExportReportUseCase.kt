package com.fintrack.app.domain.usecase

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.Category
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExportReportUseCase @Inject constructor() {

    fun exportToCsv(
        context: Context,
        transactions: List<Pair<Transaction, Category?>>
    ) {
        val csvContent = buildString {
            append("Date,Type,Category,Amount,Note\n")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            transactions.forEach { (tx, cat) ->
                val date = dateFormat.format(Date(tx.date))
                val type = tx.type.name
                val categoryName = cat?.name ?: "Unknown"
                val amount = tx.amount.toString()
                val note = tx.note?.replace(",", " ") ?: ""
                append("$date,$type,$categoryName,$amount,$note\n")
            }
        }

        val file = File(context.cacheDir, "FinTrack_Export.csv")
        file.writeText(csvContent)
        shareFile(context, file, "text/csv")
    }

    fun exportToPdf(
        context: Context,
        transactions: List<Pair<Transaction, Category?>>
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        paint.color = Color.BLACK
        paint.textSize = 24f
        canvas.drawText("FinTrack Report", 50f, 50f, paint)

        paint.textSize = 14f
        var y = 100f
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        canvas.drawText("Date", 50f, y, paint)
        canvas.drawText("Category", 150f, y, paint)
        canvas.drawText("Amount", 350f, y, paint)
        canvas.drawText("Type", 450f, y, paint)
        
        y += 20f
        paint.strokeWidth = 1f
        canvas.drawLine(50f, y, 545f, y, paint)
        y += 20f

        transactions.forEach { (tx, cat) ->
            if (y > 800f) {
                return@forEach
            }
            val date = dateFormat.format(Date(tx.date))
            val categoryName = cat?.name ?: "Unknown"
            val amount = tx.amount.toString()
            val type = tx.type.name

            canvas.drawText(date, 50f, y, paint)
            canvas.drawText(categoryName, 150f, y, paint)
            canvas.drawText(amount, 350f, y, paint)
            canvas.drawText(type, 450f, y, paint)
            y += 20f
        }

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "FinTrack_Report.pdf")
        val outputStream = FileOutputStream(file)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
        outputStream.close()

        shareFile(context, file, "application/pdf")
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Report"))
    }
}
