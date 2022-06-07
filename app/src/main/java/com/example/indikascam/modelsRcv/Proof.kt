package com.example.indikascam.modelsRcv

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

data class Proof (
    val image: Uri?,
    val title: String,
    val isItImage: Boolean
    )