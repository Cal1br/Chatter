package me.calibri.chatter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import me.calibri.chatter.AdaptorClasses.UserAdaptor
import me.calibri.chatter.ModelClasses.ChatList
import me.calibri.chatter.ModelClasses.Users
import me.calibri.chatter.R

class ChatsFragment : Fragment() {

    private var userAdaptor: UserAdaptor? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var  recycler_view_chatlist : RecyclerView
    private var firebaseUser: FirebaseUser? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recycler_view_chatlist = view.findViewById(R.id.recycler_view_chatslist)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (usersChatList as ArrayList).clear()

                for(dataSnapshot in p0.children){
                    val chatList = dataSnapshot.getValue(ChatList::class.java)
                    (usersChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })



        return view
    }

    private fun retrieveChatList() {
        mUsers = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("users")
        ref!!.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()

                for(dataSnapshot in p0.children){
                    val user = dataSnapshot.getValue(Users::class.java)


                    for(eachChatList in usersChatList!!){

                        if((mUsers as ArrayList).contains(user)){
                            continue
                        }
                        if(!user!!.getUID().equals(eachChatList.getId())){
                            (mUsers as ArrayList).add(user)
                        }
                    }
                }
                userAdaptor = UserAdaptor(context!!,(mUsers as ArrayList<Users>),true)
                recycler_view_chatlist.adapter = userAdaptor
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}