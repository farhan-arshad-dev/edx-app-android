package org.edx.mobile.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.accessibility.CaptioningManager;

import org.edx.mobile.R;
import org.edx.mobile.annotation.Nullable;
import org.edx.mobile.model.api.TranscriptModel;
import org.edx.mobile.module.prefs.LoginPrefs;

import java.util.LinkedHashMap;
import java.util.Locale;

public class LocaleUtils {

    public static String getCountryNameFromCode(@NonNull String countryCode) throws InvalidLocaleException {
        final String uppercaseRegion = countryCode.toUpperCase(Locale.ROOT);
        if (!isValidBcp47Alpha(uppercaseRegion, 2, 2)) {
            throw new InvalidLocaleException("Invalid region: " + countryCode);
        }
        return new Locale("", countryCode).getDisplayCountry();
    }

    public static String getLanguageNameFromCode(@NonNull String languageCode) throws InvalidLocaleException {
        final String lowercaseLanguage = languageCode.toLowerCase(Locale.ROOT);

        switch (lowercaseLanguage) {
            /* Chinese languages are special cases. The server uses different codes when compared
            to Android's locale library. Additionally, zh_CN and zh_TW are not languageCodes that
            Android's locale recognizes as exceptions. */
            case "zh_cn":
            case "zh_hans":
                return Locale.SIMPLIFIED_CHINESE.getDisplayLanguage();
            case "zh_tw":
            case "zh_hant":
                return Locale.TRADITIONAL_CHINESE.getDisplayLanguage();
            default:
                if (!isValidBcp47Alpha(lowercaseLanguage, 2, 3)) {
                    throw new InvalidLocaleException("Invalid language: " + languageCode);
                }
                return new Locale(languageCode).getDisplayLanguage();
        }
    }

    /*
     * Copied from Locale.Builder in API 21
     * https://github.com/google/j2objc/blob/master/jre_emul/android/platform/libcore/ojluni/src/main/java/java/util/Locale.java#L1766
     */
    private static boolean isValidBcp47Alpha(String string, int lowerBound, int upperBound) {
        final int length = string.length();
        if (length < lowerBound || length > upperBound) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            final char character = string.charAt(i);
            if (!(character >= 'a' && character <= 'z' ||
                    character >= 'A' && character <= 'Z')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Default Language List
     *
     * @param transcript
     * @return list of languages available in transcript
     */
    public static LinkedHashMap<String, String> getLanguageList(TranscriptModel transcript) {
        if (transcript != null) {
            return transcript.getLanguageList();
        }
        return null;
    }

    /**
     * Utility method that return the Current language code of the device
     *
     * @param context - current {@link Context}of the application
     * @return current language code
     */
    public static String getCurrentDeviceLanguage(Context context) {
        final CaptioningManager captionManager = (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);
        String languageCode = null;
        // Check if captioning is enabled in accessibility settings
        if (captionManager.isEnabled()) {
            final Locale cManagerLocale = captionManager.getLocale();
            if (cManagerLocale != null) {
                languageCode = cManagerLocale.getLanguage();
            }
        }
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = Locale.getDefault().getLanguage();
        }
        // Android return iw in case of Hebrew
        if ("iw".equals(languageCode)) {
            languageCode = "he";
        }
        return languageCode;
    }

    /**
     * Utility method to extract the download url from the {@link TranscriptModel} based
     * the last select transcript language / current device language.
     *
     * @param context    - current application {@link Context}
     * @param transcript - {@link TranscriptModel} transcript model contains transcript info
     * @return downloadable transcript url that can be null is transcripts are not available
     */
    @Nullable
    public static String getTranscriptURL(@NonNull Context context,
                                          @NonNull TranscriptModel transcript) {
        String subtitleLanguage = new LoginPrefs(context).getSubtitleLanguage();
        if (subtitleLanguage == null ||
                subtitleLanguage.equals(context.getString(R.string.lbl_cc_none))) {
            subtitleLanguage = LocaleUtils.getCurrentDeviceLanguage(context);
        }
        String transcriptUrl = null;
        if (transcript.containsKey(subtitleLanguage)) {
            transcriptUrl = transcript.get(subtitleLanguage);
        } else if (transcript.entrySet().size() > 0) {
            transcriptUrl = transcript.entrySet().iterator().next().getValue();
        }
        return transcriptUrl;
    }
}
