package com.naldana.ejemplo11

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_PERMISSION = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        bt_save.setOnClickListener {
            with(sharedPref.edit()) {
                putString(getString(R.string.save_email_key), et_option.text.toString())
                commit()
            }
            tv_data.text = et_option.text.toString() // Solamente para mostrar el valor de inmediato
        }
        var email = sharedPref.getString(getString(R.string.save_email_key), "")

        tv_data.text = email

        bt_write_internal.setOnClickListener {
            val filename = "email.txt"
            val fileContent = "email: $email"
            openFileOutput(filename,Context.MODE_PRIVATE).use {
                it.write(fileContent.toByteArray())
            }

        }

        bt_write_external.setOnClickListener{
            email = sharedPref.getString("email", "")
            var fileName = "emailEXT.txt"
            try {
                var dirEXT = Environment.getExternalStorageDirectory()
                var ruta = File(dirEXT.path, fileName)
                val crear = OutputStreamWriter(FileOutputStream(ruta))
                crear.write(email)
                crear.flush();
                crear.close();

            } catch (e: IOException) {
                e.printStackTrace()
            }
            Toast.makeText(applicationContext,"data save",Toast.LENGTH_SHORT).show()

        }

        bt_read_internal.setOnClickListener{
            // TODO (12) Abrir un archivo existente
            val filename = "email.txt"
            openFileInput(filename).use {
                val text = it.bufferedReader().readText() // TODO (13) Se lee todo el contenido
                tv_data.text = text
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            write()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                write()
            }
        }
    }

    private fun write() {
        val dir = "${Environment.getExternalStorageDirectory()}/$packageName"
        File(dir).mkdirs()
        val file = "%1\$tY%1\$tm%1\$td%1\$tH%1\$tM%1\$tS.log".format(Date())
        File("$dir/$file").printWriter().use {
            it.println("text")
        }
    }

}
