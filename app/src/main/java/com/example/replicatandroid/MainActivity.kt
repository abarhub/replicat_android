package com.example.replicatandroid

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.replicatandroid.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.Base64
import java.util.Properties
import java.util.logging.Logger
import java.util.stream.Collectors.toList


//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.core.Observable;
//import io.reactivex.rxjava3.core.ObservableSource;
//import io.reactivex.rxjava3.disposables.CompositeDisposable;
//import io.reactivex.rxjava3.functions.Supplier;
//import io.reactivex.rxjava3.observers.DisposableObserver;
//import io.reactivex.rxjava3.schedulers.Schedulers;
//
//
//import io.reactivex.Observable;

@Serializable
data class ListFiles(val filename: String, val size: Long)


@Serializable
data class Files2(val filename: String, val size: Long, val hash: String, val type:String)

@Serializable
data class ListFiles2(val liste: List<Files2>, val code: String)

data class Files3(val filename: String, val size: Long, val hash: String, val type:String,val doc:DocumentFile)

data class Config(val serveur: String, val rep:String, val rep2:String)

private val REQUEST_READ_EXTERNAL_STORAGE = 1

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val Log: Logger = Logger.getLogger(MainActivity::class.java.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    fun addOne(view: View) {
        Log.warning("Hello World2")
        //txtCounter.text = (txtCounter.text.toString().toInt() + 1).toString()
        val f=this.applicationContext.filesDir

        getListeFichiers();
    }

    private fun getListeFichiers() {

//        val res=Observable.just("one", "two", "three", "four", "five")
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { s: Any ->
//                println("test $s")
//            }


        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when it loads.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        //startActivityForResult(intent, RQS_OPEN_DOCUMENT_TREE)
        resultLauncher2.launch(intent)

    }


    var resultLauncher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val data2 = result.data
            //doSomeOperations()
            Log.info("date: $data; ${data2?.data}")

            val config=getConfig()

            val rep=config.rep2
            Log.info("rep: $rep")

            val listeFichier=listeFiles(rep,data)

            Log.info("listeFichier not empty: ${listeFichier.isNotEmpty()}")
            if(listeFichier.isNotEmpty()){
                envoieListeFichiers(listeFichier)
            }

//            if (data != null && data.data != null) {
//                val d: Uri? = data.data;
//                if (d != null) {
//
//                    val config=getConfig()
//
//                    val rep=config.rep2
//
//                    if(rep!=null&&rep.length>0){
//
//                        val documentFile = DocumentFile.fromTreeUri(this, d)
//
//                        if(documentFile!=null) {
//                            val racine=getFile(documentFile, rep)
//
//                            Log.info("racine=$racine")
//
//                            val listeFichiers=ArrayList<Files3>()
//
//                            if(racine!=null) {
//                                listeFichiers.clear()
//                                ajouteFichier(racine, listeFichiers, "")
////                                val liste = racine.listFiles()
////                                listeFichiers.addAll(liste)
//                            }
//                            Log.info("listeFichiers=$listeFichiers")
//
//                            envoieListeFichiers(listeFichiers)
//                        }
//                    }
//                }
//            }
        }
    }

    private fun listeFiles(repertoire:String, data: Intent?): ArrayList<Files3>{
        Log.info("listeFiles...")
        Log.info("data.data : $data;${data?.data}")
        if (data != null && data.data != null) {
            val d: Uri? = data.data;
            if (d != null) {
                Log.info("repertoire not empty : ${repertoire.isNotEmpty()}")
                Log.info("repertoire : ;${repertoire}!")
                if (repertoire.isNotEmpty()||true) {
                    val documentFile = DocumentFile.fromTreeUri(this, d)
                    Log.info("documentFile : ${documentFile != null}")
                    if (documentFile != null) {
                        val racine = getFile(documentFile, repertoire)

                        Log.info("racine=$racine")

                        val listeFichiers = ArrayList<Files3>()

                        if (racine != null) {
                            listeFichiers.clear()
                            ajouteFichier(racine, listeFichiers, "")
//                                val liste = racine.listFiles()
//                                listeFichiers.addAll(liste)
                        }
                        Log.info("listeFichiers=$listeFichiers")

//                envoieListeFichiers(listeFichiers)
                        return listeFichiers
                    }
                }
            }
        }
        return java.util.ArrayList<Files3>()
    }

    private fun envoieListeFichiers(listeFichiers: ArrayList<Files3>) {
        if(!listeFichiers.isEmpty()){

            val config=getConfig()

            if(config.serveur!=null&&config.serveur.trim().length>0) {




                val queue = Volley.newRequestQueue(this)
                val url = config.serveur
                Log.info("url=${url}")


                val stringRequest3 = object : StringRequest(
                    Request.Method.POST, "$url/init",
                    Response.Listener { response ->

                        val s=response
                        Log.info("s=$s")
                        if(s.isNotEmpty()){

                            val no=s.toInt(10)
                            if(no>0){

                                Log.info("no=$no")
                                traitement(no, listeFichiers, queue,url)
                            }

                        }

                    },
                    Response.ErrorListener {
                        Log.warning("That didn't work3! $it")
                    }) {
                    /*override fun getParams(): Map<String, String> {
                        return params
                    }*/
                }

                queue.add(stringRequest3)

                // suite




            }

        }
    }

    private fun traitement(
        no: Int,
        listeFichiers: ArrayList<Files3>,
        queue: RequestQueue,
        url: String
    ){
        val liste =ArrayList<Files2>()

        for(f in listeFichiers){
            liste.add(Files2(f.filename,f.size,f.hash,f.type))
        }

        val listFiles = ListFiles2(liste, "")
        val json = Json.encodeToString(listFiles)

        val params = HashMap<String, String>()
        params["data"] = json

        val stringRequest2 = object : StringRequest(
            Request.Method.POST, "$url/listeFichiers/$no",
            Response.Listener { response -> // Display the first 500 characters of the response string.
                //textView.setText("Response is: " + response.substring(0, 500))
                Log.info(
                    "Response2 is: " + response.substring(
                        0,
                        Math.min(500, response.length)
                    )
                )


                if(response.isNotEmpty()) {
                    val s2 = Json.decodeFromString<ListFiles2>(response)
                    Log.info("s2 is $s2")

                    if (s2 != null && s2.liste != null && s2.liste.size > 0) {
                        envoiListeFichiers(s2, listeFichiers, url, queue, no)
                    } else {
                        Log.info("no file to transfert")
                    }
                }

            },
            Response.ErrorListener {
                Log.warning("That didn't work2! $it")
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        queue.add(stringRequest2)
    }

    private fun envoiListeFichiers(
        s2: ListFiles2,
        listeFichiers: ArrayList<Files3>,
        url: String,
        queue: RequestQueue,
        no: Int
    ) {

        val listeFilename=listeFichiers.stream().map { it->it.filename }.collect(toList())
        Log.info("liste des fichiers à transferer: $listeFilename")

        for (f0 in s2.liste) {
            val file = f0
            if (file != null && file.filename != null && file.filename.length > 0) {
                val name = file.filename
                val fOpt = listeFichiers.stream().filter {
                    it.filename.equals(name)
                }.findAny()
                if (fOpt.isPresent) {
                    val f = fOpt.get()

                    var res = ""
                    val inputStream = contentResolver.openInputStream(f.doc.uri)
                    if (inputStream != null) {
    //                                            val input = InputStreamReader(inputStream)
                        //                                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val outputStream = ByteArrayOutputStream()
                        inputStream.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }
                        //                                        inputStream.readAllBytes()
                        //                                        val lines = reader.readLines()
                        inputStream.close()
                        //                                        res=String(outputStream.toByteArray())
                        res = Base64.getEncoder()
                            .encodeToString(outputStream.toByteArray())

                        //Log.info("content: $res")
                    }

                    val params2 = HashMap<String, String>()
                    params2["file"] = res
                    params2["filename"] = f.filename

                    Log.info("Envoi du fichier ${f.filename}")

                    val stringRequest3 = object : StringRequest(
                        Method.POST, "$url/upload/$no",
                        Response.Listener { response -> // Display the first 500 characters of the response string.
                            //textView.setText("Response is: " + response.substring(0, 500))
//                            Log.info(
//                                "Response3 is: " + response.substring(
//                                    0,
//                                    Math.min(500, response.length)
//                                )
//                            )
                            Log.info("Envoi du fichier ${f.filename} : "+response.substring(
                                0,
                                Math.min(500, response.length)))

                        },
                        Response.ErrorListener {
                            //textView.setText("That didn't work!")
                            Log.warning("That didn't work3! $it")
                        }) {
                        override fun getParams(): Map<String, String> {
                            return params2
                        }
                    }

                    queue.add(stringRequest3)

                }
            }
        }
    }

    private fun ajouteFichier(racine: DocumentFile, listeFichiers: ArrayList<Files3>, parent: String) {
        val liste = racine.listFiles()
        Log.info("ajouteFichier liste : $racine;${liste}")
        for(f in liste){
            if(f.name!=null) {
                val chemin:String
                if (parent.isEmpty()){
                    chemin=f.name!!
                } else {
                    chemin=parent+"/"+f.name
                }
                if(f.isDirectory){
                    ajouteFichier(f,listeFichiers,chemin)
                } else {
                    val content=readFile(f)
                    val hash=hashString(content,"SHA-256")
                    val f2 = Files3(chemin, f.length(), hash.toHex(), "F", f)
                    listeFichiers.add(f2)
                }
            }
        }
    }

    fun readFile(f: DocumentFile): ByteArray {
        val inputStream = contentResolver.openInputStream(f.uri)
        if (inputStream != null) {
//            val input = InputStreamReader(inputStream)
            //                                        val reader = BufferedReader(InputStreamReader(inputStream))
            val outputStream = ByteArrayOutputStream()
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            inputStream.close()
            return outputStream.toByteArray()
        }
        return ByteArray(0)
    }

    fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }

    fun hashString(str: ByteArray, algorithm: String): ByteArray =
        MessageDigest.getInstance(algorithm).digest(str)

    fun getFile3(doc:DocumentFile,path:String): DocumentFile? {
        var liste=path.split("/");
        Log.info("liste=$liste")
        liste=liste.stream().filter{
            it.length>0
        }.collect(toList())
        Log.info("liste2=$liste")
        return getFile4(doc,liste)
    }

    fun getFile4(doc:DocumentFile,path:List<String>): DocumentFile?{
        if(path.isEmpty()){
            return null;
        }
        val liste=doc.listFiles()
        for(tmp in liste){
            if(tmp.name.equals(path.get(0))){
                if(path.size==1){
                    return tmp;
                } else {
                    val res= getFile2(tmp, path.subList(1, path.size))
                    if(res!=null){
                        return res
                    }
                }
            }
        }
        return null
    }


