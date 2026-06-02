package com.sporthub.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

/**
 * يُحلّل أخطاء Firebase ويعرض رسائل واضحة للمستخدم / المطوّر.
 */
public class FirebaseErrorHelper {

    /** قواعد Firestore المناسبة للمشروع — انسخها في Firebase Console */
    public static final String FIRESTORE_RULES =
            "rules_version = '2';\n" +
            "service cloud.firestore {\n" +
            "  match /databases/{database}/documents {\n\n" +
            "    // المنتجات: الجميع يقرأ، المسجّل يضيف\n" +
            "    match /products/{id} {\n" +
            "      allow read: if true;\n" +
            "      allow create: if request.auth != null;\n" +
            "      allow update, delete: if request.auth != null\n" +
            "        && resource.data.sellerId == request.auth.uid;\n" +
            "    }\n\n" +
            "    // الطلبات\n" +
            "    match /orders/{id} {\n" +
            "      allow read: if request.auth != null\n" +
            "        && resource.data.userId == request.auth.uid;\n" +
            "      allow create: if request.auth != null;\n" +
            "    }\n\n" +
            "    // المستخدمون\n" +
            "    match /users/{userId} {\n" +
            "      allow read, write: if request.auth != null\n" +
            "        && request.auth.uid == userId;\n" +
            "    }\n" +
            "  }\n" +
            "}";

    /**
     * يُحلّل الخطأ ويُعيد رسالة عربية مناسبة.
     */
    public static String getArabicMessage(Exception e) {
        if (e == null) return "خطأ غير معروف";
        String msg = e.getMessage();
        if (msg == null) return "خطأ غير معروف";

        if (msg.contains("PERMISSION_DENIED"))
            return "⛔ مرفوض — قواعد Firestore تمنع هذه العملية";
        if (msg.contains("UNAVAILABLE") || msg.contains("NETWORK") || msg.contains("Unable to resolve host"))
            return "📵 لا يوجد اتصال بالإنترنت";
        if (msg.contains("NOT_FOUND"))
            return "المستند غير موجود";
        if (msg.contains("ALREADY_EXISTS"))
            return "المستند موجود مسبقاً";
        if (msg.contains("UNAUTHENTICATED"))
            return "يرجى تسجيل الدخول أولاً";
        if (msg.contains("RESOURCE_EXHAUSTED"))
            return "تجاوزت حصة Firestore المجانية مؤقتاً";

        return msg; // اعرض الخطأ الحرفي لأي حالة أخرى
    }

    /**
     * هل الخطأ بسبب قواعد الأمان؟
     */
    public static boolean isPermissionDenied(Exception e) {
        if (e == null || e.getMessage() == null) return false;
        return e.getMessage().contains("PERMISSION_DENIED");
    }

    /**
     * يعرض dialog واضح عند خطأ الصلاحيات مع تعليمات الإصلاح.
     */
    public static void showPermissionFixDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("⛔ قواعد Firestore تمنع الكتابة")
                .setMessage(
                    "لإصلاح هذا الخطأ اتّبع الخطوات التالية:\n\n" +
                    "1️⃣  افتح Firebase Console\n" +
                    "     console.firebase.google.com\n\n" +
                    "2️⃣  اختر مشروعك → Firestore Database\n\n" +
                    "3️⃣  اضغط تبويب  Rules\n\n" +
                    "4️⃣  احذف كل المحتوى والصق القواعد\n" +
                    "     المعروضة في الحوار التالي\n\n" +
                    "5️⃣  اضغط  Publish"
                )
                .setPositiveButton("عرض القواعد", (d, w) -> showRulesDialog(context))
                .setNegativeButton("لاحقاً", null)
                .show();
    }

    /**
     * يعرض نص قواعد Firestore الجاهز للنسخ.
     */
    public static void showRulesDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("📋 انسخ هذه القواعد")
                .setMessage(FIRESTORE_RULES)
                .setPositiveButton("تم", null)
                .show();
    }
}
