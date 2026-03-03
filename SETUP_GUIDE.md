# دليل الإعداد السريع - SportHub

## ⚡ البداية السريعة

### المتطلبات الأساسية
- ✅ Android Studio Arctic Fox أو أحدث
- ✅ JDK 8+
- ✅ Android SDK (API 30+)
- ✅ حساب Firebase مجاني

---

## 📋 خطوات الإعداد

### 1️⃣ استنساخ المشروع
```bash
git clone https://github.com/Majd868/SportHub.git
cd SportHub
```

### 2️⃣ إعداد Firebase (مهم جداً!)

#### أ. إنشاء مشروع Firebase
1. اذهب إلى [Firebase Console](https://console.firebase.google.com/)
2. انقر على "Add Project"
3. أدخل اسم المشروع: `SportHub`
4. اتبع الخطوات حتى إنشاء المشروع

#### ب. إضافة تطبيق Android
1. في لوحة Firebase، انقر على أيقونة Android
2. أدخل Package name: `com.sporthub`
3. أدخل App nickname: `SportHub`
4. قم بتنزيل ملف `google-services.json`
5. **هام**: استبدل الملف في `app/google-services.json`

#### ج. تفعيل الخدمات المطلوبة

**1. Authentication**
- اذهب إلى Build → Authentication
- انقر على "Get Started"
- فعّل "Email/Password"
- فعّل "Google" (اختياري)

**2. Cloud Firestore**
- اذهب إلى Build → Firestore Database
- انقر على "Create database"
- اختر "Start in test mode" للبداية
- اختر أقرب موقع لك

**3. Cloud Storage**
- اذهب إلى Build → Storage
- انقر على "Get started"
- اختر "Start in test mode"

**4. Cloud Messaging (اختياري)**
- اذهب إلى Build → Cloud Messaging
- فعّل الخدمة

### 3️⃣ إعداد Firestore Security Rules

في Firebase Console → Firestore → Rules، الصق القواعد التالية:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Workouts
    match /workouts/{workoutId} {
      allow read, write: if request.auth != null;
    }
    
    // Products
    match /products/{productId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Orders
    match /orders/{orderId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

انقر على "Publish"

### 4️⃣ فتح المشروع في Android Studio

1. افتح Android Studio
2. File → Open
3. اختر مجلد `SportHub`
4. انتظر Gradle Sync (قد يستغرق دقائق)

### 5️⃣ حل المشاكل الشائعة

#### مشكلة: Gradle Sync Failed
```bash
# الحل:
- File → Invalidate Caches / Restart
- Build → Clean Project
- Build → Rebuild Project
```

#### مشكلة: SDK not found
```bash
# الحل:
- File → Project Structure
- SDK Location → تحديد مسار Android SDK
```

#### مشكلة: google-services.json
```bash
# تأكد من:
1. الملف موجود في app/
2. الملف صحيح من Firebase Console
3. Package name = com.sporthub
```

### 6️⃣ تشغيل التطبيق

#### على جهاز حقيقي:
1. فعّل "Developer Options" على الجهاز
2. فعّل "USB Debugging"
3. وصّل الجهاز بالكمبيوتر
4. في Android Studio: Run → Run 'app'

#### على Emulator:
1. Tools → AVD Manager
2. Create Virtual Device
3. اختر جهاز (مثل Pixel 5)
4. اختر System Image (API 30+)
5. Finish
6. Run → Run 'app'

---

## 🎯 الاختبار الأول

بعد التشغيل:

1. **شاشة التسجيل**
   - أدخل بريد إلكتروني واسم وكلمة مرور
   - اضغط "تسجيل حساب"

2. **شاشة التمارين**
   - اضغط على زر ➕
   - أضف تمرين جديد
   - احفظ

3. **شاشة المتجر**
   - تصفح الفئات (سيكون فارغاً في البداية)

4. **شاشة التقدم**
   - عرض الرسم البياني (سيظهر بعد إضافة تمارين)

---

## 📝 إضافة بيانات تجريبية

### في Firebase Console → Firestore:

#### إضافة منتج تجريبي:
```javascript
Collection: products
Document ID: auto
Fields:
  - name: "بروتين واي"
  - description: "بروتين عالي الجودة"
  - category: "protein"
  - price: 200
  - stock: 50
  - available: true
  - rating: 4.5
  - reviewCount: 100
  - imageUrl: "https://via.placeholder.com/300"
  - createdAt: timestamp
```

---

## 🔧 التخصيص

### تغيير الألوان:
```xml
<!-- app/src/main/res/values/colors.xml -->
<color name="primary">#00D9FF</color> <!-- غيّر هذا -->
```

### تغيير النصوص:
```xml
<!-- app/src/main/res/values/strings.xml -->
<string name="app_name">اسم التطبيق الجديد</string>
```

---

## 📱 البناء للإنتاج

```bash
# في Terminal:
cd SportHub
./gradlew assembleRelease

# الملف في:
app/build/outputs/apk/release/app-release.apk
```

**ملاحظة**: تحتاج إلى توقيع التطبيق قبل نشره على Google Play

---

## ❓ المساعدة

### مصادر إضافية:
- [Android Developers](https://developer.android.com/)
- [Firebase Docs](https://firebase.google.com/docs)
- [Material Design 3](https://m3.material.io/)

### المشاكل الشائعة:
- تحقق من [Issues](https://github.com/Majd868/SportHub/issues)
- افتح Issue جديد إذا لزم الأمر

---

## ✅ Checklist قبل التشغيل

- [ ] Android Studio مثبت
- [ ] مشروع Firebase منشأ
- [ ] google-services.json محدّث
- [ ] Authentication مفعّل
- [ ] Firestore مفعّل
- [ ] Security Rules محدّثة
- [ ] الجهاز/Emulator جاهز

---

**مبروك! 🎉 التطبيق جاهز للتشغيل**

للأسئلة: افتح [Issue](https://github.com/Majd868/SportHub/issues/new)
