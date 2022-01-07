package com.example.leafy2.chatbot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.leafy2.R
import com.example.leafy2.databinding.FragmentChatbotBinding


class ChatbotFragment : Fragment() {
    private var _binding: FragmentChatbotBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}