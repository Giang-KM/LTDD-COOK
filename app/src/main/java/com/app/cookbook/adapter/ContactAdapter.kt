package com.app.cookbook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.cookbook.R
import com.app.cookbook.adapter.ContactAdapter.ContactViewHolder
import com.app.cookbook.constant.GlobalFunction.onClickOpenFacebook
import com.app.cookbook.constant.GlobalFunction.onClickOpenGmail
import com.app.cookbook.constant.GlobalFunction.onClickOpenSkype
import com.app.cookbook.constant.GlobalFunction.onClickOpenYoutubeChannel
import com.app.cookbook.constant.GlobalFunction.onClickOpenZalo
import com.app.cookbook.databinding.ItemContactBinding
import com.app.cookbook.listener.ICallPhoneListener
import com.app.cookbook.model.Contact

class ContactAdapter(
    private var context: Context?,
    private val listContact: List<Contact>?,
    private val iCallPhoneListener: ICallPhoneListener
) : RecyclerView.Adapter<ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemContactBinding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ContactViewHolder(itemContactBinding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = listContact!![position]
        holder.mItemContactBinding.imgContact.setImageResource(contact.image)
        when (contact.id) {
            Contact.FACEBOOK -> holder.mItemContactBinding.tvContact.text =
                context!!.getString(R.string.label_facebook)
            Contact.HOTLINE -> holder.mItemContactBinding.tvContact.text =
                context!!.getString(R.string.label_call)
            Contact.GMAIL -> holder.mItemContactBinding.tvContact.text =
                context!!.getString(R.string.label_gmail)

        }
        holder.mItemContactBinding.layoutItem.setOnClickListener {
            when (contact.id) {
                Contact.FACEBOOK -> onClickOpenFacebook(
                    context!!
                )
                Contact.HOTLINE -> iCallPhoneListener.onClickCallPhone()
                Contact.GMAIL -> onClickOpenGmail(context!!)

            }
        }
    }

    override fun getItemCount(): Int {
        return listContact?.size ?: 0
    }

    fun release() {
        context = null
    }

    class ContactViewHolder(val mItemContactBinding: ItemContactBinding) : RecyclerView.ViewHolder(
        mItemContactBinding.root
    )
}