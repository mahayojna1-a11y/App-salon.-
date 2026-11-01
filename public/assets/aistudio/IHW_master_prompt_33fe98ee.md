You are a senior full-stack React engineer and award-winning UI/UX designer. Build a complete, production-ready, mobile-first website + appointment booking application for "Indian Hair World" (IHW), Sodepur branch, a premium non-surgical hair replacement studio (hair patches, wigs, toppers, toupees, lace systems, hair extensions and bundles, for men and women). I have attached reference screenshots of the homepage and a reference image of the 6-step booking flow. Follow their layout, section order, spacing, card style and feel closely, but make everything smoother, more animated and more polished. Where text in a reference image is garbled, ignore it and use the real content in this prompt. Do not reuse photos from the references as final assets; use placeholder images in public/images that the owner can replace.

=== 1. TECH STACK AND STRUCTURE ===
React + TypeScript + Vite, Tailwind CSS, Framer Motion for animation, Lenis for smooth scrolling, Lucide icons, a simple router (React Router or hash router). Data is stored in localStorage behind a clean store layer (src/store) so a backend can replace it later. Folders: src/components/customer, src/components/admin, src/components/booking, src/components/ui, src/store, src/types, src/config, src/assets. App.tsx only holds the mode state (CustomerApp and AdminDashboard with switch handlers).
One config file (src/config/site.ts) holds: phone +91 89100 97668, WhatsApp number, address (RN Avenue, Pansila, Khardah, Sodepur), working hours (open 7 days, 11:00 AM to 7:00 PM), starting price Rs. 5,999, festival banner text, branches, services, prices, products and "what to expect" list, so the owner edits everything in one place.

=== 2. BRAND AND DESIGN SYSTEM ===
- Colours (CSS variables): deep maroon #7A1020 (main), dark maroon #5C0B18, booking maroon #6B0F1A, gold #E8C772 (accent), dark gold #C99A2E, cream #FBF4E8 and #F5EFE6 (backgrounds), white cards, dark navy text #0F172A, WhatsApp green #16B981 (only for WhatsApp), call button blue.
- Typography: Playfair Display italic for hero headlines, Inter or Poppins for body. Big bold section headings, generous spacing, rounded 20 to 24px cards, soft shadows, thin gold dividers with small ornament icons.
- Design at 360px width first, then tablet and desktop. Minimum tap target 44px. No horizontal page scroll. Respect phone safe areas. Input font size at least 16px.
- Language toggle English and Bangla (simple i18n file, full UI translation). Default English.

=== 3. GLOBAL UI ===
- Sticky header: IHW text logo, search icon, gold round call button, hamburger opening an animated full-screen menu.
- Men / Women segmented toggle under the header (animated sliding pill). Switching it cross-fades hero copy, product rows, services and pre-selects customer type in booking.
- Floating blue round Call button (bottom right, gentle pulse) on every page. Mobile sticky bottom action bar: Book Now (maroon), WhatsApp (green), Call (gold); hides on scroll down, shows on scroll up.
- Editable festival banner strip (Diwali / Pujo style, gold lamp animation); admin can turn it on or off.
- Page transitions with fade and slight slide. Gold scroll progress bar at the top.
- Splash screen with IHW logo under 1.2 seconds, skippable.

