package io.github.v2compose.core.analytics

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class VendorAnalytics @Inject constructor() : IAnalytics {

    private val analytics = Firebase.analytics

    override fun startTracking() {
        analytics.setAnalyticsCollectionEnabled(true)
    }

    override fun stopTracking() {
        analytics.setAnalyticsCollectionEnabled(false)
    }

}