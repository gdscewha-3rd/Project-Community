package com.example.leafy2.chatbot

class MessageModel {
    private var answer: String = ""
    fun MessageModel(answer: String){
        this.answer = answer
    }

    fun getAnswer(): String{ return answer }
}