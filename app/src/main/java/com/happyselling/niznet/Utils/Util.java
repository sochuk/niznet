package com.happyselling.niznet.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.happyselling.niznet.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    public void hideStatusBar(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity)context).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(context.getResources().getColor(R.color.bg_color));
        }
    }

    public boolean cekEmail(String email, Editable s){
        boolean result = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        result = email.matches(emailPattern) && s.length() > 0;
        return result;
    }

    public static String parsingRupiah(int rupiah) {
        DecimalFormat mataUangIndonesia = (DecimalFormat)DecimalFormat.getCurrencyInstance();
        mataUangIndonesia.setMinimumFractionDigits(0);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp");
        formatRp.setGroupingSeparator('.');
        mataUangIndonesia.setDecimalFormatSymbols(formatRp);
        return mataUangIndonesia.format((long)rupiah);
    }

    public String formatDateToIndonesianDate(String dates){
        try {
            Log.d("###", "date awal: "+dates);
            Locale localeID = new Locale("id", "ID");
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Date date = fmt.parse(dates);
            SimpleDateFormat fmtOut = new SimpleDateFormat("EEEE, dd MMMM yyyy", localeID);
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    return "";
    }
}
