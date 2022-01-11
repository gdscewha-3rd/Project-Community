package com.example.leafy2.chatbot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leafy2.R
import com.example.leafy2.databinding.FragmentChatbotBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ChatbotFragment : Fragment() {
    private var _binding: FragmentChatbotBinding?= null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var userMsgET: EditText
    private lateinit var chatbotAdapter: ChatbotAdapter
    private lateinit var sendBtn: FloatingActionButton

    lateinit var chatModelArrayList: ArrayList<ChatModel>

    companion object{
        const val USER_KEY: String = "user"
        const val BOT_KEY: String = "bot"
        const val BASE_URL: String = ""
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            userMsgET = sendMsgET
            recyclerView = chatbotRV
            sendBtn = sendFAB
        }

        chatModelArrayList = ArrayList<ChatModel>()
        chatbotAdapter = ChatbotAdapter(this, chatModelArrayList)
        recyclerView.adapter = chatbotAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        sendBtn.setOnClickListener {
            sendMsg()
        }
    }

    fun sendMsg(){
        if(userMsgET.text.toString().isEmpty()){
            Toast.makeText(context, getString(R.string.error_enter_your_msg), Toast.LENGTH_SHORT).show()
            return
        }
        getResponse(userMsgET.text.toString())
        userMsgET.text.clear()
    }

    private fun getResponse(msg: String) {
        chatModelArrayList.add(ChatModel(msg, USER_KEY))
        chatModelArrayList.add(ChatModel("This is bot msg", BOT_KEY)) // 임시 bot msg
        chatbotAdapter.notifyDataSetChanged()

        // retrofit, flask 통신
//        var url: String = " /?msg="+msg
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val retrofitAPI: RetrofitAPI = retrofit.create(RetrofitAPI::class.java)
//        val call: Call<MessageModel> = retrofitAPI.getMessage(url)
//
//        call.enqueue(object : Callback<MessageModel> {
//            override fun onResponse(call: Call<MessageModel>, response: Response<MessageModel>) {
//                if(response.isSuccessful()){
//                    val res = response.body()
//                    if (res != null) {
//                        chatModelArrayList.add(ChatModel(res.getAnswer(), BOT_KEY))
//                    }
//                    chatbotAdapter.notifyDataSetChanged()
//                }else{
//                    if(response.errorBody()!=null){
//                        Toast.makeText(context, "Failed to getResponse", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<MessageModel>, t: Throwable) {
//                chatModelArrayList.add(ChatModel("failure", BOT_KEY))
//                chatbotAdapter.notifyDataSetChanged()
//            }
//        })

    }
}