package com.example.leafy2.user

import com.example.leafy2.calendar.RecordData

class UserData (id:String, e: String, p: String, name:String) {

    val idToken = id
    val email = e
    val password = p
    val userName = name


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

}