=== 4. HOME PAGE SECTIONS (in this order) ===
1. Hero (maroon gradient with subtle gold lantern/ornament pattern): small gold label "Indian Hair World, Sodepur"; headline "Hair Patch, Wigs & Non-Surgical Hair Replacement in Sodepur" in gold serif italic; line "Look Natural. Feel Confident. Walk Out in 60 Minutes."; editable "Trusted by" pill; cream price card "Starting at Rs. 5,999/-" with "We Accept EMI Available" and a partner logo placeholder; two big buttons "Book Free Consultation" (maroon, opens booking) and "WhatsApp Us" (green); row of 4 trust icons: 100K+ Happy Clients, Non-Surgical & Safe, Premium Quality, Expert Support.
2. "Featured On Shark Tank India" banner strip (editable image and text, can be hidden).
3. Intro SEO paragraph plus 2-column tick list: 100% Human Hair, Natural Undetectable Look, Same-Day Hair Fixing, Custom Hair Systems, Affordable Hair Patch Price, Serving Sodepur & North 24 Parganas.
4. Trending Videos: horizontal snap carousel of vertical 9:16 video cards (poster, play icon, tap opens modal player), each with small product thumbnail, name, price, strike price and green discount badge. Arrows on desktop, swipe on mobile.
5. Transformations: before/after cards with a draggable comparison slider (Before and After glass pills), swipe carousel, maroon "View all transformations" button opening a gallery page with Men / Women filter.
6. Men's Hair Products: horizontal scroll cards with a maroon "TRY-ON" tag, image, brand, name, price, strike price, % off badge, heart wishlist icon. Sample data: Unique Gold Hair Patch Brown/Black Rs. 6,999 (was 10,999, 36% off); Full Lace Hair Patch Brown/Black Rs. 24,999 (was 34,999, 29% off); 0.3 mm Hair Patch Black Rs. 26,999 (was 39,999, 33% off); Model Hair Patch Black Rs. 6,999 (was 9,999, 30% off).
7. Women's Hair Products: Micro Ring Extension, 50 Strands, Rs. 15,000 in Black, Copper, Highlights, Burgundy.
8. Product detail bottom sheet: image gallery, description, colour options, "Book Try-On" and "WhatsApp enquiry".
9. Content cards with coloured top accent line: Indian Human Hair in Sodepur (with "Why choose Indian human hair" list: natural shine, long-lasting quality, soft texture, styling flexibility, realistic appearance), Lace Hair Systems, Human Hair Bundles; Human Hair Wigs for Men & Women (Full, Lace, Medical, Curly, Fashion, Customized Wigs); "Wig for Women" (pink tint) and "Wig for Men" (blue tint) panels with feature lists; Hair Toppers & Hair Patches for Women; Best Hair Patch for Men with "Benefits of Hair Patches" card (natural appearance, lightweight and breathable, easy maintenance, swim, gym and travel confidently); Synthetic & Affordable Wigs grid (Synthetic, Affordable, Fashion, Short, Long, Coloured); Curly Collection chips (Curly Wigs, Curly Hair Pieces, Curly Toppers, Curly Lace Wigs, Custom Curly Systems, Multiple Curl Patterns).
10. "Services at Our Sodepur Branch" list: Hair Patch for Men (from Rs. 5,999, result in 60 minutes), Hair Wig for Men, Hair Patch for Women, Hair Extensions & Toppers, Hair Patch Servicing (every 25 to 45 days). Each has a "Book" button that pre-selects that service in booking.
11. Studio/team photo with rounded corners, then "Why Choose Indian Hair World Sodepur?" check-list: featured brand, 100% human hair, natural undetectable results, 10,000+ customers served, same-day fixing in 60 minutes, EMI from Rs. 5,999, open 7 days 11am to 7pm (walk-ins welcome), experienced specialists, premium quality, personalized consultation, convenient location.
12. Reviews carousel (editable testimonials with stars) and FAQ accordion with smooth height animation.
13. Final maroon CTA "Book Your Free Consultation Today" with gold "Call +91 89100 97668" and white "WhatsApp Us"; "Areas We Serve Near Sodepur" chips; Google Maps link; footer with hours, address, social links.

=== 5. TEXT ANIMATIONS ===
- Hero headline: split-by-word reveal with upward slide, blur-to-sharp and 0.06s stagger, plus one gold shimmer sweep on load.
- Section headings: line mask reveal on scroll; sub text fades up with 0.15s delay.
- Animated counters (100K+, 10,000+, Rs. 5,999) counting up once when visible.
- Typewriter rotating word in hero (Patches, Wigs, Toppers, Extensions).
- Button arrows nudge on hover/tap; underline draw animation on links.

