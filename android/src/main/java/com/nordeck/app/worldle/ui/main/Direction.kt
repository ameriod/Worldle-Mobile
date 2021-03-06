package com.nordeck.app.worldle.ui.main

import androidx.annotation.DrawableRes
import com.nordeck.app.worldle.R
import com.nordeck.app.worldle.common.model.Direction

val Direction.drawableResId: Int
    @DrawableRes get() = when (this) {
        Direction.CORRECT -> R.drawable.ic_correct
        Direction.ERROR -> R.drawable.ic_error
        else -> R.drawable.ic_direction
    }