//    fun appel(){
//
//        val config=getConfig()
////        val confFile="/sdcard/test1/test1/test1/test1/test1/config.txt";
////        val properties=Properties();
////        val input=Files.newInputStream(Paths.get(confFile));
////        properties.load(input);
//        Log.info("config=${config}")
//
//        if(config.serveur!=null&&config.serveur.trim().length>0) {
//
//
//            val queue = Volley.newRequestQueue(this)
////        val url = "https://www.google.com"
////            val url = "http://10.0.2.2:7070"
//            val url = config.serveur
//            Log.info("url=${url}")
//
//            // Request a string response from the provided URL.
//            val stringRequest = StringRequest(
//                Request.Method.GET, url,
//                { response -> // Display the first 500 characters of the response string.
//                    //textView.setText("Response is: " + response.substring(0, 500))
//                    Log.info(
//                        "Response is: " + response.substring(
//                            0,
//                            Math.min(500, response.length)
//                        )
//                    )
//
//                    Log.info("suite...")
//
//                    val rep = Environment.getExternalStorageDirectory().path
//                    Log.info("rep=$rep")
//                    val p = Paths.get("$rep/test1/test1/abc.txt")
//
//                    openDirectory(Uri.EMPTY)
//
//                    if(false) {
//
//
//                        val len = Files.size(p);
////                    val s=Files.readAllBytes(p)
//
//                        val listFiles = ListFiles(p.fileName.toString(), len)
//                        val json = Json.encodeToString(listFiles)
//
//                        val params = HashMap<String, String>()
//                        params["data"] = json
////                params["password"] = "password123"
//
//                        val stringRequest2 = object : StringRequest(
//                            Request.Method.POST, "$url/request1",
//                            Response.Listener { response -> // Display the first 500 characters of the response string.
//                                //textView.setText("Response is: " + response.substring(0, 500))
//                                Log.info(
//                                    "Response2 is: " + response.substring(
//                                        0,
//                                        Math.min(500, response.length)
//                                    )
//                                )
//
//                                traitement(queue, config);
//
//                            },
//                            Response.ErrorListener {
//                                //textView.setText("That didn't work!")
//                                Log.warning("That didn't work2! $it")
//                            }) {
//                            override fun getParams(): Map<String, String> {
//                                return params
//                            }
//                        }
//
//                        queue.add(stringRequest2)
//
//                    }
//
//                }, {
//                    //textView.setText("That didn't work!")
//                    Log.warning("That didn't work! $it")
//                })
//
//
//// Add the request to the RequestQueue.
//            queue.add(stringRequest)
//
//        }
//    }

