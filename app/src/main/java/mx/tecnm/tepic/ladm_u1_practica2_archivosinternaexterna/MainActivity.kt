package mx.tecnm.tepic.ladm_u1_practica2_archivosinternaexterna

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var frase : String
        guardar.setOnClickListener {
            frase = phrase.text.toString()
            if(nombrearchivo.text.toString().isNotBlank()) {
                permisos()
                if(opcinterna.isChecked){
                    var data = phrase.text.toString()
                    var mensaje = ""
                    if (guardarEnMemoriaInterna(data,nombrearchivo.text.toString()) == true) {
                        mensaje = "Se guardó con éxito"
                    } else {
                        mensaje = "Error. No se pudo guardar"
                    }
                    Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show()
                }
                if(opcexterna.isChecked){
                    guardarEnMemoriaExterna(frase,nombrearchivo.text.toString())
                    phrase.setText("")
                    Toast.makeText(this,"Archivo guardado en SD",Toast.LENGTH_SHORT).show()
                }
                phrase.setText("")
                nombrearchivo.setText("")
            }else{
                Toast.makeText(this,"Por favor ponga un nombre al archivo",Toast.LENGTH_SHORT).show()
            }
        }
        abrir.setOnClickListener {
            if(nombrearchivo.text.toString().isNotBlank()) {
                permisos()
                if(opcinterna.isChecked){
                    var contenido = abrirDesdeMemoriaInterna(nombrearchivo.text.toString())
                    var mensaje = ""
                    if (contenido.isEmpty() == true) {
                        mensaje = "Error. No se pudo leer el archivo"
                    } else {
                        phrase.setText(contenido)
                        mensaje = "Archivo cargado correctamente"
                    }
                    Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show()
                }
                if(opcexterna.isChecked){
                    abrirDesdeMemoriaExterna(nombrearchivo.text.toString())
                }
            }else{
                Toast.makeText(this,"Ingrese el nombre del archivo",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun permisos(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
    }
    private fun abrirDesdeMemoriaExterna(name: String){
        var msg = ""
        try {
            //1ro NO MONTADA INFORMO NO HAY TARJETA SD
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                AlertDialog.Builder(this).setMessage("No hay SD montada")
                return
            }
            //2do ENRUTAMIENTO DE LA MEMORIA SD PARA CREAR EL ARCHIVO
            var rutaSD = getExternalFilesDir(null)!!.absolutePath
            var archivoSD = File(rutaSD, "${name}")
            var flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(archivoSD)))
            msg = flujoEntrada.readLine()
            phrase.setText(msg)
            flujoEntrada.close()
        } catch (io: IOException) {
            Toast.makeText(this, "Archivo no encontrado", Toast.LENGTH_SHORT).show()
        }
    }
    private fun guardarEnMemoriaExterna(data: String,name: String) {
        try {
            //1ro NO MONTADA INFORMO NO HAY TARJETA SD
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                return
            }
            //2do ENRUTAMIENTO DE LA MEMORIA SD PARA CREAR EL ARCHIVO
            var rutaSD = getExternalFilesDir(null)!!.absolutePath
            var archivoEnSD :File
            archivoEnSD = File(rutaSD, "${name}")
            var flujoSalida = OutputStreamWriter(FileOutputStream(archivoEnSD))
            flujoSalida.write(phrase.text.toString())
            flujoSalida.flush()
            flujoSalida.close()
            phrase.setText("")
        } catch (io: IOException) {
        }
    }

    private fun guardarEnMemoriaInterna(data: String,name: String): Boolean {
        try {
            var flujoSalida = OutputStreamWriter(openFileOutput("${name}", Context.MODE_PRIVATE))
            flujoSalida.write(data)
            flujoSalida.flush()
            flujoSalida.close()
        } catch (io: IOException) {
            return false
        }
        return true
    }

    private fun abrirDesdeMemoriaInterna(name: String): String {
        var data = ""
        try {
            var flujoEntrada = BufferedReader(InputStreamReader(openFileInput("${name}")))
            data = flujoEntrada.readLine()
        } catch (io: IOException) {
            return ""
        }
        return data
    }
}