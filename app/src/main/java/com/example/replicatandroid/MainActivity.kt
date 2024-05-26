package com.example.replicatandroid

import android.app.DownloadManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.replicatandroid.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val Log: Logger = Logger.getLogger(MainActivity::class.java.name)

//    var apiRequestQueue: RequestQueue? =null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        apiRequestQueue = Volley.newRequestQueue(this@MainActivity);

//        apiRequestQueue=Volley.newRequestQueue(createContext(null));

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
        appel()
    }

    fun appel(){

        val queue = Volley.newRequestQueue(this)
        val url = "https://www.google.com"


        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response -> // Display the first 500 characters of the response string.
                //textView.setText("Response is: " + response.substring(0, 500))
                Log.info("Response is: " + response.substring(0, 500))
            }, {
                //textView.setText("That didn't work!")
                Log.warning("That didn't work!")
            })


// Add the request to the RequestQueue.
        queue.add(stringRequest)

        /*val request: StringRequest =
            object : StringRequest(Request.Method.PUT, url, object : Response.Listener<String?> {
                override fun onResponse(response: String?) {

                    // hiding our progress bar.
                    loadingPB.visibility = View.GONE

                    // inside on response method we are
                    // setting our edit text to empty.
                    jobEdt.setText("")
                    nameEdt.setText("")

                    // on below line we are displaying a toast message as data updated.
                    Toast.makeText(this@MainActivity, "Data Updated..", Toast.LENGTH_SHORT).show()
                    try {
                        // on below line we are extracting data from our json object
                        // and passing our response to our json object.
                        val jsonObject = JSONObject(response)

                        // creating a string for our output.
                        val result =
                            "User Name : " + jsonObject.getString("name") + "\n" + "Job : " + jsonObject.getString(
                                "job"
                            ) + "\n" + "Updated At : " + jsonObject.getString("updatedAt")

                        // on below line we are setting
                        // our string to our text view.
                        resultTV.setText(result)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    // displaying toast message on response failure.
                    Log.e("tag", "error is " + error!!.message)
                    Toast.makeText(this@MainActivity, "Fail to update data..", Toast.LENGTH_SHORT)
                        .show()
                }
            }) {
                override fun getParams(): Map<String, String>? {

                    // below line we are creating a map for storing
                    // our values in key and value pair.
                    val params: MutableMap<String, String> = HashMap()

                    // on below line we are passing our key
                    // and value pair to our parameters.
                    params["name"] = userName
                    params["job"] = job

                    // at last we are
                    // returning our params.
                    return params
                }
            }*/
    }

}