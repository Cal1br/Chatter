package me.calibri.chatter.ModelClasses

class Chat {

    private var sender: String = ""
    private var message: String = ""
    private var receiver: String = ""
    private var isseen: Boolean = false
    private var url: String = ""
    private var messageId: String = ""

    constructor()
    constructor(
        sender: String,
        message: String,
        receiver: String,
        isseen: Boolean,
        url: String,
        messageId: String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isseen = isseen
        this.url = url
        this.messageId = messageId
    }


    fun getSender():String{
        return this.sender
    }
    fun setSender(sender: String){
        this.sender = sender
    }

    fun getMessage():String{
        return this.message
    }
    fun setMessage(message: String){
        this.message = message
    }

    fun getReceiver():String{
        return this.receiver
    }
    fun setReceiver(receiver: String){
        this.receiver = receiver
    }

    fun getIsSeen():Boolean{
        return this.isseen
    }
    fun setIsSeen(isSeen: Boolean){
        this.isseen = isSeen
    }

    fun getUrl():String{
        return this.url
    }
    fun setIrl(url: String){
        this.url = url
    }

    fun getMessageId():String{
        return this.messageId
    }
    fun setMessageId(messageId: String){
        this.messageId = messageId
    }
}