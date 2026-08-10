# Project Lomdi: Pre-Transaction UPI Fraud Prevention

## 1. Mission Statement
To compute a real-time, on-device Fraud Risk Score before a user executes a UPI transaction by monitoring Vishing indicators, remote access tools, and suspicious clipboard/SMS data.

## 2. Tech Stack & Architecture
* **Language:** Kotlin
* **UI:** Jetpack Compose (Material Design 3)
* **Background Monitoring:** WorkManager, TelephonyManager, ClipboardManager
* **System Interception:** `AccessibilityService` (for active app tracking) & `NotificationListenerService` (for VoIP tracking)

## 3. Core Feature Roadmap
1. **SMS Interceptor & Link Parser:** Captures incoming messages, extracts links/UPI handles, and evaluates threat severity.
2. **Call & Vishing Detector:** Monitors if the user is on an active call with an unsaved contact.
3. **Clipboard Interceptor:** Tracks copied UPI IDs during active call sessions.
4. **Remote App Monitor:** Detects active screen-sharing or remote desk applications (AnyDesk, TeamViewer, etc.).
5. **Real-Time Threat Interception:** Triggers a high-priority, persistent notification (call-style) before the user accesses target UPI apps (GPay, Paytm, PhonePe) if cumulative risk exceeds safety thresholds.

## 4. Current Project State & UI
* **Security Hub Dashboard:** Reorganized into four clean, technical categories: App & System Security, Communication Security, Session Monitors, and Device Integrity.
* **Hero Header Shield:** High-contrast system status display with integrated "Simulate Threat" toggle for live demonstrations.
* **Granular Detail Screens:** Every security module (Accessibility, Tapjacking, Keyboard, Call, SMS, Clipboard, Remote Apps, Integrity) now features a dedicated drill-down screen with detailed audit logs and risk explanations.
* **Diagnostic Tools:** Integrated "Test Overlay Badge" toggle to verify floating overlay rendering across various applications.
* **Terminology Pivot:** Transitioned to professional security standards (e.g., Risk Index, Severity Coefficient, Suspicious Transaction Halted).
* **Risk Breakdown System:** Implemented an **Itemized Risk Factor Decomposition** in `RiskManager`. Every blocked transaction now displays a detailed percentage-based breakdown of contributing threats with direct dynamic evidence strings.
* **Hybrid SMS Analysis Engine:** Implemented `SmsAnalyzer`, a hybrid Decision Tree engine. Features a **Categorized Link Classifier** and strict TLD-based filtering.
* **Floating Protection Badge:** Implemented a persistent, non-intrusive "Anti-Lomdi Active" status overlay via `ActiveStatusOverlayController`.
* **Threat Alert System:** High-priority **Sticky Call-Style Notifications** using native `CATEGORY_CALL` and `setColorized(true)` APIs.
* **VoIP Call Detection:** Integrated `NotificationListenerService` and `AudioManager` for WhatsApp/Telegram security.
* **First Launch Logic:** Preferences DataStore managed onboarding. Strictly enforces **Required Permissions** via a reactive setup gate.

## 5. Critical Architectural Pivots & Constraints (DO NOT REVERT)
* **PIVOT 1: Abandoned WindowManager Overlays:** Due to `FLAG_SECURE` restrictions on apps like Paytm causing render flashes and touch passthrough bugs, the overlay system was completely removed and replaced with native Android Notifications (`ThreatNotificationManager`).
* **PIVOT 2: Accessibility Service Hardening:** `LomdiAccessibilityService` utilizes a **Hybrid Package Resolution** strategy. It evaluates both `rootInActiveWindow` (base truth) AND `event.packageName` (override) to successfully bypass `FLAG_SECURE` blindness on apps like Paytm and BHIM UPI.
* **Constraint: Android 12+ Notification UI:** Custom `RemoteViews` for notifications are heavily restricted by the OS, which forces them into standard system templates (gray backgrounds, hidden buttons). UI logic must account for these system-level limitations.