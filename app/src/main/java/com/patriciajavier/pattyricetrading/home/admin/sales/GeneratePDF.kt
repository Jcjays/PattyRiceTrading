package com.patriciajavier.pattyricetrading.home.admin.sales

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.patriciajavier.pattyricetrading.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GeneratePDF:  AppCompatActivity() {

    private var btn: Button? = null
    private var btnScroll: Button? = null
    private var RVPdf: RecyclerView? = null
    private var bitmap: Bitmap? = null
    var targetPdf: String = "/storage/emulated/0//pdffromRecyclerView.pdf"

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sales_report_screen)

        btn = findViewById(R.id.DownloadPDFbutton)
        RVPdf = findViewById(R.id.salesReportScreenEpoxyRecyclerView)


        btn!!.setOnClickListener {
            Log.d("size", " " + RVPdf!!.width + "  " + RVPdf!!.width)
            bitmap = loadBitmapFromView(RVPdf!!, RVPdf!!.width, RVPdf!!.height)
            createPdf()
        }

    }

    private fun createPdf() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        //  Display display = wm.getDefaultDisplay();
        val displaymetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val hight = displaymetrics.heightPixels.toFloat()
        val width = displaymetrics.widthPixels.toFloat()

        val convertHighet = hight.toInt()
        val convertWidth = width.toInt()

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas

        val paint = Paint()
        canvas.drawPaint(paint)

        bitmap = Bitmap.createScaledBitmap(bitmap!!, convertWidth, convertHighet, true)

        paint.color = Color.BLUE
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        document.finishPage(page)

        // write the document content

        Log.d("target",targetPdf)
        val filePath: File
        filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Something wrong: $e", Toast.LENGTH_LONG).show()
        }

        // close the document
        document.close()
        Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show()

    }

    companion object {

        fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
            val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v.draw(c)

            return b
        }
    }

    }
