package com.example.testinternet.recyclerview

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.testinternet.BackgroundThread
import com.example.testinternet.R
import com.example.testinternet.ZenRepository
import com.example.testinternet.ZenViewModel
import com.example.testinternet.data.AuthorItem
import com.example.testinternet.data.DataItem
import com.example.testinternet.interfaces.CustomAction
import com.example.testinternet.interfaces.HasCustomAction
import com.example.testinternet.interfaces.HasCustomTitle
import kotlin.math.log
import kotlin.properties.Delegates

private const val TAG = "BACKGROUND Thread ZenListArticle"
class ZenListArticle: Fragment(), HasCustomTitle {
    private lateinit var zenRecyclerViewArticle:  RecyclerView
    private lateinit var zenViewModel: ZenViewModel
    private lateinit var authorItem: AuthorItem
    lateinit var downloader: BackgroundThread<ZenHolder>
    lateinit var listDataItem: List<DataItem>
    lateinit var mHandler: Handler
    lateinit var adapter: ZenAdapter





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authorItem = AuthorItem().apply {
            url = arguments?.getString("URL") ?: ""
        }
       // zenViewModel = ViewModelProviders.of(this).get(ZenViewModel::class.java)// подключаем ViewModel

        zenViewModel = ViewModelProvider(this).get(ZenViewModel::class.java)// подключаем ViewModel

        if (savedInstanceState == null){
            zenViewModel.setUrlZenArticleList(authorItem.url)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.zen_list_article, container, false)// создаем сам контейнер и находим слой
        zenRecyclerViewArticle = view.findViewById(R.id.zenItemRecyclerView)
        zenRecyclerViewArticle.layoutManager = LinearLayoutManager(context)
        zenViewModel.authorItem(authorItem.url).observe(viewLifecycleOwner,
                Observer {
                    authorItem = it ?: authorItem
                    updateUIAuthor(authorItem)
                })
        mHandler = Handler(Looper.getMainLooper())    // handler main thread
        downloader = BackgroundThread(TAG, mHandler, null, {
            for (i in 0 until it.size){
                listDataItem[i].bitmap = it[i]
            }
            updateUI(listDataItem)
        })
        zenViewModel.dataItemLiveData.observe(
                viewLifecycleOwner,
                Observer {
                    it[0].urlPaper = it[0].urlPaper.substringBefore("&")
                    if (it[0].urlPaper != authorItem.lastArticle){
                        authorItem.lastArticle = it[0].urlPaper
                        zenViewModel.zenRepository.updateAuthorZen(authorItem)
                    }
                    val progressBar = view.findViewById<ProgressBar>(R.id.progressBarListArticle)
                    progressBar.visibility = View.GONE
                    restartUI(it)
                    listDataItem = it
                    // устраняет мигания при раскрытии статьи при нажатии
                    (zenRecyclerViewArticle.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                    val list: MutableList<String> = mutableListOf()
                    it.forEach{
                        list.add(it.urlImage)
                    }
                    downloader.returnListImage(list)
                }
        )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }


    class ZenHolder(view: View): RecyclerView.ViewHolder(view){
        val title = itemView.findViewById<TextView>(R.id.textItemTitle)
        val text = itemView.findViewById<TextView>(R.id.textItemText)
        val image = itemView.findViewById<ImageView>(R.id.imageItemImage)
    }

    inner class ZenAdapter(private val listDataItem: List<DataItem>) : RecyclerView.Adapter<ZenHolder>(){
        var data = listDataItem
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZenHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.zen_article, parent, false)
            return  ZenHolder(view)
        }

        override fun onBindViewHolder(holder: ZenHolder, position: Int) {
            val dataItem = data[position]
            // делает список раскрываемым
            holder.itemView.setOnClickListener{
                val expanded: Boolean = dataItem.vision
                dataItem.vision = !expanded
                notifyItemChanged(position)
            }

            val expended: Boolean = dataItem.vision
            if (expended) holder.text.visibility = View.VISIBLE
            else holder.text.visibility = View.GONE
            //
            holder.text.text = dataItem.text
            holder.title.text = dataItem.header
            if (dataItem.bitmap != null) {
                holder.image.setImageDrawable(BitmapDrawable(Resources.getSystem(), dataItem.bitmap))
            }
        }

        override fun getItemCount(): Int = data.size
    }
    companion object{
        fun newInstance(data: AuthorItem): ZenListArticle {
            val args = Bundle().apply {
                putParcelable("IMAGE", data.bitmap)
                putString("URL", data.url)
                putString("TITLE", data.title)
            }
            return ZenListArticle().apply {
                arguments = args
            }
        }
    }
    fun updateUIAuthor(authorItem: AuthorItem){
        val imageViewAuthor = view?.findViewById<ImageView>(R.id.imageViewAuthorDat)
        imageViewAuthor?.setImageDrawable(BitmapDrawable(Resources.getSystem(), authorItem.bitmap))
        val textViewAuthor = view?.findViewById<TextView>(R.id.textViewAuthorDat)
        textViewAuthor?.text = authorItem.title
    }

    override fun onDetach() {
        downloader.quit()
        super.onDetach()
    }
    fun restartUI(data: List<DataItem>){
        adapter = ZenAdapter(data)
        zenRecyclerViewArticle.adapter = adapter
    }
    fun updateUI(data: List<DataItem>){
        adapter.data = data
        adapter.notifyDataSetChanged()
    }

    override fun getTitle() = arguments?.getString("TITLE") ?: ""
}