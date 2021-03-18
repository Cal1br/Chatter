package me.calibri.chatter.ModelClasses

class ChatList {
    private var id: String = ""

    constructor()

    constructor(id: String) {

    }

    fun getId():String{
        return this.id
    }
    fun setId(messageId: String){
        this.id = messageId
    }

}