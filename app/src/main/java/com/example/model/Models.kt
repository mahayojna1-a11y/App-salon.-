package com.example.model

enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    BENGALI("bn", "বাংলা")
}

enum class CustomerType(val label: String, val bnLabel: String) {
    MEN("Gents", "জেন্টস"),
    WOMEN("Ladies", "লেডিস")
}

enum class WorkType(val label: String, val description: String) {
    NEW_WORK("New Work", "Fresh patch fixing & styling in 60 mins"),
    SERVICING_WORK("Servicing Work", "Maintenance, cleaning & re-bonding of existing patch")
}

enum class ServiceCategory(val label: String, val bnLabel: String) {
    ALL("All", "সমস্ত"),
    PATCH("Patch", "প্যাচ"),
    WIG("Wig", "উইগ"),
    TOUPEE("Toupee", "টুপে"),
    TOPPER("Topper", "টপার"),
    EXTENSIONS("Extensions", "এক্সটেনশন"),
    SERVICING("Servicing", "সার্ভিসিং"),
    CONSULTATION("Consultation", "পরামর্শ")
}

data class ServiceItem(
    val id: String,
    val name: String,
    val durationMinutes: Int,
    val price: Int, // 0 means FREE
    val originalPrice: Int,
    val discountBadge: String,
    val isNewWork: Boolean,
    val customerType: CustomerType,
    val category: ServiceCategory,
    val description: String,
    val isPriceOnConsultation: Boolean = false,
    val imagePath: String = "public/images/services/hair-patch.jpg",
    val imageResName: String = "img_service_hair_patch"
)

data class FixingMethod(
    val id: String,
    val name: String,
    val subtitle: String,
    val isRecommended: Boolean = false
)

data class ColorOption(
    val id: String,
    val name: String,
    val hexColor: Long,
    val extraPrice: Int = 0
)

data class Branch(
    val id: String,
    val name: String,
    val address: String,
    val area: String,
    val phone: String,
    val hours: String,
    val distance: String,
    val isDefault: Boolean = false,
    val isEnabled: Boolean = true
)

enum class BookingStatus(val label: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    NO_SHOW("No-show")
}

data class Booking(
    val id: String,
    val bookingCode: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String = "",
    val notes: String = "",
    val serviceId: String,
    val serviceName: String,
    val customerType: CustomerType,
    val isNewWork: Boolean,
    val method: String,
    val color: String,
    val branchId: String,
    val branchName: String,
    val dateStr: String, // e.g. "Sat, Oct 26, 2026"
    val timeSlot: String, // e.g. "1:00 PM"
    val durationMinutes: Int,
    val totalPrice: Int,
    val status: BookingStatus = BookingStatus.CONFIRMED,
    val createdAt: Long = System.currentTimeMillis()
)

data class BookingDraft(
    val currentStep: Int = 1,
    val customerType: CustomerType = CustomerType.MEN,
    val isNewWork: Boolean = true,
    val selectedCategory: ServiceCategory = ServiceCategory.ALL,
    val searchQuery: String = "",
    val selectedService: ServiceItem? = null,
    val selectedMethod: FixingMethod? = null,
    val selectedColor: ColorOption? = null,
    val specialNote: String = "",
    val lastServiceDate: String = "",
    val selectedBranch: Branch? = null,
    val selectedDateStr: String = "",
    val selectedTimeSlot: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val userNotes: String = "",
    val agreedToContact: Boolean = true
) {
    val totalPrice: Int
        get() {
            val base = selectedService?.price ?: 0
            val colorExtra = selectedColor?.extraPrice ?: 0
            return base + colorExtra
        }
}

data class UserProfile(
    val name: String = "Client",
    val phone: String = "9830123456",
    val email: String = "client@example.com"
)

enum class LeadStatus(val label: String) {
    NEW("New"),
    CONTACTED("Contacted"),
    CONVERTED("Converted"),
    DISMISSED("Dismissed")
}

data class Lead(
    val id: String,
    val name: String,
    val phone: String,
    val preferredTime: String,
    val status: LeadStatus = LeadStatus.NEW,
    val createdAt: Long = System.currentTimeMillis()
)

data class ProductItem(
    val id: String,
    val name: String,
    val brand: String = "IHW Signature",
    val price: Int,
    val originalPrice: Int,
    val discountPercent: Int,
    val customerType: CustomerType,
    val colors: List<String>,
    val description: String,
    val isVisible: Boolean = true,
    val isWishlisted: Boolean = false
)

data class TransformationCase(
    val id: String,
    val title: String,
    val customerType: CustomerType,
    val beforeDescription: String,
    val afterDescription: String,
    val duration: String = "60 Minutes",
    val method: String = "Bonding System"
)

data class ReviewItem(
    val id: String,
    val name: String,
    val rating: Int,
    val date: String,
    val comment: String,
    val serviceUsed: String,
    val isVerified: Boolean = true
)

data class FaqItem(
    val id: String,
    val question: String,
    val answer: String,
    val bnQuestion: String,
    val bnAnswer: String
)

data class SiteConfig(
    val phone: String = "+91 89100 97668",
    val whatsAppNumber: String = "918910097668",
    val address: String = "RN Avenue, Pansila, Khardah, Sodepur, Kolkata - 700114",
    val workingHours: String = "Open 7 Days, 11:00 AM – 7:00 PM",
    val startingPrice: Int = 5999,
    val festivalBannerEnabled: Boolean = true,
    val festivalBannerText: String = "Festive Offer: Free Hair Density Consultation + Up to 35% Off on Custom Patches! Walk out in 60 mins.",
    val sharkTankEnabled: Boolean = true,
    val gentsChairCapacity: Int = 1,
    val ladiesChairCapacity: Int = 1,
    val adminPin: String = "1234"
)

enum class MainTab(val label: String, val bnLabel: String) {
    HOME("Home", "হোম"),
    SERVICES("Services", "সার্ভিস"),
    BOOKINGS("Bookings", "বুকিংস"),
    PROFILE("Profile", "প্রোফাইল")
}
