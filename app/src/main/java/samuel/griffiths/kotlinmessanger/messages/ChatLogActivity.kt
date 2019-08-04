package samuel.griffiths.kotlinmessanger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.chatlog_row_from.view.*
import kotlinx.android.synthetic.main.chatlog_row_to.view.*
import samuel.griffiths.kotlinmessanger.R
import samuel.griffiths.kotlinmessanger.model.ChatMessage
import samuel.griffiths.kotlinmessanger.model.User

class ChatLogActivity : AppCompatActivity() {

    val TAG = "ChatLogActivity"

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        //recycler view
        chatlog_recyclerView.adapter = adapter

        //get information about user to message to.
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username

        listenForMessages()

        chatlog_sendmessage_btn.setOnClickListener {
            Log.d(TAG, "Attempt to send message")
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage?.text)
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val fromUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, fromUser!!))
                    } else {
                        // create parcelable intent for adapter using Groupie
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }

                chatlog_recyclerView.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun performSendMessage() {

        //values to push to reference
        val text = chatlog_edittext.text.toString()
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        //        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val id = toReference.key
        val chatMessage = ChatMessage(id!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "message saved successfully: ${reference.key}")
                chatlog_edittext.text.clear()
                chatlog_recyclerView.scrollToPosition(adapter.itemCount -1)
            }

        toReference.setValue(chatMessage)

        val latestMessageFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageFromRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }


    class ChatFromItem(val text: String, val user: User) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.chatlog_row_from
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.chatlog_from_tv.text = text

            //load from user image
            val uri = user.profileImageUrl
            val targetImageView = viewHolder.itemView.chatlog_from_iv
            Picasso.get().load(uri).into(targetImageView)
        }

    }

    class ChatToItem(val text: String, val user: User) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.chatlog_row_to
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.chatlog_to_tv.text = text

            //load our user image
            val uri = user.profileImageUrl
            val targetImageView = viewHolder.itemView.chatlog_to_iv
            Picasso.get().load(uri).into(targetImageView)
        }
    }
}