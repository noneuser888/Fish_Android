package com.wantime.wbangapp.ui.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.wantime.wbangapp.R
import kotlinx.android.synthetic.main.widget_search.view.*


class SearchView : ConstraintLayout {
    private var mListener: SearchViewListener? = null

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs, defStyleAttr)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs, -1)
    }

    constructor(context: Context) : super(context) {
        initView(context, null, -1)
    }

    private fun initView(mContext: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val inflater =
            mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.widget_search, this, true)
        search_edit.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //点击搜索的时候隐藏软键盘
                hideKeyboard(search_edit)
                // 在这里写搜索的操作,一般都是网络请求数据
                if (this.mListener != null) this.mListener!!.onSearch(search_edit.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
        search_edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                clearButton.visibility =
                    if (p0 != null && p0.isNotEmpty()) View.VISIBLE else View.GONE
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        //搜索
        mSearchButton.setOnClickListener {
            //点击搜索的时候隐藏软键盘
            hideKeyboard(search_edit)
            // 在这里写搜索的操作,一般都是网络请求数据
            if (this.mListener != null) this.mListener!!.onSearch(search_edit.text.toString())
        }


        clearButton.setOnClickListener {
            search_edit.setText("")
            this.mListener?.onSearch("")
        }
    }

    private fun hideKeyboard(view: View) {
        val manager: InputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun addSearchViewListener(mListener: SearchViewListener) {
        this.mListener = mListener
    }


    interface SearchViewListener {
        fun onSearch(mText: String)
    }

}