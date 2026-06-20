package com.zhiwu.app.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.vanniktech.android_image_cropper.CropImage
import com.vanniktech.android_image_cropper.CropImageContract
import com.vanniktech.android_image_cropper.CropImageContractOptions
import com.vanniktech.android_image_cropper.CropImageOptions

/**
 * 图片裁剪工具
 */
object ImageCropper {
    
    /**
     * 创建裁剪启动器
     */
    @Composable
    fun rememberCropLauncher(
        onCropped: (Uri?) -> Unit
    ) = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            onCropped(result.uriContent)
        } else {
            onCropped(null)
        }
    }
    
    /**
     * 创建裁剪选项
     */
    fun createCropOptions(
        aspectX: Int = 1,
        aspectY: Int = 1,
        outputCompressQuality: Int = 90
    ): CropImageContractOptions {
        return CropImageContractOptions(
            uri = Uri.EMPTY,
            cropImageOptions = CropImageOptions().apply {
                this.aspectRatioX = aspectX
                this.aspectRatioY = aspectY
                this.outputCompressQuality = outputCompressQuality
                this.fixAspectRatio = true
                this.outputCompressFormat = android.graphics.Bitmap.CompressFormat.JPEG
            }
        )
    }
}

/**
 * 裁剪结果数据类
 */
data class CropResult(
    val uri: Uri?,
    val success: Boolean
)
