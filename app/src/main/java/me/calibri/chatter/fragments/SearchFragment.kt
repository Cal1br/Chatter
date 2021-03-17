package me.calibri.chatter.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import me.calibri.chatter.AdaptorClasses.UserAdaptor
import me.calibri.chatter.ModelClasses.Users
import me.calibri.chatter.R

class SearchFragment : Fragment() {
    private var userAdaptor: UserAdaptor? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEditText:EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchEditText = view.findViewById(R.id.searchUserET)
        mUsers = ArrayList()
        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        return view
    }

    private fun retrieveAllUsers() {
        var firebaseUserID=FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("users")

        refUsers.addValueEventListener(object:ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                if (searchEditText!!.text.toString()==""){
                    for(snapshot in snapshot.children){

                        val user:Users? = snapshot.getValue(Users::class.java)
                        //за да не можем да намерим нашият акаунт
                        if(!(user!!.getUID().equals(firebaseUserID))){
                            (mUsers as ArrayList<Users>).add(user)

                        }
                    }
                    userAdaptor = UserAdaptor(context!!, mUsers!!,false)
                    recyclerView!!.adapter = userAdaptor
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun searchForUsers(str:String){
        var firebaseUserID=FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference.
        child("users").orderByChild("search")
            .startAt(str)
            .endAt(str+"\uf8ff")
        queryUsers.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for(snapshot in snapshot.children){

                    val user:Users? = snapshot.getValue(Users::class.java)
                    //за да не можем да намерим нашият акаунт
                    if(!(user!!.getUID()).equals(firebaseUserID)){
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdaptor = UserAdaptor(context!!, mUsers!!,false)
                recyclerView!!.adapter = userAdaptor
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}