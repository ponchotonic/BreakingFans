package com.alfonsocastro.breakingfans

import android.app.Application
import com.alfonsocastro.breakingfans.data.local.BreakingBadDatabase

class BreakingBadApplication : Application() {
    val database: BreakingBadDatabase by lazy { BreakingBadDatabase.getDatabase(this) }
}