# SportHub 🏋️‍♂️

تطبيق Android شامل لتتبع التمارين الرياضية والتسوق الرياضي

## نظرة عامة

SportHub هو تطبيق Android احترافي مبني بلغة Java يجمع بين:
- ⚡ تتبع التمارين الرياضية اليومية
- 🛒 متجر إلكتروني للمعدات والمكملات الرياضية
- 📊 تتبع التقدم بالرسوم البيانية
- 👥 مجتمع اجتماعي
- ⭐ نظام اشتراكات Premium

## المميزات التقنية

### البنية التقنية
- **اللغة**: Java
- **المنصة**: Android (API Level 30+)
- **التصميم**: Material Design 3 (Dark Theme)
- **البنية المعمارية**: MVVM Pattern
- **Build System**: Gradle 8.2

### قاعدة البيانات
- **Firebase**: Firestore, Storage, Auth, Messaging
- **Room Database**: للتخزين المحلي والعمل Offline

### المكتبات الرئيسية
- AndroidX (AppCompat, Core, ConstraintLayout)
- Material Design 3
- Firebase BOM 32.7.0
- Room Database 2.6.1
- MPAndroidChart (للرسوم البيانية)
- Glide & Picasso (لتحميل الصور)
- Stripe & Google Play Billing (للمدفوعات)
- WorkManager (للمهام الخلفية)

## هيكل المشروع

```
app/
├── src/main/
│   ├── java/com/sporthub/
│   │   ├── MainActivity.java
│   │   ├── SportHubApplication.java
│   │   ├── adapter/          # RecyclerView Adapters
│   │   ├── data/
│   │   │   ├── local/        # Room Database
│   │   │   ├── model/        # Data Models
│   │   │   └── repository/   # Repositories
│   │   ├── ui/               # Activities & Fragments
│   │   ├── services/         # FCM & Background Services
│   │   └── utils/            # Utility Classes
│   ├── res/
│   │   ├── layout/           # XML Layouts
│   │   ├── values/           # Colors, Strings, Themes
│   │   └── menu/             # Navigation Menu
│   └── AndroidManifest.xml
└── build.gradle
```

## الإعداد والتثبيت

### المتطلبات
- Android Studio Arctic Fox أو أحدث
- JDK 8 أو أعلى
- Android SDK (API 30+)
- حساب Firebase

### خطوات التثبيت

1. **استنساخ المشروع**
```bash
git clone https://github.com/Majd868/SportHub.git
cd SportHub
```

2. **إعداد Firebase**
   - أنشئ مشروع جديد في [Firebase Console](https://console.firebase.google.com/)
   - أضف تطبيق Android بـ Package name: `com.sporthub`
   - قم بتحميل ملف `google-services.json`
   - استبدل الملف في `app/google-services.json`
   - فعّل الخدمات التالية:
     - Authentication (Email/Password & Google)
     - Cloud Firestore
     - Cloud Storage
     - Cloud Messaging

3. **فتح المشروع في Android Studio**
   - افتح Android Studio
   - File → Open
   - اختر مجلد المشروع

4. **Sync Gradle**
   - انتظر حتى تكتمل مزامنة Gradle
   - إذا واجهت أخطاء، تأكد من تثبيت SDK المطلوب

5. **تشغيل التطبيق**
   - وصّل جهاز Android أو استخدم Emulator
   - Run → Run 'app'

## قواعد Firestore Security Rules

أضف القواعد التالية في Firebase Console → Firestore → Rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    match /workouts/{workoutId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
    
    match /products/{productId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    match /orders/{orderId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

## التصميم

### نظام الألوان (Material Design 3 Dark Theme)
- **Primary**: #00D9FF (أزرق فيروزي)
- **Background**: #0F0F0F (أسود داكن)
- **Surface**: #1A1A1A (رمادي داكن)
- **Card Background**: #222222

### الخصائص
- دعم كامل للغة العربية (RTL)
- تصميم متجاوب لجميع أحجام الشاشات
- ViewBinding في كل الواجهات

## الميزات الرئيسية

### 1. تتبع التمارين
- إضافة وتعديل التمارين
- تسجيل المجموعات والتكرارات
- حساب السعرات الحرارية
- مزامنة مع Firebase
- العمل Offline مع Room Database

### 2. المتجر الإلكتروني
- تصفح المنتجات حسب الفئات (بروتين، معدات، ملابس، فيتامينات، إكسسوارات)
- سلة مشتريات
- نظام طلبات كامل
- دفع آمن

### 3. تتبع التقدم
- رسوم بيانية للتقدم (MPAndroidChart)
- إحصائيات الأسبوع
- عدد التمارين والسعرات

### 4. الملف الشخصي
- معلومات المستخدم
- الطلبات السابقة
- الاشتراك Premium
- لوحة البائع

## الأمن والخصوصية

- ✅ تشفير البيانات عبر Firebase
- ✅ مصادقة آمنة (Email + Google Sign-In)
- ✅ قواعد Firestore Security
- ✅ ProGuard للحماية

## المساهمة

نرحب بالمساهمات! يرجى:
1. Fork المشروع
2. إنشاء Branch للميزة الجديدة
3. Commit التغييرات
4. Push إلى Branch
5. فتح Pull Request

## التواصل

- GitHub: [@Majd868](https://github.com/Majd868)
- Project Link: [https://github.com/Majd868/SportHub](https://github.com/Majd868/SportHub)

---

**تم البناء بـ ❤️ للمجتمع الرياضي العربي**
