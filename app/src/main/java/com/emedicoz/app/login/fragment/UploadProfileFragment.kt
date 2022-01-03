package com.emedicoz.app.login.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.emedicoz.app.R
import com.emedicoz.app.customviews.imagecropper.TakeImageClass
import com.emedicoz.app.databinding.LayoutUploadProfileBinding
import com.emedicoz.app.login.activity.AuthActivity
import com.emedicoz.app.modelo.MediaFile
import com.emedicoz.app.modelo.User
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.SharedPreference
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading
import com.emedicoz.app.utilso.show
import java.util.ArrayList

class UploadProfileFragment : Fragment(), TakeImageClass.ImageFromCropper, AmazonCallBack {
    private lateinit var binding: LayoutUploadProfileBinding
    private var takeImageClass: TakeImageClass? = null
    var isPictureChanged = false
    var isDataChanged = false
    var bitmap: Bitmap? = null
    var s3ImageUploading: s3ImageUploading? = null
    var mediaFile: ArrayList<MediaFile>? = null
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getSerializable("user") as User
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutUploadProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        takeImageClass = TakeImageClass(activity, this)
        binding.apply {
            uploadPicIV.setOnClickListener {
                takeImageClass?.getImagePickerDialog(
                    activity,
                    getString(R.string.upload_profile_picture),
                    getString(R.string.choose_image)
                )
            }
            if (!GenericUtils.isEmpty(user.profile_picture)) {
                Glide.with(requireActivity()).load(user.profile_picture).into(uploadPicIV)
                isPictureChanged = true
            }

            nextBtn.setOnClickListener {
               // if (isPictureChanged) {
                    val parentFragment = (requireActivity() as AuthActivity).getCurrentFragment()
                    if (parentFragment is ProfileSubmissionFragment) {
                        parentFragment.replaceFragment(ContactDetailFragment.newInstance(user))
                        SharedPreference.getInstance().loggedInUser = user
                        parentFragment.changeBackground(
                            Const.CONTACT,
                            isContactFilled = false,
                            isAcademicFilled = false,
                            isCourseFilled = false
                        )
                    }
/*                } else {
                    requireActivity().show("Please upload Profile picture", Toast.LENGTH_SHORT)
                }*/
            }
        }

    }

    override fun imagePath(str: String?) {
        if (str != null) {
            isDataChanged = true
            bitmap = BitmapFactory.decodeFile(str)
            val ei = ExifInterface(str)
            val orientation: Int = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            var rotatedBitmap: Bitmap? = null
            rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap!!, 90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap!!, 180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap!!, 270F)
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }
            if (rotatedBitmap != null) {
                binding.uploadPicIV.setImageBitmap(rotatedBitmap)
                isPictureChanged = true
                binding.uploadPicIV.visibility = View.VISIBLE
                mediaFile = ArrayList<MediaFile>()
                val mf = MediaFile()
                mf.file_type = Const.IMAGE
                mf.image = rotatedBitmap
                mediaFile?.add(mf)
                s3ImageUploading = s3ImageUploading(
                    Const.AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES,
                    activity,
                    this,
                    null
                )
                s3ImageUploading?.execute(mediaFile)
            }
        }
    }

    override fun onS3UploadData(images: ArrayList<MediaFile>?) {
        if (images!!.isNotEmpty()) {
            var pic = images[0].file
            if (TextUtils.isEmpty(pic)) {
                pic =
                    "https://s3.ap-south-1.amazonaws.com/dams-apps-production/medicos_icon.png"
                //                user.setProfile_picture("https://s3.ap-south-1.amazonaws.com/dams-apps-production/medicos_icon.png");
            } else {
                user.profile_picture = pic
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (takeImageClass != null)
            takeImageClass?.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: ")
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    companion object {
        private const val TAG = "UploadProfileFragment"
        fun newInstance(user: User): UploadProfileFragment {
            val fragment = UploadProfileFragment()
            val bundle = Bundle()
            bundle.putSerializable("user", user)
            fragment.arguments = bundle
            return fragment
        }
    }

}