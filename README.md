# HorizontalCalendar

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)  
[![JitPack](https://img.shields.io/badge/JitPack-Ready-blue.svg)](https://jitpack.io)

A **lightweight**, **highly customizable** Android library providing an **infinite-scrolling**, **horizontally-oriented** calendar widget.

<p align="center">
  <img src="https://user-images.githubusercontent.com/…/widget_demo.gif" alt="HorizontalCalendar demo" width="320"/>
</p>

---

## Table of Contents

- [Features](#features)  
- [Installation](#installation)  
- [Quick Start](#quick-start)  
- [XML Attributes](#xml-attributes)  
- [API Reference](#api-reference)  
- [Customization](#customization)  
- [Sample App / Testing](#sample-app--testing)
- [Changelog](#changelog) 
- [Acknowledgments](#acknowledgments)
- [Author](#author)
- [Contact & Support](#contact--support)
- [License](#license)  

---

## Features

- **Infinite horizontal scrolling** — display as many days into the past and future as needed  
- **Center-snap behavior** with smooth or instant scroll  
- **Optional week dividers** to visually separate weeks  
- **Configurable visible range** (days before/after center date)  
- **Date click listener** for custom actions  
- **Lightweight** (no large dependencies) and **easy to integrate**  

---

## Installation

### Via JitPack

1. In your **root** `build.gradle`, add JitPack:
   ```groovy
   allprojects {
     repositories {
       maven { url 'https://jitpack.io' }
     }
   }
   ```
2. In your **app** module’s `build.gradle`, add:
   ```groovy
   dependencies {
     implementation 'com.github.annasozonova:HorizontalCalendar:1.0.2'
   }
   ```

### As a Local Module

1. Copy the `library/` folder into your project root.  
2. In `settings.gradle`:
   ```groovy
   include ':library'
   ```
3. In your app’s `build.gradle`:
   ```groovy
   dependencies {
     implementation project(':library')
   }
   ```

---

## Quick Start

1. **Layout** (`res/layout/activity_main.xml`):
   ```xml
   <com.sahana.horizontalcalendar.HorizontalCalendar
       android:id="@+id/horizontalCalendar"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       app:visibleRange="90"
       app:showWeekDividers="true"
       app:weekStartsOnMonday="true" />
   ```
2. **Activity** (`MainActivity.java`):
   ```java
   import androidx.appcompat.app.AppCompatDelegate;
   import android.os.Bundle;
   import androidx.appcompat.app.AppCompatActivity;
   import com.sahana.horizontalcalendar.HorizontalCalendar;
   import java.util.Calendar;
    
   public class MainActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    
            HorizontalCalendar horizontalCalendar = findViewById(R.id.horizontalCalendar);
            Calendar targetDate = Calendar.getInstance();
            horizontalCalendar.setInitialDate(targetDate);
        }
    }
   ```

---

## XML Attributes

| Attribute                | Format  | Default | Description                                |
|--------------------------|---------|---------|--------------------------------------------|
| `app:visibleRange`       | integer | 180     | Number of days before/after the center date |
| `app:showWeekDividers`   | boolean | true    | Draw dividers at week boundaries           |
| `app:weekStartsOnMonday` | boolean | true    | Week starts on Monday (`false` = Sunday)   |

---

## API Reference

### `void setInitialDate(Calendar date)`
Set the calendar’s center date.
```java
calendar.setInitialDate(myCalendar);
```

### `void scrollToDateSmooth(Calendar date)`
Animate smoothly to the specified date.
```java
calendar.scrollToDateSmooth(targetCalendar);
```

### `void scrollToDateInstant(Calendar date)`
Jump instantly to the specified date without animation.
```java
calendar.scrollToDateInstant(targetCalendar);
```

### `void resetToInitialDate()`
Return (smoothly) to the date passed in `setInitialDate()`.
```java
calendar.resetToInitialDate();
```

### `void setShowWeekDividers(boolean enabled)`
Enable or disable week boundary dividers.
```java
calendar.setShowWeekDividers(false);
```

### `void setWeekStartsOnMonday(boolean startsOnMonday)`
Change the start-of-week day at runtime.
```java
calendar.setWeekStartsOnMonday(false);  # weeks start on Sunday
```

---

## Customization

- **Colors & Styles**: Override library drawables or define your own shapes in your app theme.  
- **Selection Frame**: Modify or subclass `SelectionFrameView` for custom highlighting.  

---

## Sample App / Testing

This project no longer includes a built-in sample module, but you can still test the calendar widget:

### 1. Import as a Local Module

1. Clone this repository:
   ```bash
   git clone https://github.com/annasozonova/HorizontalCalendar.git
   cd HorizontalCalendar
   ```
2. Open **Android Studio** and choose **File → New → Import Module...**, then select the `library/` folder and import.
3. In your project’s `settings.gradle`:
   ```groovy
   include ':library'
   ```
4. In your app module’s `build.gradle`, add:
   ```groovy
   dependencies {
     implementation project(':library')
   }
   ```
5. Apply the **Quick Start** code in your layout and Activity, then run on a device or emulator.

### 2. Via JitPack

1. Follow the **Installation → Via JitPack** instructions above to add the library.
2. Apply the **Quick Start** snippet in your layout and Activity, then run.

### 3. Testing Local Changes

1. Publish to your local Maven:
   ```bash
   ./gradlew publishToMavenLocal
   ```
2. In your consuming app’s `build.gradle`:
   ```groovy
   repositories {
     mavenLocal()
     mavenCentral()
   }
   dependencies {
     implementation 'com.github.annasozonova:HorizontalCalendar:1.0.2'
   }
   ```
3. Sync and apply the **Quick Start** snippet to verify your changes.

---

## Changelog

### [1.0.0] - 2025-05-01
- Initial release of HorizontalCalendar.
- Features infinite scrolling, center snap, week dividers, and configurable range.

---

## Acknowledgments

- Based on the original [HorizontalCalendar by B-Sahana](https://github.com/B-Sahana/Horizontal-Calendar-Sample) (MIT License).

---

## Author

Developed and maintained by **Anna Sozonova**.

---

## Contact & Support

For questions, issues, or feature requests, please open an issue on the [GitHub repository](https://github.com/annasozonova/HorizontalCalendar).

---

## License

This library is released under the **MIT License**. See [LICENSE](LICENSE) for details.
