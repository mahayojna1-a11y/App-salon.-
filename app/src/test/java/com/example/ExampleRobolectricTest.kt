package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.IhwRepository
import com.example.model.BookingDraft
import com.example.model.CustomerType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Indian Hair World", appName)
  }

  @Test
  fun `repository adds booking and updates state`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = IhwRepository(context)

    val draft = BookingDraft(
      customerName = "Test Client",
      customerPhone = "9876543210",
      customerType = CustomerType.MEN,
      selectedDateStr = "Oct 31, 2026",
      selectedTimeSlot = "1:00 PM"
    )

    val booking = repo.addBooking(draft)
    assertNotNull(booking)
    assertEquals("Test Client", booking.customerName)
    assertTrue(booking.bookingCode.startsWith("IHW-"))
  }

  @Test
  fun `gents and ladies chairs track independently for same slot`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = IhwRepository(context)

    val date = "Nov 1, 2026"
    val slot = "2:00 PM"

    // Initial check: both available
    assertTrue(repo.isSlotAvailable(date, slot, CustomerType.MEN))
    assertTrue(repo.isSlotAvailable(date, slot, CustomerType.WOMEN))

    // Book Gents chair
    repo.addBooking(
      BookingDraft(
        customerName = "Gents Client",
        customerPhone = "9876543210",
        customerType = CustomerType.MEN,
        selectedDateStr = date,
        selectedTimeSlot = slot
      )
    )

    // Gents chair (capacity 1) is now full
    assertFalse(repo.isSlotAvailable(date, slot, CustomerType.MEN))
    // Ladies chair is STILL available at the exact same 2:00 PM slot!
    assertTrue(repo.isSlotAvailable(date, slot, CustomerType.WOMEN))
  }

  @Test
  fun `available slots strictly span 11 AM to 7 PM only`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = IhwRepository(context)

    val slots = repo.getAvailableTimeSlots()
    assertEquals(8, slots.size)
    assertEquals("11:00 AM", slots.first())
    assertEquals("6:00 PM", slots.last()) // Last 1-hour slot ends at 7:00 PM
  }
}
