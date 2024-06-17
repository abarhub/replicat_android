package com.example.replicatandroid

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.example.replicatandroid.databinding.FragmentSecondBinding
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties
import java.util.logging.Logger
import java.util.stream.Collectors

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private val Log: Logger = Logger.getLogger(SecondFragment::class.java.name)
    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        init()

        binding.buttonSave.setOnClickListener {
            suite()
        }
    }

    private fun init() {
        Log.info("init ...")
        val nametxt = this.activity?.findViewById (R.id.edit) as EditText
        if(nametxt!=null){
            var s="Un test simple"
            val t=getFileContent()
            if(t!=null){
                s=t
            }
            nametxt.setText(s)
        }
        Log.info("init ok")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun suite() {

        Log.info("suite:")
        val config=getConfig()
        Log.info("config2: $config")

        Log.info("sauvegarde ...")

        val nametxt = this.activity?.findViewById (R.id.edit) as EditText
        if(nametxt!=null){
            val s=nametxt.text
            if(s!=null){

                val confFile = this.context?.filesDir?.path + "/test_android/config.properties"
                Log.info("config: $confFile")

                val f=Paths.get(confFile)
                val parent=f.parent
                if(Files.notExists(parent)){
                    Files.createDirectory(parent)
                }
                val s2=s.toString()
                Log.info("s: $s")
                Files.write(f,s2.toByteArray(StandardCharsets.UTF_8))
                Log.info("ecriture ok")
            }
        }

        Log.info("sauvegarde ok")
    }

    private fun getFileContent(): String? {
        if(this.context!=null) {
            val confFile = this.context?.filesDir?.path + "/test_android/config.properties"
            Log.info("config: $confFile")
            val properties = Properties();
            val input = Files.readAllLines(Paths.get(confFile), StandardCharsets.UTF_8);
//            properties.load(input);

//            input.close()
            val s= input.stream().collect(Collectors.joining("\n"))
            return s
        } else {
            return null
        }
    }

    private fun getConfig(): Config?{

//        val confFile="/data/data/com.example.myapplication/test_android/config.properties";
//        val confFile= this.filesDir.path +"/test_android/config.properties"
        if(this.context!=null) {
            val confFile = this.context?.filesDir?.path + "/test_android/config.properties"
            Log.info("config: $confFile")
            val properties = Properties();
            val input = Files.newInputStream(Paths.get(confFile));
            properties.load(input);
            input.close()
            val serveur = properties.getProperty("serveur", "")
            val rep = properties.getProperty("rep1", "")
            val rep2 = properties.getProperty("rep2", "")
            return Config(serveur, rep, rep2)
        } else {
            return null
        }
    }

}