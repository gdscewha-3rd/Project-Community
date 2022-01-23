package com.example.leafy2

import com.example.leafy2.calendar.RecordData

class UserData {
    private lateinit var idToken: String

    private lateinit var email: String

    private lateinit var password: String

    private lateinit var userName: String


    var recordList: MutableList<RecordData> = mutableListOf()


    fun addRecord(rec: RecordData){
        recordList.add(rec)
    }
    fun getRecordDate(pos: Int): String {
        return recordList[pos].date
    }

    fun getRecordListOfTheDate(date: String): List<RecordData>{
        var recList: MutableList<RecordData> = arrayListOf()

        for( i in 0 until recordList.size){
            if(recordList[i].date==date){
                recList.add(recordList[i])
            }
        }

        return recList
    }

    fun setIdToken(id: String){
        idToken = id
    }
    fun setEmail(e:String) {
        email = e
    }
    fun setPassWord(p:String){
        password = p
    }
    fun setUserName(u:String){
        userName = u
    }
}