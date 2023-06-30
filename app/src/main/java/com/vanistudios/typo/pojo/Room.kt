package com.vanistudios.typo.pojo

class Room {
    var room_name : String = ""
    var id : Long = 0
    var messages : List<Message> = ArrayList<Message>()

    constructor()

    constructor(text: String) {
        this.room_name = text
    }

    constructor(id: Long) {
        this.id = id
    }
}