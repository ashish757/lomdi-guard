# Fox Guard 🛡️

## 📖 Overview
**Fox Guard** is a real-time, on-device fraud prevention system designed to protect users from UPI scams, vishing (voice phishing), and malicious applications. By continuously monitoring device state, communication channels, and app behaviors, it calculates a live Fraud Risk Score and forcefully intercepts the user before they make a dangerous financial transaction.

## ✨ Features & Threat Prevention

Fox Guard uses a hybrid heuristic and context-aware rule engine to track and neutralize threats locally on your device:

*   **Live Risk Engine & Severity Coefficient:** Correlates real-time data across calls, SMS, and clipboard contents. If the user opens a UPI app (like GPay or PhonePe) while under high threat, a persistent, unswipeable warning overlay blocks the transaction.
*   **Call & Vishing Interceptor:** Monitors standard PSTN and VoIP calls (WhatsApp, Telegram). If the user is on a call with an unsaved contact while attempting a transaction, it is flagged as a high-risk vishing attempt.
*   **SMS Phishing & Link Classifier:** Analyzes incoming text messages for urgency keywords, shortened URLs, and fake "Account Credited" claims using an on-device text normalizer and fuzzy matching algorithm to bypass scammer typos. 
*   **Clipboard Guard:** Tracks copied sensitive data (like UPI handles or malicious APK links). It uses strict domain filters to eliminate false positives (like standard email addresses) while flagging actual payment IDs copied during suspicious active calls.
*   **Malware & App-Level Exploit Auditor:** Actively scans the device for third-party apps holding dangerous permissions. It flags active non-system `AccessibilityService` apps (Banking Trojans), apps with overlay permissions (Tapjacking), and untrusted custom keyboards (Keyloggers).
*   **Remote Access & Screen Cast Monitor:** Detects the presence of installed remote administration tools (like AnyDesk) and monitors Android's `DisplayManager` to flag active, hidden screen-sharing sessions.

---

## ⚙️ How to Install & Sideload the APK

Because Fox Guard requires advanced system-level permissions (like Accessibility and Notification access) to protect you, it cannot be downloaded from the Google Play Store for general use. You must sideload the provided APK.

### Step 1: Pause Google Play Protect
Google Play Protect will aggressively flag sideloaded apps requesting accessibility permissions. You need to pause it temporarily to install the app:
1. Open the **Google Play Store** app.
2. Tap your profile icon in the top right corner and select **Play Protect**.
3. Tap the **Settings (Gear) icon** in the top right corner.
4. Toggle off **Scan apps with Play Protect**. 
5. Confirm by tapping **Turn off**.

### Step 2: Install the APK
1. Download the `app-release.apk` file to your Android device.
2. Tap the downloaded file to install it. 
3. If prompted, tap **Settings** and toggle on **Allow from this source** to let your file manager or browser install unknown apps. 
4. Tap **Install**.

### Step 3: Allow Restricted Settings (Android 13+ Users Only)
Modern Android versions aggressively block accessibility permissions for sideloaded apps. To grant Fox Guard the necessary permissions, you must bypass this restriction:
1. Open your device **Settings** and go to **Accessibility**.
2. Find **Fox Guard** and attempt to turn it on. You will get a prompt saying *"Restricted setting — For your security, this setting is currently unavailable."* **Dismiss this dialog** (this step is required to trigger the override).
3. Go back to your main device **Settings** > **Apps** (or See all apps) and select **Fox Guard**.
4. Tap the **three-dot menu (⋮)** in the top right corner (or scroll down on Samsung devices) and tap **Allow restricted settings**. Confirm with your PIN or fingerprint.
5. Go back to **Settings** > **Accessibility**, and you can now successfully toggle Fox Guard on.

---

## 🚀 Basic Usage

1. **Initial Setup:** Launch Fox Guard. You will be greeted by the onboarding screen. Grant all the requested core permissions (Phone State, Notifications, SMS) and enable the Accessibility Service.
2. **The Security Hub:** Once permissions are granted, you will land on the Dashboard. Here you can see a live readout of your system's health, categorized into *App Security*, *Communication Security*, *Session Monitors*, and *Device Integrity*.
3. **Simulate a Threat:** Want to see the defenses in action? Toggle the **Simulate Threat** switch on the dashboard. Then, try opening a UPI app like Paytm or GPay. You will be immediately intercepted by the high-priority alert system.
4. **Audit Explanations:** Tap on any category card (e.g., "App & System Security") to open a detailed breakdown screen. This will explain exactly *why* a particular app or clipboard item was flagged, showing the exact risk coefficient applied to your device.
