package com.example.chatuser.base

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseActivity<VM : BaseViewModel<*>, DB : ViewDataBinding> : AppCompatActivity() {
    lateinit var viewModel: VM
    lateinit var binding: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        viewModel = initViewModel()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {

        viewModel.msgLiveData.observe(this) {
            showAlertDialog(it)
        }

        viewModel.showLoading.observe(this) {
            if (it == true)
                showLoading()
            else
                hideLoading()
        }
    }

    private fun showAlertDialog(message: String) {
        MaterialAlertDialogBuilder(this).setMessage(message).setCancelable(false)
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }.show()
    }

    fun showCustomAlertDialog(
        message: String,
        posName: String? = null,
        posAction: DialogInterface.OnClickListener? = null,
    ) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Attention")
            setMessage(message)
            setCancelable(false)
            setPositiveButton(posName, posAction)
            show()
        }
    }

    var progressDialog: ProgressDialog? = null
    private fun showLoading() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    abstract fun getLayoutId(): Int
    abstract fun initViewModel(): VM
}