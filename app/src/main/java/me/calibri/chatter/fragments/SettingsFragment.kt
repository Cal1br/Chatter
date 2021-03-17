package me.calibri.chatter.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import kotlinx.android.synthetic.main.fragment_settings.view.*
import me.calibri.chatter.ModelClasses.Users
import me.calibri.chatter.R


class SettingsFragment : Fragment() {

    var usersReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val requestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: Boolean = false
    private var socialChecker: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference =
            FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("user images")
        usersReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? =
                        snapshot.getValue(Users::class.java)

                    view.username_settings.text = user!!.getUserName()

                    if (context != null) {
                        Picasso.get().load(user.getProfile()).into(view.profile_image_settings)
                        Picasso.get().load(user.getCover()).into(view.cover_image_settings)

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        view.profile_image_settings.setOnClickListener {
            pickImage()
        }
        view.cover_image_settings.setOnClickListener {
            coverChecker = true
            pickImage()
        }

        view.set_pinterest.setOnClickListener {
            socialChecker = "pinterest"
            setSocialLinks()
        }
        view.set_tumblr.setOnClickListener {
            socialChecker = "tumblr"
            setSocialLinks()
        }
        view.set_website.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()
        }


        return view
    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        if (socialChecker == "website") {
            builder.setTitle("Enter URL:")
        } else {
            builder.setTitle("Enter username:")
        }

        val editText = EditText(context)

        if (socialChecker == "website") {
            editText.hint = "e.g. www.google.com"
        } else {
            editText.hint = "e.g. cal1br"
            //https://www.pinterest.com/atillamexmed/
            //https://www.tumblr.com/blog/cal1br-blog
        }
        builder.setView(editText)
        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            val str = editText.text.toString()
            if (str == "" || str == "null") {
                Toast.makeText(context, "Please input something", Toast.LENGTH_SHORT).show()
            } else {
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()

        })
        builder.show()
    }

    private fun saveSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()
//        mapSocialImg[] = url
//        usersReference!!.updateChildren(mapCoverImg)
        when (socialChecker) {
            //https://www.pinterest.com/atillamexmed/
            //https://www.tumblr.com/blog/cal1br-blog
            "pinterest" -> {
                //poneje ima golqm shans da bude daden s @
                val processed = str.replace("@", "")
                mapSocial["pinterest"] = "https://www.pinterest.com/${processed}/"
            }
            "tumblr" -> {
                mapSocial["tumblr"] = "https://www.pinterest.com/${str}/"
            }
            "website" -> {
                mapSocial["website"] = str
            }
        }
        usersReference!!.updateChildren(mapSocial).addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, requestCode)

    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        if (reqCode == requestCode && resultCode == Activity.RESULT_OK
            && data!!.data != null
        ) {
            imageUri = data.data
            Toast.makeText(context, "Image uploading...", Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("please wait...")
        progressBar.setTitle("Uploading")
        progressBar.show()

        if (imageUri != null) {
            //pravim go unique za da ne se zamesti sus imaage sus sushtoto ime
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".png")
            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {

                    task.exception?.let {
                        throw  it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker) {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        usersReference!!.updateChildren(mapCoverImg)
                        coverChecker = false

                    } else {
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        usersReference!!.updateChildren(mapProfileImg)
                    }
                    progressBar.dismiss()
                }
            }
        }
    }
}