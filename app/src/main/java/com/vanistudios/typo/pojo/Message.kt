package com.vanistudios.typo.pojo

class Message {
    var id: Long = 0
    var text: String = ""
    var room : Room = Room()
    var listNotesItems : List<ListNoteItem> = ArrayList<ListNoteItem>()

    constructor()

    constructor(text: String,roomid : Long) {
        this.listNotesItems = listNotesItems
        this.text = text
        this.room = Room(roomid)
    }

    constructor(text:String,room:Room){
        this.text = text
        this.room = room
    }

    constructor(text: String,roomid : Long,listNoteItem: List<ListNoteItem>) {
        this.listNotesItems = listNoteItem
        this.text = text
        this.room = Room(roomid)
    }


}