=== 6. IMAGE AND ELEMENT ANIMATIONS ===
- Hero background: slow Ken Burns zoom plus light gold sparkle particles (disabled on low-power devices).
- Subtle parallax (max 40px). Images fade-scale in on scroll with blur-up placeholders, lazy loading, width and height set to avoid layout shift.
- Product cards lift with shadow and slight image zoom; TRY-ON tag gentle pulse; heart pop animation.
- Before/after slider handle bounces once to hint it is draggable. Carousels use scroll-snap with spring physics.
- Staggered grid reveals; floating lamp/ornament in the festival banner; skeleton loaders.

=== 7. SMOOTH SCROLLING AND MOBILE FRIENDLINESS ===
- Lenis smooth scrolling site-wide; disable automatically for prefers-reduced-motion; keep native scroll inside modals, bottom sheets and horizontal carousels. Anchor links scroll smoothly with header offset.
- Scroll-snap carousels, swipe gestures, bottom sheets, thumb-reachable buttons, no hover-only interactions, numeric keypad for phone input, press animation (scale 0.97).
- Performance: lazy-load images and videos, code-split routes, animate only transform and opacity, high Lighthouse mobile score. All animations respect prefers-reduced-motion.

=== 8. BOOKING SYSTEM (follow the attached 6-step booking reference image) ===
UI style: full-screen sheet on mobile (centered modal on desktop). Top bar: back arrow, centered "Book Appointment", close X. Cream background (#F5EFE6), white rounded cards (20px), maroon (#6B0F1A) primary buttons and selected states, thin maroon outline for unselected buttons, soft shadows.
Progress: 6 connected dots with a line, filled maroon to the current step, text "Step X of 6: <name>", animated fill between steps.
Sticky bottom bar on every step: left shows live summary (branch, date and time, total, e.g. "Sodepur | Oct 26, 1:00 PM | Rs. 3,499"), right shows maroon "Continue". Continue is disabled (faded rose) until the step is valid. Total animates when it changes. Steps slide with spring animation. Data is kept when going back. Phone back button goes one step back inside the flow.

STEP 1 Choose Service: heading "Select your service", sub "What can we help you with today?". Scrollable filter chips: All, Patch, Wig, Toupee, Topper, Extensions, Servicing, Consultation. Search box. Men/Women toggle switch (ladies work is fully separate: own service list, own slot capacity, own admin queue). Banner "First time? Book a FREE Consultation for expert advice." Quick toggle New Work / Servicing Work. Service cards: photo, name, duration with clock icon, bold price, strike-through old price with yellow discount badge, New Work or Servicing tag, outline "Select" button (selected = maroon border and check). Services: Hair Patch Fixing (60 min), Hair Patch Servicing, Free Consultation (30 min, Rs. 0 FREE), Premium Wig (price on consultation), Topper, Extensions, Women's Patch, Medical Wig.

STEP 2 Customise: "Personalise your service". Method as 2x2 selectable tiles with icons: Bonding, Clipping, Taping, Weaving (selected = maroon border and check). "Gold / Lace patches" colour swatches and image tiles (Brown, Black, more); extra cost adds to total. Optional note textarea (200 characters counter), optional photo upload with preview. For Servicing, show a last-service-date picker instead of method tiles.

STEP 3 Choose Branch: "Select a studio", "Find a branch near you." Branch cards with photo, name, check, location, "Open 11 AM - 7 PM" with green dot, distance (only if geolocation allowed), and small outline buttons Call and Maps. Branch data in config: Sodepur (default, active; RN Avenue, Pansila, Khardah) plus optional Durgapur, Lake Gardens, Salt Lake, Siliguri that admin can enable or disable, each with own hours, slots and phone. If only one branch is enabled, auto-select it and skip visually while keeping step numbering.

STEP 4 Choose Date: "Choose a date", "When would you like to visit?". Chips Today, Tomorrow, This Weekend. Month calendar with arrows, today dot, selected date as filled maroon circle, past and blocked dates disabled. Up to 30 days ahead (configurable). Small dots when few slots remain.

STEP 5 Pick a Time Slot: shows chosen date (e.g. "Saturday, October 26") and "Available slots for 60 min service." Three columns Morning, Afternoon, Evening with time pills; selected = filled maroon; booked = grey with strike-through and "Booked" label, not tappable. Slots every 30 minutes from 11:00 AM to 7:00 PM, and only shown if the full service duration ends by closing time. Capacity per branch, separate for men and women. If no slots left, friendly message and "Try next day" button.

STEP 6 Details and Confirm: fields with icons: Full Name (required), Phone with +91 prefix and 10-digit validation (required), Email (optional), Notes (optional). Inline red helper text ("Enter your full name", "Enter a valid 10 digit mobile number"). "Review your booking" card: service + options, branch and address, date, time and duration, price "(Pay at studio)", small "Edit" links jumping to the right step. Checkbox "I agree to be contacted on WhatsApp / call about this booking". Full-width maroon "Confirm Booking" with loading state. Re-check slot availability on confirm; if taken, show a friendly message and return to Step 5. Prevent double booking.

CONFIRMATION SCREEN: large animated maroon circle with drawing checkmark and soft confetti, "Booking Confirmed", Booking ID like IHW-241026-0042 with copy icon (toast on copy). Summary card with Edit links. Full-width outline buttons: Add to Calendar (.ics), Share on WhatsApp (pre-filled message with all details), Get Directions (Google Maps link of chosen branch), View My Bookings, Back to Home. "What to expect" list: Arrive 10 minutes early, Bring a photo of your preferred hairstyle, Result in 60 minutes (editable in config).

QUICK CONSULTATION FORM: name, phone, preferred time; creates a lead for the admin.

MY BOOKINGS: lookup by phone number (remembered on the device after first booking), list with status chips (Pending, Confirmed, Completed, Cancelled, No-show), Reschedule (reopens Step 4 and frees the old slot) and Cancel (confirm dialog).

=== 9. ADMIN DASHBOARD ===
- Entry through a small footer link and PIN screen (default 1234, changeable). Mobile bottom tab bar: Today, Calendar, Bookings, Leads, Products, Settings.
- Today: timeline of appointments with status chips and quick actions (Confirm, Complete, No-show, Cancel, Reschedule), tap-to-call and WhatsApp customer, one-tap WhatsApp reminder templates.
- Calendar: month and day views, Men / Women filter, colour coded by New Work and Servicing, branch filter.
- Bookings: searchable list; filters by status, branch, men/women, new/servicing, date range. Each booking shows service, method, colour, notes and uploaded photo. Add manual walk-in booking. Export CSV.
- Leads: consultation requests with follow-up status.
- Products: add, edit, delete with image upload, price, strike price, auto discount, men/women category, visibility toggle; home page updates instantly.
- Settings: working hours (default 11:00 to 19:00), slot interval, capacity per slot, services, prices, discounts, methods, colour options, branches, blocked dates and holidays, festival banner on/off and text, contact numbers, default language, change PIN.
- Stats cards with animated counters: today, this week, new vs servicing, men vs women, no-shows; simple bar and donut charts.

=== 10. QUALITY, IMAGES, PWA, BUILD ===
- Semantic accessible HTML, aria labels, focus states, alt text, good contrast. Inline validation, loading, empty and error states, error boundary.
- SEO: title, description, Open Graph, LocalBusiness structured data for Sodepur.
- Every image stored locally in public/images and referenced as /images/filename.jpg. No external hotlinks or temporary URLs. Use tasteful placeholders or gradients where a photo is missing and list which images I must replace.
- Add public/manifest.json (name, short_name, 192 and 512 icons, theme colour maroon, standalone display) and a service worker so the app is installable.
- Do not use HTML form tags that break the AI Studio preview; use controlled inputs and button handlers.
- npm run build must succeed with zero errors, and the production build must look the same as the preview.

=== 11. DELIVERY ===
Build everything in one go, working end to end, with no placeholder logic. At the end: list every file created, list the image files I must replace, explain how to change prices, phone number, branches and working hours, and confirm that npm run build passes and all images load from public/images.
