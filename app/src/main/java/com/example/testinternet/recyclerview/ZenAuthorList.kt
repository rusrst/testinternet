package com.example.testinternet.recyclerview

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testinternet.R
import com.example.testinternet.ZenRepository
import com.example.testinternet.ZenViewModel
import com.example.testinternet.data.AuthorItem
import com.example.testinternet.interfaces.CustomAction
import com.example.testinternet.interfaces.HasCustomAction
import com.example.testinternet.interfaces.HasCustomTitle
import java.util.*

class ZenAuthorList: Fragment(), View.OnClickListener, HasCustomTitle, HasCustomAction {
    private var adapter : ZenAuthorAdapter? = ZenAuthorAdapter(emptyList())
    lateinit var repository: ZenRepository
    private lateinit var zenViewModel: ZenViewModel
    private var callbacks: Callbacks? = null
    lateinit var recyclerView: RecyclerView


    companion object{
        fun newInstance() = ZenAuthorList()
        private const val UPDATE = 1
        private const val NOT_UPDATE = 2
        private const val UPGRAGE = 3
    }




    interface Callbacks {
        fun onButtonPressed ()
        fun onAuthorSelected(data:AuthorItem)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


    override fun onClick(v: View) {
        callbacks?.onButtonPressed()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zenViewModel = ViewModelProvider(this).get(ZenViewModel::class.java)// подключаем ViewModel]
        repository = ZenRepository.get()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val button = view.findViewById<Button>(R.id.button)
        /*button.setOnClickListener(this)
        zenViewModel.authorListLiveData.observe(viewLifecycleOwner,
            {
                updateUI(it)
            })

         */
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.zen_list_authors, container, false)// создаем сам контейнер и находим слой
        recyclerView = view.findViewById(R.id.zenAuthorRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return view
    }



    private inner class ZenAuthorHolder (view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        private lateinit var authorItem: AuthorItem
        val channelName = view.findViewById<TextView>(R.id.textViewChannelName)
        val channelDescriptor = view.findViewById<TextView>(R.id.textViewDescriptionChannel)
        val channelImageView = view.findViewById<ImageView>(R.id.imageViewChannel)
        val buttonMoreAction = view.findViewById<ImageView>(R.id.buttonMoreAction)
        init {
            itemView.setOnClickListener(this)
            buttonMoreAction.setOnClickListener(this)
        }
        fun bind(authorItem: AuthorItem){
            this.authorItem = authorItem
        }

        override fun onClick(v: View) {
            if (v.id == R.id.buttonMoreAction) {
                showPopUpMenu(v, authorItem)
            } else {
                val callback2 = v.context as Callbacks?
                callback2?.onAuthorSelected(authorItem)

            }
        }
    }
    private inner class ZenAuthorAdapter(var authorList: List<AuthorItem>):RecyclerView.Adapter<ZenAuthorHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZenAuthorHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.zen_author, parent, false)
            return ZenAuthorHolder(view)
        }

        override fun onBindViewHolder(holder: ZenAuthorHolder, position: Int) {
            val authorItem = authorList[position]
            holder.bind(authorItem)
            holder.channelName.text = authorItem.title
            holder.channelDescriptor.text = authorItem.descriptor
            val drawable = BitmapDrawable(Resources.getSystem(), authorItem.bitmap)
            holder.channelImageView.setImageDrawable(drawable)
        }

        override fun getItemCount(): Int {
            return authorList.size
        }

    }

    fun updateUI(listOfAuthor: List<AuthorItem>){
        adapter = ZenAuthorAdapter(listOfAuthor)
        recyclerView.adapter = adapter
            }

    override fun getTitle() = getString(R.string.first_fragment)
    override fun getCustomAction(): CustomAction {
        return CustomAction(
            iconRes = R.drawable.ic_action_name,
            textRes = R.string.add_author,
            onCustomAction = Runnable {
                callbacks?.onButtonPressed()
            }
        )
    }
    private fun showPopUpMenu(v: View, authorItem: AuthorItem) {
        val popUpMenu = PopupMenu(v.context, v)
        if(!authorItem.isUpdate) popUpMenu.menu.add(0, UPDATE, Menu.NONE, "UPDATE")
        else popUpMenu.menu.add(0, NOT_UPDATE, Menu.NONE, "NOT UPDATE")
        popUpMenu.menu.add(0, UPGRAGE, Menu.NONE, "UPGRAGE")
        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId){
                UPDATE -> {
                    authorItem.isUpdate = true
                    repository.updateAuthorZen(authorItem)
                }
                NOT_UPDATE -> {
                    authorItem.isUpdate = false
                    repository.updateAuthorZen(authorItem)
                }
                UPGRAGE -> {

                }
            }
            return@setOnMenuItemClickListener true
        }
        popUpMenu.show()
    }

}