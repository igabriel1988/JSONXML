package dgtic.unam.jsonxml

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import dgtic.unam.jsonxml.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.Charset

class MainActivity : AppCompatActivity()
{
    private lateinit var binding:ActivityMainBinding
    private lateinit var volleyAPI: VolleyAPI
    private lateinit var url:String
    private val  ipPuerto = "192.168.210.1:8080"


    override fun onCreate( savedInstanceState: Bundle? )
        {
            super.onCreate( savedInstanceState )
            setContentView( R.layout.activity_main )

            binding=ActivityMainBinding.inflate( layoutInflater )
            setContentView( binding.root )

            volleyAPI= VolleyAPI( this )


            binding.search.setOnClickListener{
                    binding.outText.text = ""
                    url = "https://www.google.es/search?q"+URLEncoder.encode(
                        binding.searchText.text.toString(),"UTF8"
                    )

                    //Indicar un metodo para buscar, indicar como hacerlo
                    buscar()
                }
            binding.restXml.setOnClickListener {
                studentXML()
            }

            binding.restjson.setOnClickListener {
                studentJSON()
            }
            binding.restjsonid.setOnClickListener {
                studiantID()
            }
            binding.restjsonadd.setOnClickListener {
                studiantADD()
            }
            binding.restjsondelete.setOnClickListener {
                studiantDELETE()
            }
        }

    private fun buscar(){
        var stringRequest=object: StringRequest(
            Request.Method.GET,url,Response.Listener<String>{response->
                binding.outText.text=response
            },Response.ErrorListener { binding.outText.text="No se encuentra la información, revice su conexión" }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>()
                headers["User-Agent"]="Mozilla/5.0 (Windows NT 6.1)"
                return headers
            }
        }
        volleyAPI.add(stringRequest)
    }

    private fun studentXML() {
        val urlXML = "http://" + ipPuerto + "/estudiantesXML"
        //Hacemos la solicitud, necesitamos tres parametros
        val stringRequest = object : StringRequest(
              Request.Method.GET,
                urlXML,
                Response.Listener<String> { response ->
                    binding.outText.text = response
                },Response.ErrorListener {
                    binding.outText.text = "No se encuentra la informacion, revice su conexion"
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>()
                headers["User-Agent"]="Mozilla/5.0 (Windows NT 6.1)"
                return headers
            }
        }
        volleyAPI.add( stringRequest )
    }

    private fun studentJSON()
            {
                val urlJSON = "http://" + ipPuerto + "/estudiantesJSON"
                var cadena = ""
                val jsonRequest = object : JsonArrayRequest(
                                            urlJSON,
                                            Response.Listener<JSONArray> {response ->
                                                ( 0 until response.length() ).forEach {
                                                    val estudiante = response.getJSONObject( it )
                                                    val materia = estudiante.getJSONArray("materias" )
                                                    cadena += estudiante.get( "cuenta" ).toString() + "<"
                                                    ( 0 until materia.length() ).forEach {
                                                        val datos = materia.getJSONObject( it )
                                                        cadena += datos.get( "nombre" ).toString() + "**"+datos.get( "creditos" )
                                                            .toString()+"---"
                                                    }
                                                    cadena += "> \n"
                                                }
                                                binding.outText.text = cadena
                                            },
                                            Response.ErrorListener {
                                                binding.outText.text = "No se encuentra la informacion, revise su conexion"
                                            })
                                                {
                                                    override fun getHeaders(): MutableMap<String, String> {
                                                        val headers=HashMap<String,String>()
                                                        headers["User-Agent"]="Mozilla/5.0 (Windows NT 6.1)"
                                                        return headers
                                                    }
                                                }
                                volleyAPI.add(jsonRequest)
                        }

            private fun studiantID()
                {
                    val urlJSON = "http://" + ipPuerto + "/id/"+binding.searchText.text.toString()
                    var cadena = ""
                    val jsonRequest = object : JsonObjectRequest(
                        Method.GET,
                        urlJSON,
                        null,
                        Response.Listener<JSONObject> { response ->
                            binding.outText.text = response.get( "cuenta" )
                                .toString() + "----------" + response.get( "nombre" ).toString() + "\n"
                        },
                        Response.ErrorListener {
                            binding.outText.text = "No se encuentra la informacion, revise su conexion"
                        })
                    {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers=HashMap<String,String>()
                            headers["User-Agent"]="Mozilla/5.0 (Windows NT 6.1)"
                            return headers
                        }
                    }
                    volleyAPI.add(jsonRequest)
                }
    private fun  studiantADD()
        {
                val urlJSON = "http://" + ipPuerto + "/agregarestudiante"
                var cadena = ""
                val jsonRequest = object : JsonArrayRequest(
                    urlJSON,
                    Response.Listener<JSONArray> {response ->
                        ( 0 until response.length() ).forEach {
                            val estudiante = response.getJSONObject( it )
                            val materia = estudiante.getJSONArray("materias" )
                            cadena += estudiante.get( "cuenta" ).toString() + "<"
                            ( 0 until materia.length() ).forEach {
                                val datos = materia.getJSONObject( it )
                                cadena += datos.get( "nombre" ).toString() + "**"+datos.get( "creditos" )
                                    .toString()+"---"
                            }
                            cadena += "> \n"
                        }
                        binding.outText.text = cadena
                    },
                    Response.ErrorListener {
                        binding.outText.text = "No se encuentra la informacion, revise su conexion"
                    })
                        {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers=HashMap<String,String>()
                                headers["User-Agent"]="Mozilla/5.0 (Windows NT 6.1)"
                                return headers
                            }

                            override fun getBody(): ByteArray
                                {
                                    val estudiante = JSONObject()
                                    estudiante.put( "cuenta", "A000" )
                                    estudiante.put( "nombre", "Android" )
                                    estudiante.put( "edad", "200" )
                                    val materias = JSONArray()
                                    val itemMaterias = JSONObject()
                                    itemMaterias.put( "id", "1" )
                                    itemMaterias.put( "nombre", "Nueva materia" )
                                    itemMaterias.put( "creditos", "100" )
                                    materias.put( itemMaterias )
                                    estudiante.put( "materias", materias )
                                    return estudiante.toString().toByteArray( charset = Charsets.UTF_8 )
                                }

                            override fun getMethod(): Int {
                                return Method.POST
                            }
                        }
                volleyAPI.add(jsonRequest)
        }

    private fun studiantDELETE()
        {
                    val urlJSON = "http://" + ipPuerto + "/borrarestudiante/"+binding.searchText.text.toString()
                    var cadena = ""
                    val jsonRequest = object : JsonArrayRequest(
                        urlJSON,
                        Response.Listener<JSONArray> {response ->
                            ( 0 until response.length() ).forEach {
                                val estudiante = response.getJSONObject( it )
                                val materia = estudiante.getJSONArray("materias" )
                                cadena += estudiante.get( "cuenta" ).toString() + "<"
                                ( 0 until materia.length() ).forEach {
                                    val datos = materia.getJSONObject( it )
                                    cadena += datos.get( "nombre" ).toString() + "**"+datos.get( "creditos" )
                                        .toString()+"---"
                                }
                                cadena += "> \n"
                            }
                            binding.outText.text = cadena
                        },
                        Response.ErrorListener {
                            binding.outText.text = "No se encuentra la informacion, revise su conexion"
                        })
                    {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers=HashMap<String,String>()
                            headers["User-Agent"]="Mozilla/5.0 (Windows NT 6.1)"
                            return headers
                        }

                        override fun getMethod(): Int {
                            return Method.DELETE
                        }
                    }
                    volleyAPI.add( jsonRequest )
        }
}