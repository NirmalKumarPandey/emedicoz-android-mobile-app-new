package com.emedicoz.app.utilso

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

fun Context.show(message: String, length: Int) {
    Toast.makeText(this, message, length).show()
}

fun EditText.getQueryTextChangeObservable(): Observable<String> {

    val subject = PublishSubject.create<String>()

    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            subject.onNext(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
            subject.onComplete()
        }
    })

    return subject

}