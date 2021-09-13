package com.example.testinternet.database

import android.app.Activity
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.testinternet.*
import com.example.testinternet.data.AuthorItem
import com.example.testinternet.data.DataItem
import com.example.testinternet.data.ZenParser
import com.example.testinternet.interfaces.HasCustomTitle
import java.lang.Exception

private const val TAG = "BACKGROUND Thread add author"
class AddAuthor:Fragment(), View.OnClickListener, HasCustomTitle {
    var bitmap: Bitmap? = null
    lateinit var downloader: BackgroundThread<DataItem>
    lateinit var tempData: AuthorItem
    lateinit var repository: ZenRepository
    lateinit var dataAuthor: MutableLiveData<AuthorItem>
    lateinit var btOk: Button
    lateinit var btPlus: Button
    lateinit var txTitle: TextView
    lateinit var txText: TextView
    lateinit var imView: ImageView
    private lateinit var zenViewModel: ZenViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = ZenRepository.get()

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_author, container, false)// создаем сам контейнер и находим слой
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mHandler = Handler(Looper.getMainLooper())
        imView = view.findViewById(R.id.imageViewAddAuthor)
        downloader  = BackgroundThread(TAG, mHandler, { it, int ->
            val drawable = BitmapDrawable(resources, it)
            imView.setImageDrawable(drawable)
            imView.visibility = View.VISIBLE
            tempData.bitmap = it
        },
                null)
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.buttonOk).apply {
            setOnClickListener(this@AddAuthor)
        }

        btOk = view.findViewById(R.id.buttonOk)
        btPlus = view.findViewById(R.id.buttonPlus)
        txTitle = view.findViewById(R.id.textViewTitleAddAuthor)
        txText = view.findViewById(R.id.textViewDescriptorAddAuthor)
        btPlus.setOnClickListener{
                repository.addAuthorZen(tempData)
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                activity?.supportFragmentManager?.popBackStack()
        }
    }
    override fun onClick(v: View) {
// Скрытие клавиатуры
        val context = v.context
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
// Скрытие клавиатуры
        val eText = view?.findViewById<EditText>(R.id.editTextTextAddAuthor)
        var text = eText?.text
        if (text != null && eText != null){
           eText.setText(ZenParser().GetCorrectURL(text.toString()), TextView.BufferType.EDITABLE)
            text = eText.text
            dataAuthor = repository.GetZenAuthorList(text.toString())
            dataAuthor.observe(
                    viewLifecycleOwner,
                    Observer {
                        eText.visibility = View.INVISIBLE
                        btOk.visibility=View.INVISIBLE
                        tempData = it
                        txTitle.marginTop
                        txTitle.visibility = View.VISIBLE
                        txTitle.text = it.title
                        txText.visibility = View.VISIBLE
                        txText.text = it.descriptor
                        btPlus.visibility = View.VISIBLE
                        downloader.returnImage(tempData.urlImage)
                    }
            )
        }
    }

    override fun onDestroy() {
        downloader.quit()
        super.onDestroy()
    }

    override fun getTitle() = getString(R.string.add_author)


}