package com.vanistudios.typo.pojo

class CheckboxItem{
    var content: String = ""
    var isChecked: Boolean = false

    constructor()

    constructor(content:String,isChecked : Boolean){
        this.content = content
        this.isChecked = isChecked
    }

    constructor(listNoteItem : ListNoteItem){
        this.content = listNoteItem.content
        this.isChecked = listNoteItem.isChecked
    }
}
