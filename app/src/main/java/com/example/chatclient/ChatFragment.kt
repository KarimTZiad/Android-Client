package com.example.chatclient

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.chatclient.databinding.FragmentChatBinding
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.concurrent.Executors

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val args: ChatFragmentArgs by navArgs()

    private lateinit var sock : Socket
    private lateinit var outStream : DataOutputStream
    private lateinit var inStream : DataInputStream

    private val _response: MutableLiveData<String> = MutableLiveData<String>("")
    private val response: LiveData<String> get() = _response

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Executors.newSingleThreadExecutor().execute {
            connectToServer(args.ip,args.port)
        }
        requireActivity().onBackPressedDispatcher.addCallback{
            Executors.newSingleThreadExecutor().execute{
                sendToServer("CLOSE SOCKET")
            }
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ResponseMessage.inputType = InputType.TYPE_NULL
        binding.SendButton.setOnClickListener {
            val message = binding.InputMessage.text.toString()
            binding.InputMessage.setText("")
            Executors.newSingleThreadExecutor().execute{
                sendToServer(message)
            }
            if("CLOSE SOCKET" == message){
                findNavController().navigateUp()
            }
        }
        response.observe(viewLifecycleOwner, Observer{ res ->
            binding.ResponseMessage.setText(res)
        })
    }

    private fun connectToServer(ip:String, port:Int){
        sock = Socket(ip, port)
        outStream = DataOutputStream(BufferedOutputStream(sock.getOutputStream()))
        inStream = DataInputStream(BufferedInputStream(sock.getInputStream()))
    }

    private fun sendToServer(message:String){
        if(this::sock.isInitialized) {
            outStream.writeBytes(message)
            outStream.flush()
            if ("CLOSE SOCKET" == message) {
                sock.close()
                return
            }
            val bytes = ByteArray(1024)
            inStream.read(bytes)
            _response.postValue(bytes.decodeToString())
        }
    }
}