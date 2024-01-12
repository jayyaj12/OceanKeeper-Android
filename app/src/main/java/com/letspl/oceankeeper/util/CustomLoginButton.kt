package com.letspl.oceankeeper.util

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.letspl.oceankeeper.R

class CustomLoginButton: ConstraintLayout {

    lateinit var constrLayout: ConstraintLayout
    lateinit var symbolIv: ImageView
    lateinit var loginTv: TextView

    constructor(context: Context?): super(context!!) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?): super(context!!, attrs) {
        init(context)
        getAttrs(attrs)
    }

    // 초기화
    private fun init(context: Context?) {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_login_xml, this, false)
        addView(view)

        constrLayout = findViewById(R.id.constr_layout)
        loginTv = findViewById(R.id.login_tv)
        symbolIv = findViewById(R.id.symbol_iv)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.LoginButton)
        setTypeArray(typeArray, attrs)
    }


    //디폴트 설정
    private fun setTypeArray(typedArray: TypedArray, attrs: AttributeSet?) {

        val backgroundResId = typedArray.getResourceId(
            R.styleable.LoginButton_bg,
            R.drawable.ic_launcher_foreground
        )
        constrLayout.setBackgroundResource(backgroundResId)

        val symbolImgResId = typedArray.getResourceId(
            R.styleable.LoginButton_symbol,
            R.drawable.ic_launcher_foreground
        )
        symbolIv.setImageResource(symbolImgResId)

        val textColor = typedArray.getColor(R.styleable.LoginButton_loginTextColor, 0)
        loginTv.setTextColor(textColor)

        val text = typedArray.getText(R.styleable.LoginButton_loginText)
        loginTv.text = text

        typedArray.recycle()
    }

    fun setBg(bg_resID: Int) {
        constrLayout.setBackgroundResource(bg_resID)
    }

    fun setSymbol(symbol_resID: Int) {
        symbolIv.setImageResource(symbol_resID)
    }

    fun setTextColor(color: Int) {
        loginTv.setTextColor(color)
    }

    fun setText(text_string: String?) {
        loginTv.setText(text_string)
    }

    fun setText(text_resID: Int) {
        loginTv.setText(text_resID)
    }


}