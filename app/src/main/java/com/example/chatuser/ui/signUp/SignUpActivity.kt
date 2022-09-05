package com.example.chatuser.ui.signUp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.chatuser.R
import com.example.chatuser.base.BaseActivity
import com.example.chatuser.databinding.ActivitySignUpBinding
import com.example.chatuser.other.Constants
import com.example.chatuser.other.DataUtils
import com.example.chatuser.other.PreferenceManger
import com.example.chatuser.ui.home.HomeActivity
import com.example.chatuser.ui.verification.VerificationActivity
import java.io.ByteArrayOutputStream
import java.io.InputStream


class SignUpActivity : BaseActivity<SignUpViewModel, ActivitySignUpBinding>(), Navigator {
    private lateinit var encodedImage: String
    private lateinit var preferenceManger: PreferenceManger
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        viewModel.navigator = this
        preferenceManger = PreferenceManger(applicationContext)
        verificationDialog()
    }

    private fun verificationDialog() {
        viewModel.verificationDialog.observe(this) {
            when {
                it -> {
                    showCustomAlertDialog(message = "Verification email has been sent check your email",
                        posName = "OK"
                    )
                    { dialog, p1 ->
                        val user = viewModel.userToAddPref
                        preferenceManger.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                        preferenceManger.putString(Constants.KEY_USER_ID, user!!.id!!.toString())
                        preferenceManger.putString(Constants.KEY_USERNAME,
                            user.userName!!.toString())
                        preferenceManger.putString(Constants.KEY_IMAGE, user.imageId!!.toString())
                        val userf = DataUtils.firebaseUser
                        when (userf?.isEmailVerified) {
                            true -> {
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                            }
                            else -> {
                                val intent = Intent(this, VerificationActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                            }
                        }
                        dialog?.dismiss()
                    }
                }
            }
        }
    }

    private fun encodedImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }


    private val pickImage = registerForActivityResult(
        StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val data = it.data
            if (data != null) {
                val selectedImageUri = data.data
                if (selectedImageUri != null) {
                    try {
                        val inputStream: InputStream? =
                            contentResolver.openInputStream(selectedImageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.profileImage.setImageBitmap(bitmap)
                        binding.addImageText.isVisible = false
                        encodedImage = encodedImage(bitmap)
                        viewModel.imageUri.set(encodedImage)
                    } catch (e: Exception) {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_sign_up

    override fun initViewModel(): SignUpViewModel =
        ViewModelProvider(this)[SignUpViewModel::class.java]

    override fun goToSignInActivity() = onBackPressed()

    override fun openGalleryToSelectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pickImage.launch(intent)
    }
}