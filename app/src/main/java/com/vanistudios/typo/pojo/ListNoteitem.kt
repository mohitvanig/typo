package com.vanistudios.typo.pojo

class ListNoteItem {
    var id : Long = 0
    var message_id : Long = 0
    var content: String = ""
    var isChecked: Boolean = false
    var list_type : String = ""

    constructor()

    constructor(content: String,isChecked : Boolean,listType : String) {
        this.content = content
        this.isChecked = isChecked
        this.list_type = listType
    }

    constructor(checkBoxItem : CheckboxItem){
        this.content = checkBoxItem.content
        this.list_type = "CHECKBOX"
        this.isChecked = checkBoxItem.isChecked
    }

}