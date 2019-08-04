package samuel.griffiths.kotlinmessanger.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize  //androidextension shortcut. library not production ready.
class User(val uid: String, val username: String, val profileImageUrl: String): Parcelable{
    constructor() : this("", "", "")
}