//    val RQS_OPEN_DOCUMENT_TREE=2;
//
//    fun openDirectory(pickerInitialUri: Uri) {
//        Log.info("openDirectory")
//        // Choose a directory using the system's file picker.
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
//            // Optionally, specify a URI for the directory that should be opened in
//            // the system file picker when it loads.
//            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
//        }
//
//        //startActivityForResult(intent, RQS_OPEN_DOCUMENT_TREE)
//        resultLauncher.launch(intent)
////        registerFor
//    }

//    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // There are no request codes
//            val data: Intent? = result.data
//            val data2=result.data
//            //doSomeOperations()
//            Log.info("date: $data; ${data2?.data}")
//
//            if(data!=null&&data.data!=null) {
//                val d:Uri?=data.data;
//                if(d!=null) {
//                    val documentFile = DocumentFile.fromTreeUri(this, d)
//                    for (file in documentFile!!.listFiles()) {
//                        if (file.isDirectory) { // if it is sub directory
//                            // Do stuff with sub directory
//                            Log.info("directory: ${file.uri}")
//                        } else {
//                            // Do stuff with normal file
//
////                            file.
////                            val contentResolver: ContentResolver =
////                                getActivity().getContentResolver()
////                            val docUri = DocumentsContract.buildDocumentUriUsingTree(
////                                file.uri,
////                                DocumentsContract.getTreeDocumentId(file.uri)
////                            )
//
//                            val inputStream = contentResolver.openInputStream(file.uri)
//                            if(inputStream!=null) {
//                                val reader = BufferedReader(InputStreamReader(inputStream))
//                                val lines = reader.readLines()
//                                inputStream.close()
//                                Log.info("content: $lines")
//                            }
//                        }
//
//                        Log.info("Uri-> ${file.uri}")
//                    }
//
//                    Log.info("lecture fichier ...")
//                    val config=getConfig();
//                    // /sdcard/Documents/document/document/rep2/test7.txt
//                    var suite=""
//                    suite="/"+config.rep2+"/test7.txt"
//                    Log.info("suite: $suite")
//                    val doc2=getFile(documentFile,suite)
//                    //suite="%2Fdocument%2Fdocument%2Frep2%2Ftest7.txt"
////                    val liste=documentFile.listFiles()
////                    for(tmp in liste){
////                        if(tmp.name.equals("document")){
////
////                        }
////
////                    }
////                    val uri=documentFile.uri.toString()+suite
//                    if(doc2!=null) {
//                        val uri = doc2.uri
//                        Log.info("Fichier: $uri")
//                        if (uri != null) {
////                            val uri2 = Uri.parse(uri)
//                            Log.info("Fichier2: $uri")
////                        val documentFile2 = DocumentFile.fromTreeUri(this, uri2)
//                            val size=doc2.length()
//                            val lastMod=doc2.lastModified()
//                            Log.info("len: $size; lastMod: $lastMod")
//                            val inputStream = contentResolver.openInputStream(uri)
//                            if (inputStream != null) {
//                                val reader = BufferedReader(InputStreamReader(inputStream))
//                                val lines = reader.readLines()
//                                inputStream.close()
//                                Log.info("content2: $lines")
//                            }
//                        }
//                    } else {
//                        Log.info("pas de doc")
//                    }
//                }
//            }
//
////            if(data2!=null&&data2.data!=null) {
////                val f = File (data2.data?.path)
////                val tab=f.list()
////                Log.info("liste=$tab")
////                if(tab!=null&&tab.size>0){
//////                    tab[0]
////                }
////            }
//        }
//    }

    fun getFile(doc:DocumentFile,path:String): DocumentFile? {
        var liste=path.split("/");
        Log.info("liste=$liste")
        liste=liste.stream().filter{
            it.length>0
        }.collect(toList())
        Log.info("liste2=$liste")
        return getFile2(doc,liste)
    }

    fun getFile2(doc:DocumentFile,path:List<String>): DocumentFile?{
        if(path.isEmpty()){
            return null
        }
        val liste=doc.listFiles()
        for(tmp in liste){
            if(tmp.name.equals(path[0])){
                if(path.size==1){
                    return tmp
                } else {
                    val res= getFile2(tmp, path.subList(1, path.size))
                    if(res!=null){
                        return res
                    }
                }
            }
        }
        return null
    }

