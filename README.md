# 📍 Jetpack Compose Location App

This Android app fetches and displays the user's current location (latitude and longitude) using **Google Play Services Location API**, built entirely using **Jetpack Compose**. It handles runtime permissions for both **ACCESS_FINE_LOCATION** and **ACCESS_COARSE_LOCATION**.

---

## ✨ Features

- Built with Jetpack Compose (no XML).
- Uses `FusedLocationProviderClient` from Google Play Services.
- Requests fine and coarse location at runtime.
- Displays location in real-time.
- Lightweight and beginner-friendly architecture.

---

## 📸 Screenshot

<img src="ss/lc1.png" width="300" alt="Location App Screenshot" />
<img src="ss/lc2.png" width="300" alt="Location App Screenshot" />

---

## 🔐 Permissions Required

Add the following to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
