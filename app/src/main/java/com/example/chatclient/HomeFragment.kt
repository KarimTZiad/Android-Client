package com.example.chatclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatclient.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ConnectButton.setOnClickListener {
            if(checkIP() and checkPort()){
                val ip = binding.InputIP.text.toString()
                val port = binding.InputPort.text.toString().toInt()
                val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(ip, port)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkIP() : Boolean{
        val ip = binding.InputIP.text.toString()
        val values = ip.split(".")
        if(values.size!=4 || values.any{value ->
                val cur = value.toIntOrNull()
                (cur==null || cur<0 || cur>255)
        }){
            binding.InvalidIPText.visibility = View.VISIBLE
            return false
        }
        binding.InvalidIPText.visibility = View.GONE
        return true
    }

    private fun checkPort(): Boolean {
        val port = binding.InputPort.text.toString().toIntOrNull()
        if(port==null || port<1024 || port>65535){
            binding.InvalidPortText.visibility = View.VISIBLE
            return false
        }
        binding.InvalidPortText.visibility = View.GONE
        return true
    }
}