//    @Override
//    fun onActivityResult(requestCode: Int,  resultCode:Int,  data:Intent) {
//        super.onActivityResult(requestCode,resultCode,data);
//
//        Log.info("onActivityResult $requestCode, $resultCode, $data")
//
//        if(resultCode == RESULT_OK && requestCode == RQS_OPEN_DOCUMENT_TREE) {
//            val uriTree = data.data;
////            textInfo.append(uriTree.toString() + "\n");
////            textInfo.append("=====================\n");
//            Log.info("url: $uriTree")
//
//        }
//
//    }

    fun getConfig(): Config{

//        val confFile="/data/data/com.example.myapplication/test_android/config.properties";
        val confFile= this.filesDir.path +"/test_android/config.properties"
        //val confFile=this.applicationContext.filesDir.path+"/test_android/config.properties"
        Log.info("config: $confFile")
        val properties=Properties();
        val input=Files.newInputStream(Paths.get(confFile));
        properties.load(input);
        input.close()
        val serveur=properties.getProperty("serveur","")
        val rep=properties.getProperty("rep1","")
        val rep2=properties.getProperty("rep2","")
        return Config(serveur,rep,rep2)
    }

//    fun traitement(queue: RequestQueue, config: Config) {
//        val rep=Paths.get(config.rep)
//        if(Files.exists(rep)&&Files.isDirectory(rep)&&false) {
//
//            Log.info("debut")
//            val url = config.serveur
//            Log.info("url=${url}")
//            Log.info("rep=${rep}")
//
//            val liste3=rep.toFile().listFiles()
//            Log.info("liste3=$liste3")
//            val liste2= ArrayList<Files2>()
//            val stream=Files.list(rep)
//            try {
//                val liste1=stream.collect(toList())
//                Log.info("debut boucle")
//                for(f in liste1){
//                    val isDir=Files.isDirectory(f)
//                    val type=if(isDir) "D" else "F"
//                    liste2.add(Files2(f.fileName.toString(),Files.size(f),"", type))
//                    Log.info("ajout de ${liste2.last()}")
//                }
//                Log.info("fin boucle")
//            } finally {
//                stream.close()
//            }
//            val f2=rep.resolve("abc.txt");
//            Log.info("read file $f2")
//
//            val res=checkPermission(f2)
//
////            val res=Files.readAllBytes(f2)
//            Log.info("read file res: $res")
//
//            val listeFiles=ListFiles2(liste2,"aa")
//
//            val json = Json.encodeToString(listeFiles)
//
//            val params = HashMap<String, String>()
//            params["listFiles"] = json
//
//            val stringRequest2 = object : StringRequest(
//                Request.Method.POST, "$url/request2",
//                Response.Listener { response -> // Display the first 500 characters of the response string.
//                    //textView.setText("Response is: " + response.substring(0, 500))
//                    Log.info(
//                        "Response2 is: " + response.substring(
//                            0,
//                            Math.min(500, response.length)
//                        )
//                    )
//
//                    //traitement(queue, config);
//
//                },
//                Response.ErrorListener {
//                    //textView.setText("That didn't work!")
//                    Log.warning("That didn't work3! $it")
//                }) {
//                override fun getParams(): Map<String, String> {
//                    return params
//                }
//            }
//
//            queue.add(stringRequest2)
//
//            Log.info("fin")
//        } else {
//            Log.warning("le chemion $rep n'est pas un répertoire")
//        }
//    }

