package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.IhwRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen {
    CUSTOMER_HOME,
    BOOKING_FLOW,
    BOOKING_CONFIRMATION,
    TRANSFORMATIONS_GALLERY,
    ADMIN_DASHBOARD
}

enum class AdminTab {
    TODAY,
    CALENDAR,
    BOOKINGS,
    SETTINGS
}

data class TrendingVideo(
    val id: String,
    val title: String,
    val clientName: String,
    val productName: String,
    val price: Int,
    val originalPrice: Int,
    val discountPercent: Int,
    val videoDuration: String = "0:45",
    val tags: List<String> = listOf("60 Mins Fixing", "100% Human Hair")
)

class IhwViewModel(application: Application) : AndroidViewModel(application) {
    val repository = IhwRepository(application.applicationContext)

    // Current screen mode
    private val _currentScreen = MutableStateFlow(AppScreen.CUSTOMER_HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Active bottom navigation tab in customer mode
    private val _mainTab = MutableStateFlow(MainTab.HOME)
    val mainTab: StateFlow<MainTab> = _mainTab.asStateFlow()

    // Customer audience toggle: Gents / Ladies
    private val _customerAudience = MutableStateFlow(CustomerType.MEN)
    val customerAudience: StateFlow<CustomerType> = _customerAudience.asStateFlow()

    // Booking Flow State
    private val _bookingDraft = MutableStateFlow(BookingDraft())
    val bookingDraft: StateFlow<BookingDraft> = _bookingDraft.asStateFlow()

    private val _confirmedBooking = MutableStateFlow<Booking?>(null)
    val confirmedBooking: StateFlow<Booking?> = _confirmedBooking.asStateFlow()

    private val _isBookingSubmitting = MutableStateFlow(false)
    val isBookingSubmitting: StateFlow<Boolean> = _isBookingSubmitting.asStateFlow()

    // Video modal player
    private val _activeVideo = MutableStateFlow<TrendingVideo?>(null)
    val activeVideo: StateFlow<TrendingVideo?> = _activeVideo.asStateFlow()

    // Admin state
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _adminTab = MutableStateFlow(AdminTab.TODAY)
    val adminTab: StateFlow<AdminTab> = _adminTab.asStateFlow()

    // Toast notification
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Trending videos static data
    val trendingVideos: List<TrendingVideo> = listOf(
        TrendingVideo("v1", "Hairline Restoration Live in Sodepur", "Subir M.", "Unique Gold Hair Patch", 6999, 10999, 36),
        TrendingVideo("v2", "Full Crown Balding to Stylish Fade", "Rohan S.", "0.3 mm Hair Patch Black", 26999, 39999, 33),
        TrendingVideo("v3", "Silk Base Topper Parting for Women", "Pooja B.", "Silk Base Parting Topper", 12999, 17999, 28),
        TrendingVideo("v4", "Waterproof Swimming & Gym Test", "Debayan C.", "Full Lace Hair Patch", 24999, 34999, 29)
    )

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setMainTab(tab: MainTab) {
        _mainTab.value = tab
    }

    fun setCustomerAudience(audience: CustomerType) {
        _customerAudience.value = audience
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun openVideoPlayer(video: TrendingVideo) {
        _activeVideo.value = video
    }

    fun closeVideoPlayer() {
        _activeVideo.value = null
    }

    // --- Booking Flow Methods ---
    fun startBooking(
        preselectedService: ServiceItem? = null,
        preselectedCategory: CustomerType? = null,
        preselectedIsNewWork: Boolean? = null,
        startStep: Int = 1
    ) {
        val cat = preselectedCategory ?: preselectedService?.customerType ?: _customerAudience.value
        val isNew = preselectedIsNewWork ?: preselectedService?.isNewWork ?: true
        val service = preselectedService ?: repository.services.value.firstOrNull { it.customerType == cat && it.isNewWork == isNew }
        val branch = repository.branches.value.firstOrNull { it.isDefault } ?: repository.branches.value.first()
        val defaultMethod = repository.fixingMethods.first()
        val defaultColor = repository.colorOptions.first()
        val profile = repository.userProfile.value

        _bookingDraft.value = BookingDraft(
            currentStep = startStep,
            customerType = cat,
            isNewWork = isNew,
            selectedService = service,
            selectedMethod = defaultMethod,
            selectedColor = defaultColor,
            selectedBranch = branch,
            selectedDateStr = "Oct 31, 2026",
            selectedTimeSlot = "1:00 PM",
            customerName = profile.name,
            customerPhone = profile.phone,
            customerEmail = profile.email
        )
        _currentScreen.value = AppScreen.BOOKING_FLOW
    }

    fun nextBookingStep() {
        val draft = _bookingDraft.value
        if (draft.currentStep < 6) {
            _bookingDraft.value = draft.copy(currentStep = draft.currentStep + 1)
        }
    }

    fun prevBookingStep() {
        val draft = _bookingDraft.value
        if (draft.currentStep > 1) {
            _bookingDraft.value = draft.copy(currentStep = draft.currentStep - 1)
        } else {
            _currentScreen.value = AppScreen.CUSTOMER_HOME
        }
    }

    fun jumpToBookingStep(step: Int) {
        if (step in 1..6) {
            _bookingDraft.value = _bookingDraft.value.copy(currentStep = step)
        }
    }

    fun updateDraft(updater: (BookingDraft) -> BookingDraft) {
        _bookingDraft.value = updater(_bookingDraft.value)
    }

    fun submitBooking() {
        val draft = _bookingDraft.value
        // Double booking check per category chair
        if (!repository.isSlotAvailable(draft.selectedDateStr, draft.selectedTimeSlot, draft.customerType)) {
            showToast("Sorry, this slot for ${draft.customerType.label} was just booked! Please choose another time.")
            jumpToBookingStep(5)
            return
        }

        viewModelScope.launch {
            _isBookingSubmitting.value = true
            kotlinx.coroutines.delay(400)
            val booking = repository.addBooking(draft)
            _confirmedBooking.value = booking
            _isBookingSubmitting.value = false
            _currentScreen.value = AppScreen.BOOKING_CONFIRMATION
        }
    }

    // --- Admin Operations ---
    fun verifyAdminPin(pin: String): Boolean {
        return if (pin == repository.siteConfig.value.adminPin) {
            _isAdminAuthenticated.value = true
            _currentScreen.value = AppScreen.ADMIN_DASHBOARD
            true
        } else {
            showToast("Incorrect Admin PIN! Try 1234")
            false
        }
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
        _currentScreen.value = AppScreen.CUSTOMER_HOME
    }

    fun setAdminTab(tab: AdminTab) {
        _adminTab.value = tab
    }
}
