# এসএইচএস খাতা (SHS Khata)

A small-business customer due/transaction ledger app for Bangladeshi shop owners.
A native Android app built with Kotlin, Jetpack Compose, and Room.

---

## What is SHS Khata?

এসএইচএস খাতা is a simple **customer khata** (লেনদেন খাতা) for small shops.
Track who owes you money (দিলাম) and who you owe them (পেলাম), keep a running
balance per customer, and see the total due at a glance.

## Features

- **Customer list** with live search (name or phone), running balance per customer,
  and a **Total Due (মোট বাকি)** summary header.
- **Add / edit / delete customers** via FAB + long-press menu.
- **Customer ledger**: all transactions newest-first, each showing date, note,
  and amount — colored green (পেলাম / You Got) or red (দিলাম / You Gave).
- **Add transaction**: bottom sheet with GAVE/GOT type toggle, amount, optional note.
- **Delete transaction** by tapping the row.
- **Balance rule**: `balance = sum(দিলাম) − sum(পেলাম)`
  - Positive → customer owes the shop (বাকি)
  - Zero or negative → clear or advance (অগ্রিম)

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0.20 |
| UI | Jetpack Compose (Material 3) |
| Architecture | MVVM + StateFlow |
| Database | Room 2.6.1 (KSP) |
| Navigation | Compose Navigation 2.7.7 |
| Lifecycle | lifecycle-viewmodel-compose 2.8.6 |
| minSdk | 26 · targetSdk / compileSdk | 34 |
| AGP | 8.5.2 |

## Project Structure

```
app/
  src/main/
    AndroidManifest.xml
    java/com/shslab/shskhata/
      SHSKhataApp.kt              # Application class, DI wiring
      MainActivity.kt             # Entry point, sets up Compose content
      data/
        db/
          Customer.kt             # Room entity: customers
          Txn.kt                  # Room entity: transactions + TxnType enum
          CustomerDao.kt          # Flow queries + CRUD
          TxnDao.kt
          AppDatabase.kt          # Room database, version 1
        repository/
          CustomerRepository.kt   # Wraps CustomerDao + TxnDao
          TxnRepository.kt
      ui/
        SHSKhataRoot.kt           # NavHost + Routes
        theme/
          Color.kt  Theme.kt  Type.kt
        util/
          FormatUtils.kt          # formatTaka(), formatDate()
        customers/
          CustomerListScreen.kt   # List + search + FAB + dialogs
          CustomerListViewModel.kt
        ledger/
          LedgerScreen.kt         # Ledger + add-transaction sheet
          LedgerViewModel.kt
    res/
      drawable/
        ic_launcher_foreground.xml  # Vector (no binary assets)
        ic_launcher_background.xml
      mipmap-anydpi-v26/
        ic_launcher.xml  ic_launcher_round.xml
      values/
        colors.xml  strings.xml  themes.xml
```

## Building

### Prerequisites
- Android Studio (Hedgehog or later)
- JDK 17
- Android SDK with platform 34

### Steps
1. Open this repository folder in **Android Studio**.
2. Wait for **Gradle sync** to complete — Android Studio will automatically
   download the Gradle wrapper distribution specified in
   `gradle/wrapper/gradle-wrapper.properties` (Gradle 8.11.1).
   The `gradle-wrapper.jar` binary is intentionally not committed;
   Android Studio generates it on first sync.
3. Build: **Build → Make Project**, or from the terminal:
   ```bash
   ./gradlew assembleDebug
   ```
4. Run on a device or emulator (API 26+).

> Note: The Gradle wrapper JAR is not committed. On first open,
> Android Studio will generate `gradle/wrapper/gradle-wrapper.jar`
> automatically. No manual setup is required.

## Privacy & Data

All data is stored locally in Room on the device. No network access,
no accounts, no analytics. Your khata data never leaves the phone.

---

## License

MIT — see [LICENSE](LICENSE).