//    private fun checkPermission(p: Path):String {
////        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
//        if(true){
//            Log.info("permission ...")
////            Log.info("lecture2 ...")
////            val res=Files.readAllBytes(p)
////            Log.info("lecture2 ok")
////            return String(res)
//            permission2()
//            return readFile()
//        } else {
//            // L'autorisation est déjà accordée, vous pouvez accéder à la carte SD
//            Log.info("sans permission ...")
////            accessExternalStorage()
////            Log.info("lecture ...")
////            val res=Files.readAllBytes(p)
////            Log.info("lecture ok")
////            return String(res)
//            return readFile()
//        }
//    }

//    private fun permission2(){
////        val permissionsCode = REQUEST_READ_EXTERNAL_STORAGE
//        val permissionsCode = 42
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
//                    //,android.Manifest.permission.READ_MEDIA_IMAGES
//                ), permissionsCode)
//            Log.info("permission2 ...")
////            Log.info("lecture2 ...")
////            val res=Files.readAllBytes(p)
////            Log.info("lecture2 ok")
////            return String(res)
////            permission2()
//            val s= readFile()
//            Log.info("s=${s.length}")
//        } else {
//            // L'autorisation est déjà accordée, vous pouvez accéder à la carte SD
//            Log.info("sans permission2 ...")
////            accessExternalStorage()
////            Log.info("lecture ...")
////            val res=Files.readAllBytes(p)
////            Log.info("lecture ok")
////            return String(res)
////            return readFile()
//        }
//    }

//    private fun readFile():String {
//        //val p=Environment.getExternalStorageDirectory().resolve("test1/test1/test1/test1/abc.txt").toPath()
//        val p=Environment.getExternalStorageDirectory().resolve("Documents/document/text1.txt").toPath()
//        Log.info("file=$p")
//        Log.info("lecture ...")
//        val res=Files.readAllBytes(p)
//        Log.info("lecture ok")
//        return String(res)
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // L'autorisation a été accordée, vous pouvez accéder à la carte SD
                //val res=Files.readAllBytes(p)
                Log.info("accorde")
            } else {
                // L'autorisation a été refusée
                // Vous pouvez afficher un message d'erreur ou effectuer d'autres actions appropriées
                Log.info("refuse")
            }
        }
        Log.info("fin")
    }

}