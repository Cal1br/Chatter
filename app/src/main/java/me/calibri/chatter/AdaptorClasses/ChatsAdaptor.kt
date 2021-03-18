package me.calibri.chatter.AdaptorClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import me.calibri.chatter.ModelClasses.Chat
import me.calibri.chatter.R

class ChatsAdaptor(
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String

) : RecyclerView.Adapter<ChatsAdaptor.ViewHolder?>() {
    private val mContext: Context
    private val mChatList: List<Chat>
    private val imageUrl: String
    val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mChatList = mChatList
        this.mContext = mContext
        this.imageUrl = imageUrl
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1) {
            val view: View = LayoutInflater.from(mContext)
                .inflate(me.calibri.chatter.R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(mContext)
                .inflate(me.calibri.chatter.R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profile_image)


        /*като подаваме снимка, ако ние сме я пратили, отива в дясната страна
        * ако друг я е пратил, отива в ляавата страна.*/
        if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){
            //image message - right side/ sender
            if (chat.getSender().equals(firebaseUser!!.uid)) {
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.right_image_view)

            }
            //image sender - left side
            else if (!(chat.getSender().equals(firebaseUser!!.uid))) {
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.left_image_view)
            }
        }
        /*и тук имаме текстовите съобщения*/
        else{
            holder.show_text_message!!.text = chat.getMessage()
        }
        //sent and seen message

        if(position == mChatList.size-1){ //трябва да има съобщения
            if(chat.getIsSeen()){
                holder.text_seen!!.text = "Seen"
                if(chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){
                    val lp : RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
            else{
                holder.text_seen!!.text = "Sent"
                if(chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){
                    val lp : RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
        }
        else{
            holder.text_seen!!.visibility = View.GONE
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_image: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)
        }
    }

    override fun getItemViewType(position: Int): Int {

         if (mChatList[position].getSender() == firebaseUser!!.uid) { //ако съобщението е нашо
           return 1
        } else {
           return 0
        }

    }

}