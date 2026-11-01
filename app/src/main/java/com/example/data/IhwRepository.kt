package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class IhwRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ihw_prefs", Context.MODE_PRIVATE)

    // Standard 1-hour slots from 11 AM to 7 PM
    val standardHourSlots = listOf(
        "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM",
        "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM"
    )

    // Site Config Flow
    private val _siteConfig = MutableStateFlow(loadConfig())
    val siteConfig: StateFlow<SiteConfig> = _siteConfig.asStateFlow()

    // Branches Flow
    private val _branches = MutableStateFlow(defaultBranches())
    val branches: StateFlow<List<Branch>> = _branches.asStateFlow()

    // Services Flow
    private val _services = MutableStateFlow(defaultServices())
    val services: StateFlow<List<ServiceItem>> = _services.asStateFlow()

    // Products Flow
    private val _products = MutableStateFlow(loadProducts())
    val products: StateFlow<List<ProductItem>> = _products.asStateFlow()

    // Bookings Flow
    private val _bookings = MutableStateFlow(loadBookings())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    // User Profile Flow
    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Leads Flow
    private val _leads = MutableStateFlow(loadLeads())
    val leads: StateFlow<List<Lead>> = _leads.asStateFlow()

    // Language Flow
    private val _currentLanguage = MutableStateFlow(loadLanguage())
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    // User Phone Memory
    var rememberedPhone: String
        get() = prefs.getString("user_phone", "9830123456") ?: "9830123456"
        set(value) = prefs.edit().putString("user_phone", value).apply()

    // Methods & Colors
    val fixingMethods: List<FixingMethod> = listOf(
        FixingMethod("bonding", "Bonding", "Secure adhesive for active lifestyle", isRecommended = true),
        FixingMethod("clipping", "Clipping", "Easy daily removable clips"),
        FixingMethod("taping", "Taping", "Hypoallergenic double-sided tape"),
        FixingMethod("weaving", "Weaving", "Micro-track attachment without glue")
    )

    val colorOptions: List<ColorOption> = listOf(
        ColorOption("black", "Natural Black", 0xFF111111, extraPrice = 0),
        ColorOption("brown", "Dark Brown", 0xFF3D2314, extraPrice = 500),
        ColorOption("copper", "Copper Brown", 0xFF783F1C, extraPrice = 800),
        ColorOption("highlights", "Highlights Blend", 0xFFA67B51, extraPrice = 1200),
        ColorOption("burgundy", "Burgundy", 0xFF58111A, extraPrice = 1000)
    )

    fun setLanguage(lang: Language) {
        _currentLanguage.value = lang
        prefs.edit().putString("app_lang", lang.code).apply()
    }

    private fun loadLanguage(): Language {
        val code = prefs.getString("app_lang", "en") ?: "en"
        return if (code == "bn") Language.BENGALI else Language.ENGLISH
    }

    fun getAvailableTimeSlots(): List<String> = listOf(
        "11:00 AM",
        "12:00 PM",
        "1:00 PM",
        "2:00 PM",
        "3:00 PM",
        "4:00 PM",
        "5:00 PM",
        "6:00 PM"
    )

    // Chair availability check
    fun isSlotAvailable(dateStr: String, timeSlot: String, category: CustomerType): Boolean {
        val capacity = if (category == CustomerType.MEN) _siteConfig.value.gentsChairCapacity else _siteConfig.value.ladiesChairCapacity
        val activeBookings = _bookings.value.count {
            it.dateStr == dateStr &&
            it.timeSlot == timeSlot &&
            it.customerType == category &&
            it.status != BookingStatus.CANCELLED
        }
        return activeBookings < capacity
    }

    // --- Bookings Operations ---
    fun addBooking(draft: BookingDraft): Booking {
        val service = draft.selectedService ?: defaultServices().first()
        val branch = draft.selectedBranch ?: defaultBranches().first()
        val code = "IHW-${System.currentTimeMillis() % 1000000}"
        val newBooking = Booking(
            id = "bk_${System.currentTimeMillis()}",
            bookingCode = code,
            customerName = draft.customerName.trim().ifEmpty { _userProfile.value.name },
            customerPhone = draft.customerPhone.trim().ifEmpty { rememberedPhone },
            customerEmail = draft.customerEmail.trim().ifEmpty { _userProfile.value.email },
            notes = if (draft.specialNote.isNotBlank()) "${draft.specialNote} | ${draft.userNotes}" else draft.userNotes,
            serviceId = service.id,
            serviceName = service.name,
            customerType = draft.customerType,
            isNewWork = draft.isNewWork,
            method = if (draft.isNewWork) (draft.selectedMethod?.name ?: "Bonding") else "Servicing",
            color = draft.selectedColor?.name ?: "Natural Black",
            branchId = branch.id,
            branchName = branch.name,
            dateStr = draft.selectedDateStr,
            timeSlot = draft.selectedTimeSlot,
            durationMinutes = service.durationMinutes,
            totalPrice = draft.totalPrice,
            status = BookingStatus.CONFIRMED,
            createdAt = System.currentTimeMillis()
        )
        val updated = listOf(newBooking) + _bookings.value
        _bookings.value = updated
        saveBookings(updated)
        rememberedPhone = newBooking.customerPhone
        return newBooking
    }

    fun updateBookingStatus(bookingId: String, newStatus: BookingStatus) {
        val updated = _bookings.value.map {
            if (it.id == bookingId) it.copy(status = newStatus) else it
        }
        _bookings.value = updated
        saveBookings(updated)
    }

    fun rescheduleBooking(bookingId: String, newDate: String, newTime: String) {
        val updated = _bookings.value.map {
            if (it.id == bookingId) it.copy(dateStr = newDate, timeSlot = newTime, status = BookingStatus.CONFIRMED) else it
        }
        _bookings.value = updated
        saveBookings(updated)
    }

    fun addWalkInBooking(
        customerName: String,
        customerPhone: String,
        customerType: CustomerType,
        isNewWork: Boolean,
        service: ServiceItem,
        dateStr: String,
        timeSlot: String,
        notes: String = "Walk-in client"
    ): Booking {
        val code = "IHW-${System.currentTimeMillis() % 1000000}"
        val newBooking = Booking(
            id = "bk_${System.currentTimeMillis()}",
            bookingCode = code,
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            customerEmail = "",
            notes = notes,
            serviceId = service.id,
            serviceName = service.name,
            customerType = customerType,
            isNewWork = isNewWork,
            method = if (isNewWork) "Bonding System" else "Servicing",
            color = "Natural Black",
            branchId = "br_sodepur",
            branchName = "Sodepur (Main Studio)",
            dateStr = dateStr,
            timeSlot = timeSlot,
            durationMinutes = service.durationMinutes,
            totalPrice = service.price,
            status = BookingStatus.CONFIRMED,
            createdAt = System.currentTimeMillis()
        )
        val updated = listOf(newBooking) + _bookings.value
        _bookings.value = updated
        saveBookings(updated)
        return newBooking
    }

    fun updateServicePrice(serviceId: String, newPrice: Int) {
        val updated = _services.value.map {
            if (it.id == serviceId) it.copy(price = newPrice) else it
        }
        _services.value = updated
    }

    // --- User Profile Operations ---
    fun saveUserProfile(name: String, phone: String, email: String) {
        val profile = UserProfile(name = name.trim(), phone = phone.trim(), email = email.trim())
        _userProfile.value = profile
        rememberedPhone = profile.phone
        prefs.edit()
            .putString("u_name", profile.name)
            .putString("u_phone", profile.phone)
            .putString("u_email", profile.email)
            .apply()
    }

    private fun loadUserProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("u_name", "Rahul Mukherjee") ?: "Rahul Mukherjee",
            phone = prefs.getString("u_phone", "9830123456") ?: "9830123456",
            email = prefs.getString("u_email", "rahul@gmail.com") ?: "rahul@gmail.com"
        )
    }

    // --- Leads Operations ---
    fun addLead(name: String, phone: String, preferredTime: String): Lead {
        val lead = Lead(
            id = "ld_${System.currentTimeMillis()}",
            name = name.trim(),
            phone = phone.trim(),
            preferredTime = preferredTime.trim(),
            status = LeadStatus.NEW,
            createdAt = System.currentTimeMillis()
        )
        val updated = listOf(lead) + _leads.value
        _leads.value = updated
        saveLeads(updated)
        return lead
    }

    fun updateLeadStatus(leadId: String, status: LeadStatus) {
        val updated = _leads.value.map {
            if (it.id == leadId) it.copy(status = status) else it
        }
        _leads.value = updated
        saveLeads(updated)
    }

    // --- Products Operations ---
    fun toggleProductVisibility(productId: String) {
        val updated = _products.value.map {
            if (it.id == productId) it.copy(isVisible = !it.isVisible) else it
        }
        _products.value = updated
        saveProducts(updated)
    }

    fun saveProduct(product: ProductItem) {
        val existing = _products.value.any { it.id == product.id }
        val updated = if (existing) {
            _products.value.map { if (it.id == product.id) product else it }
        } else {
            _products.value + product
        }
        _products.value = updated
        saveProducts(updated)
    }

    fun deleteProduct(productId: String) {
        val updated = _products.value.filterNot { it.id == productId }
        _products.value = updated
        saveProducts(updated)
    }

    // --- Config Operations ---
    fun updateConfig(config: SiteConfig) {
        _siteConfig.value = config
        prefs.edit()
            .putString("cfg_phone", config.phone)
            .putString("cfg_wa", config.whatsAppNumber)
            .putString("cfg_addr", config.address)
            .putString("cfg_hours", config.workingHours)
            .putInt("cfg_price", config.startingPrice)
            .putBoolean("cfg_fest_on", config.festivalBannerEnabled)
            .putString("cfg_fest_text", config.festivalBannerText)
            .putBoolean("cfg_shark_on", config.sharkTankEnabled)
            .putInt("cfg_gents_cap", config.gentsChairCapacity)
            .putInt("cfg_ladies_cap", config.ladiesChairCapacity)
            .putString("cfg_pin", config.adminPin)
            .apply()
    }

    fun toggleBranch(branchId: String) {
        val updated = _branches.value.map {
            if (it.id == branchId) it.copy(isEnabled = !it.isEnabled) else it
        }
        _branches.value = updated
    }

    // --- Private Persistence Helpers ---
    private fun loadConfig(): SiteConfig {
        return SiteConfig(
            phone = prefs.getString("cfg_phone", "+91 89100 97668") ?: "+91 89100 97668",
            whatsAppNumber = prefs.getString("cfg_wa", "918910097668") ?: "918910097668",
            address = prefs.getString("cfg_addr", "RN Avenue, Pansila, Khardah, Sodepur, Kolkata - 700114") ?: "RN Avenue, Pansila, Khardah, Sodepur, Kolkata - 700114",
            workingHours = prefs.getString("cfg_hours", "Open 7 Days, 11:00 AM – 7:00 PM") ?: "Open 7 Days, 11:00 AM – 7:00 PM",
            startingPrice = prefs.getInt("cfg_price", 5999),
            festivalBannerEnabled = prefs.getBoolean("cfg_fest_on", true),
            festivalBannerText = prefs.getString("cfg_fest_text", "Festive Offer: Free Hair Density Consultation + Up to 35% Off on Custom Patches! Walk out in 60 mins.") ?: "",
            sharkTankEnabled = prefs.getBoolean("cfg_shark_on", true),
            gentsChairCapacity = prefs.getInt("cfg_gents_cap", 1),
            ladiesChairCapacity = prefs.getInt("cfg_ladies_cap", 1),
            adminPin = prefs.getString("cfg_pin", "1234") ?: "1234"
        )
    }

    private fun loadProducts(): List<ProductItem> {
        val jsonStr = prefs.getString("ihw_products", null)
        if (jsonStr.isNullOrEmpty()) {
            return defaultProducts()
        }
        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<ProductItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val colorsArr = obj.optJSONArray("colors") ?: JSONArray()
                val colors = (0 until colorsArr.length()).map { colorsArr.getString(it) }
                list.add(
                    ProductItem(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        brand = obj.optString("brand", "IHW Signature"),
                        price = obj.getInt("price"),
                        originalPrice = obj.getInt("originalPrice"),
                        discountPercent = obj.getInt("discountPercent"),
                        customerType = CustomerType.valueOf(obj.getString("customerType")),
                        colors = colors,
                        description = obj.optString("description", ""),
                        isVisible = obj.optBoolean("isVisible", true),
                        isWishlisted = false
                    )
                )
            }
            list
        } catch (e: Exception) {
            defaultProducts()
        }
    }

    private fun saveProducts(list: List<ProductItem>) {
        val arr = JSONArray()
        list.forEach { p ->
            val obj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("brand", p.brand)
                put("price", p.price)
                put("originalPrice", p.originalPrice)
                put("discountPercent", p.discountPercent)
                put("customerType", p.customerType.name)
                put("colors", JSONArray(p.colors))
                put("description", p.description)
                put("isVisible", p.isVisible)
            }
            arr.put(obj)
        }
        prefs.edit().putString("ihw_products", arr.toString()).apply()
    }

    private fun loadBookings(): List<Booking> {
        val jsonStr = prefs.getString("ihw_bookings", null)
        if (jsonStr.isNullOrEmpty()) {
            return defaultBookings()
        }
        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<Booking>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    Booking(
                        id = obj.getString("id"),
                        bookingCode = obj.getString("bookingCode"),
                        customerName = obj.getString("customerName"),
                        customerPhone = obj.getString("customerPhone"),
                        customerEmail = obj.optString("customerEmail", ""),
                        notes = obj.optString("notes", ""),
                        serviceId = obj.getString("serviceId"),
                        serviceName = obj.getString("serviceName"),
                        customerType = CustomerType.valueOf(obj.getString("customerType")),
                        isNewWork = obj.optBoolean("isNewWork", true),
                        method = obj.getString("method"),
                        color = obj.getString("color"),
                        branchId = obj.getString("branchId"),
                        branchName = obj.getString("branchName"),
                        dateStr = obj.getString("dateStr"),
                        timeSlot = obj.getString("timeSlot"),
                        durationMinutes = obj.getInt("durationMinutes"),
                        totalPrice = obj.getInt("totalPrice"),
                        status = BookingStatus.valueOf(obj.optString("status", "CONFIRMED")),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            defaultBookings()
        }
    }

    private fun saveBookings(list: List<Booking>) {
        val arr = JSONArray()
        list.forEach { b ->
            val obj = JSONObject().apply {
                put("id", b.id)
                put("bookingCode", b.bookingCode)
                put("customerName", b.customerName)
                put("customerPhone", b.customerPhone)
                put("customerEmail", b.customerEmail)
                put("notes", b.notes)
                put("serviceId", b.serviceId)
                put("serviceName", b.serviceName)
                put("customerType", b.customerType.name)
                put("isNewWork", b.isNewWork)
                put("method", b.method)
                put("color", b.color)
                put("branchId", b.branchId)
                put("branchName", b.branchName)
                put("dateStr", b.dateStr)
                put("timeSlot", b.timeSlot)
                put("durationMinutes", b.durationMinutes)
                put("totalPrice", b.totalPrice)
                put("status", b.status.name)
                put("createdAt", b.createdAt)
            }
            arr.put(obj)
        }
        prefs.edit().putString("ihw_bookings", arr.toString()).apply()
    }

    private fun loadLeads(): List<Lead> {
        val jsonStr = prefs.getString("ihw_leads", null)
        if (jsonStr.isNullOrEmpty()) {
            return defaultLeads()
        }
        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<Lead>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    Lead(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        phone = obj.getString("phone"),
                        preferredTime = obj.getString("preferredTime"),
                        status = LeadStatus.valueOf(obj.optString("status", "NEW")),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            defaultLeads()
        }
    }

    private fun saveLeads(list: List<Lead>) {
        val arr = JSONArray()
        list.forEach { l ->
            val obj = JSONObject().apply {
                put("id", l.id)
                put("name", l.name)
                put("phone", l.phone)
                put("preferredTime", l.preferredTime)
                put("status", l.status.name)
                put("createdAt", l.createdAt)
            }
            arr.put(obj)
        }
        prefs.edit().putString("ihw_leads", arr.toString()).apply()
    }

    // Default Branches
    private fun defaultBranches(): List<Branch> = listOf(
        Branch("sodepur", "Sodepur", "Station Road, RN Avenue, Pansila, Khardah", "Sodepur, Kolkata", "+91 89100 97668", "Open 11 AM - 7 PM", "0.8 km", isDefault = true, isEnabled = true),
        Branch("durgapur", "Durgapur", "City Centre, Near Junction Mall", "Durgapur", "+91 89100 97668", "Open 11 AM - 7 PM", "165 km", isDefault = false, isEnabled = true),
        Branch("lake_gardens", "Lake Gardens", "Prince Anwar Shah Road", "South Kolkata", "+91 89100 97668", "Open 11 AM - 7 PM", "22 km", isDefault = false, isEnabled = true),
        Branch("salt_lake", "Salt Lake", "Sector 1, Near City Centre 1", "East Kolkata", "+91 89100 97668", "Open 11 AM - 7 PM", "16 km", isDefault = false, isEnabled = true),
        Branch("siliguri", "Siliguri", "Sevoke Road, Planet Mall Complex", "North Bengal", "+91 89100 97668", "Open 11 AM - 7 PM", "560 km", isDefault = false, isEnabled = true)
    )

    // Standard Services as prescribed by prompt section 6
    private fun defaultServices(): List<ServiceItem> = listOf(
        // Gents Services
        ServiceItem("srv_g_patch", "Hair Patch Fixing", 60, 5999, 8999, "33% OFF", isNewWork = true, CustomerType.MEN, ServiceCategory.PATCH, "100% human hair custom patch fixing with natural undetectable hairline.", imagePath = "public/images/services/hair-patch.jpg", imageResName = "img_service_hair_patch"),
        ServiceItem("srv_g_servicing", "Hair Patch Servicing", 45, 999, 1499, "33% OFF", isNewWork = false, CustomerType.MEN, ServiceCategory.SERVICING, "Deep scalp clean, anti-fungal hygiene, re-bonding and re-styling.", imagePath = "public/images/services/patch-servicing.jpg", imageResName = "img_service_patch_servicing"),
        ServiceItem("srv_g_wig", "Hair Wig for Men", 60, 18999, 24000, "20% OFF", isNewWork = true, CustomerType.MEN, ServiceCategory.WIG, "Full coverage natural scalp base wig customized for perfect head shape.", imagePath = "public/images/services/wig.jpg", imageResName = "img_service_wig"),
        ServiceItem("srv_g_topper", "Hair Topper", 60, 8999, 12000, "25% OFF", isNewWork = true, CustomerType.MEN, ServiceCategory.TOPPER, "Crown volume and vertex hair integration with natural parting.", imagePath = "public/images/services/topper.jpg", imageResName = "img_service_topper"),
        ServiceItem("srv_g_consult", "Free Consultation", 30, 0, 500, "FREE", isNewWork = true, CustomerType.MEN, ServiceCategory.CONSULTATION, "Scalp density analysis, custom system selection and zero-obligation trial.", imagePath = "public/images/services/consultation.jpg", imageResName = "img_service_consultation"),

        // Ladies Services
        ServiceItem("srv_l_patch", "Women's Hair Patch", 60, 6999, 9999, "30% OFF", isNewWork = true, CustomerType.WOMEN, ServiceCategory.PATCH, "Frontal lace hair patch for female hairline recession in private cabin.", imagePath = "public/images/services/hair-patch.jpg", imageResName = "img_service_hair_patch"),
        ServiceItem("srv_l_servicing", "Women's Patch Servicing", 45, 999, 1499, "33% OFF", isNewWork = false, CustomerType.WOMEN, ServiceCategory.SERVICING, "Gentle care, wash, conditioning and re-bonding by lady specialists.", imagePath = "public/images/services/patch-servicing.jpg", imageResName = "img_service_patch_servicing"),
        ServiceItem("srv_l_extensions", "Hair Extensions", 90, 15000, 19999, "25% OFF", isNewWork = true, CustomerType.WOMEN, ServiceCategory.EXTENSIONS, "Cold fusion micro-ring extension with 50 strands virgin Indian remy hair.", imagePath = "public/images/services/extensions.jpg", imageResName = "img_service_extensions"),
        ServiceItem("srv_l_wig", "Women's Wig", 60, 14999, 21000, "28% OFF", isNewWork = true, CustomerType.WOMEN, ServiceCategory.WIG, "Ultra-soft silk base medical and fashion wigs gentle on delicate scalp.", imagePath = "public/images/services/wig.jpg", imageResName = "img_service_wig"),
        ServiceItem("srv_l_topper", "Hair Topper", 60, 12999, 16999, "24% OFF", isNewWork = true, CustomerType.WOMEN, ServiceCategory.TOPPER, "Seamless crown volume solution for female pattern thinning and parting.", imagePath = "public/images/services/topper.jpg", imageResName = "img_service_topper"),
        ServiceItem("srv_l_consult", "Free Consultation", 30, 0, 500, "FREE", isNewWork = true, CustomerType.WOMEN, ServiceCategory.CONSULTATION, "Private 1-on-1 consultation with experienced female hair consultant.", imagePath = "public/images/services/consultation.jpg", imageResName = "img_service_consultation")
    )

    private fun defaultProducts(): List<ProductItem> = listOf(
        // Gents Products
        ProductItem("p1", "Unique Gold Hair Patch", "IHW Signature", 6999, 10999, 36, CustomerType.MEN, listOf("Natural Black", "Dark Brown"), "French lace front with mono top, highest heat and sweat resistance."),
        ProductItem("p2", "Full Lace Hair Patch", "IHW Masterpiece", 24999, 34999, 29, CustomerType.MEN, listOf("Natural Black", "Dark Brown"), "100% Swiss lace with bleached knots, totally invisible hairline."),
        ProductItem("p3", "0.3 mm Hair Patch Black", "IHW Feather", 26999, 39999, 33, CustomerType.MEN, listOf("Natural Black"), "Ultra thin 0.3mm polymer skin system that melts seamlessly into scalp."),
        ProductItem("p4", "Model Hair Patch Black", "IHW Signature", 6999, 9999, 30, CustomerType.MEN, listOf("Natural Black"), "Affordable, durable daily wear hair patch with natural wave pattern."),

        // Ladies Products
        ProductItem("p5", "Micro Ring Extension (50 Strands)", "IHW Silk", 15000, 18999, 21, CustomerType.WOMEN, listOf("Natural Black", "Copper Brown", "Highlights", "Burgundy"), "Premium virgin Indian remy human hair extensions for length and luxury volume."),
        ProductItem("p6", "Silk Base Parting Topper", "IHW Silk", 12999, 17999, 28, CustomerType.WOMEN, listOf("Natural Black", "Dark Brown"), "Natural multi-directional skin part topper with feather-light breathable mesh."),
        ProductItem("p7", "Invisible Front Lace Wig", "IHW Silk", 28000, 36000, 22, CustomerType.WOMEN, listOf("Natural Black", "Dark Brown"), "Natural scalp illusion with 100% human remy hair."),
        ProductItem("p8", "Clip-in Volumizer Streaks", "IHW Silk", 4999, 6999, 28, CustomerType.WOMEN, listOf("Natural Black", "Burgundy", "Highlights"), "Instant volume for parties and weddings with zero damage.")
    )

    private fun defaultBookings(): List<Booking> = listOf(
        Booking("b1", "IHW-241026-0042", "Rahul Mukherjee", "9830123456", "rahul@gmail.com", "Natural low fade styling", "srv_g_patch", "Hair Patch Fixing", CustomerType.MEN, true, "Bonding", "Natural Black", "sodepur", "Sodepur", "Today, Oct 31, 2026", "2:00 PM", 60, 5999, BookingStatus.CONFIRMED, System.currentTimeMillis() - 3600000),
        Booking("b2", "IHW-241026-0089", "Ananya Sen", "9874561234", "ananya@yahoo.com", "Crown topper trial", "srv_l_topper", "Hair Topper", CustomerType.WOMEN, true, "Clipping", "Dark Brown", "sodepur", "Sodepur", "Today, Oct 31, 2026", "2:00 PM", 60, 12999, BookingStatus.CONFIRMED, System.currentTimeMillis() - 7200000),
        Booking("b3", "IHW-241025-0012", "Subhashis Roy", "9432188765", "", "Routine monthly servicing", "srv_g_servicing", "Hair Patch Servicing", CustomerType.MEN, false, "Servicing", "Natural Black", "sodepur", "Sodepur", "Yesterday", "12:00 PM", 45, 999, BookingStatus.COMPLETED, System.currentTimeMillis() - 86400000)
    )

    private fun defaultLeads(): List<Lead> = listOf(
        Lead("ld1", "Amit Ghosh", "+91 98311 99882", "Morning 11:30 AM", LeadStatus.NEW, System.currentTimeMillis() - 5400000),
        Lead("ld2", "Priyanka Mondal", "+91 90070 44551", "Evening 5:00 PM", LeadStatus.CONTACTED, System.currentTimeMillis() - 28800000)
    )

    fun getTransformations(): List<TransformationCase> = listOf(
        TransformationCase("t1", "Receding Hairline to Dense Natural Crop", CustomerType.MEN, "Stage 4 male pattern baldness with deep frontal recession", "Seamless 0.3mm skin patch with custom fade, undetectable hairline", "60 mins", "Medical Grade Bonding"),
        TransformationCase("t2", "Crown Baldness to Youthful Volume", CustomerType.MEN, "Severe vertex thinning affecting daily confidence", "Mono-filament base patch matched to natural side density", "55 mins", "Waterproof Bonding"),
        TransformationCase("t3", "Female Wide Parting to Silk Crown Volume", CustomerType.WOMEN, "Widening center partition due to hormonal thinning", "Hand-tied silk base topper blended seamlessly with natural locks", "45 mins", "Soft Micro-Clips"),
        TransformationCase("t4", "Fine Thin Hair to 24\" Luxurious Length", CustomerType.WOMEN, "Shoulder-length fine hair lacking volume and body", "100 Strands Micro-Ring Remy extensions with balayage blend", "90 mins", "Cold Micro Ring")
    )
}
