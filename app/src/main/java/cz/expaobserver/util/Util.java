package cz.expaobserver.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cz.expaobserver.R;
import cz.expaobserver.ui.ObserverApplication;

/**
 * Created by pechanecjr on 10. 11. 2014.
 */
public class Util {

    private static final String TAG = Util.class.getSimpleName();

    public static final boolean API_21 = Build.VERSION.SDK_INT >= 21;
    public static final boolean API_19 = Build.VERSION.SDK_INT >= 19;
    public static final boolean API_14 = Build.VERSION.SDK_INT >= 14;
    public static final boolean API_11 = Build.VERSION.SDK_INT >= 11;

    private static final ObserverApplication APP = ObserverApplication.getInstance();

    private Util() {
    }

    public static void varDump(Object obj, Class<?>... types) {
        List<Class<?>> classes = Arrays.asList(types);
        Class<?> cls = obj.getClass();
        for (Field f : cls.getDeclaredFields()) {
            if (classes.isEmpty() || !classes.contains(f.getType())) continue;
            f.setAccessible(true);
            try {
                Log.d(f.getName(), f.get(obj) + "");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Window {
        private static final float STATUS_BAR_SCRIM = 0.2f; // 0x33 // 51

        private Window() {
        }

        public static void setStatusBarColor(android.view.Window window, int color, boolean scrim) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (scrim) color = Util.Resources.modifyColor(color, 1 - STATUS_BAR_SCRIM);
                window.setStatusBarColor(color);
            }
        }

        public static void clearBackground(android.view.Window window) {
            window.setBackgroundDrawable(new ColorDrawable(0x0));
        }
    }

    public static class Material {
        private static final String TAG = Util.TAG + "$" + Material.class.getSimpleName();

        private static final HashMap<TintConfig, Drawable> CACHE = new HashMap<>();

        private Material() {
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public static Drawable getControlDrawable(@DrawableRes int drawableId, Context context) {
            TypedArray ta = context.obtainStyledAttributes(new int[]{API_21 ? android.R.attr.colorControlNormal : R.attr.colorControlNormal});
            int c = ta.getColor(0, Color.BLACK);
            ta.recycle();
            Drawable d = APP.getResources().getDrawable(drawableId);
            return getTintedDrawableInternal(d, c);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public static Drawable getControlDrawable(Drawable d, Context context) {
            TypedArray ta = context.obtainStyledAttributes(new int[]{API_21 ? android.R.attr.colorControlNormal : R.attr.colorControlNormal});
            int c = ta.getColor(0, Color.BLACK);
            ta.recycle();
            return getTintedDrawableInternal(d, c);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public static Drawable getAccentDrawable(@DrawableRes int drawableId, Context context) {
            TypedArray ta = context.obtainStyledAttributes(new int[]{API_21 ? android.R.attr.colorAccent : R.attr.colorAccent});
            int c = ta.getColor(0, Color.BLACK);
            ta.recycle();
            Drawable d = APP.getResources().getDrawable(drawableId);
            return getTintedDrawableInternal(d, c);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public static Drawable getAccentDrawable(Drawable d, Context context) {
            TypedArray ta = context.obtainStyledAttributes(new int[]{API_21 ? android.R.attr.colorAccent : R.attr.colorAccent});
            int c = ta.getColor(0, Color.BLACK);
            ta.recycle();
            return getTintedDrawableInternal(d, c);
        }

        public static Drawable getTintedDrawable(@DrawableRes int drawableId, @ColorRes int colorId) {
            Drawable d = APP.getResources().getDrawable(drawableId);
            int c = APP.getResources().getColor(colorId);
            return getTintedDrawableInternal(d, c);
        }

        private static Drawable getTintedDrawableInternal(Drawable d, int c) {
            TintConfig tc = new TintConfig(d, c);
            if (CACHE.containsKey(tc)) {
                return CACHE.get(tc);
            }

            d = d.mutate();
            d.setColorFilter(c, PorterDuff.Mode.SRC_IN);
            CACHE.put(tc, d);
            return d;
        }

        public static void tintMenuItem(MenuItem item, int color) {
            Drawable icon = item.getIcon();
            if (icon != null) {
                icon = icon.mutate();
                icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                item.setIcon(icon);
            }
        }

        public static void tintMenu(Menu menu, int color) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                tintMenuItem(item, color);
            }
        }

        public static void tintMenu(Menu menu, Context context) {
            TypedArray ta = context.obtainStyledAttributes(new int[]{R.attr.colorControlNormal});
            int color = ta.getColor(0, Color.BLACK);
            ta.recycle();
            tintMenu(menu, color);
        }

        private static class TintConfig {
            public TintConfig(Drawable drawable, int color) {
                this.drawable = drawable;
                this.color = color;
            }

            public Drawable drawable;
            public int color;

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof TintConfig)) return false;

                TintConfig that = (TintConfig) o;

                if (color != that.color) return false;
                if (!drawable.equals(that.drawable)) return false;

                return true;
            }

            @Override
            public int hashCode() {
                int result = drawable.hashCode();
                result = 31 * result + color;
                return result;
            }
        }

    }

    public static class Attr {
        private static final String TAG = Util.TAG + "$" + Attr.class.getSimpleName();

        private Attr() {
        }

        public static int getColor(Context context, int attr, int fallback) {
            TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
            try {
                return ta.getColor(0, fallback);
            } finally {
                ta.recycle();
            }
        }
    }

    public static class Resources {
        private static final String TAG = Util.TAG + "$" + Resources.class.getSimpleName();

        private Resources() {
        }

        public static int dpToPixelSize(int dp) {
            return Math.round(0.5f + dpToPixel(dp));
        }

        public static int dpToPixelOffset(int dp) {
            return (int) dpToPixel(dp);
        }

        public static float dpToPixel(int dp) {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, APP.getResources().getDisplayMetrics());
        }

        public static int getHeight(int width, float ratio) {
            return (int) (width / ratio);
        }

        public static int getWidth(int height, float ratio) {
            return (int) (height * ratio);
        }

        public static int modifyColor(int color, float light) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= Math.min(light, 1f); // value component
            return Color.HSVToColor(hsv);
        }
    }

    public static class View {
        public static final float RATIO_16_BY_9 = 16 / 9f;
        public static final float RATIO_3_BY_2 = 3 / 2f;
        public static final float RATIO_4_BY_3 = 4 / 3f;
        public static final float RATIO_1_BY_1 = 1f;
        public static final float RATIO_3_BY_4 = 3 / 4f;
        public static final float RATIO_2_BY_3 = 2 / 3f;

        private View() {
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static void setBackground(android.view.View v, Drawable d) {
            if (Build.VERSION.SDK_INT < 16) {
                v.setBackgroundDrawable(d);
            } else {
                v.setBackground(d);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public static void setCompoundDrawables(TextView tv, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tv.setCompoundDrawables(start, top, end, bottom);
            } else {
                tv.setCompoundDrawablesRelative(start, top, end, bottom);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public static void setCompoundDrawablesWithIntrinsicBounds(TextView tv, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tv.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
            } else {
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static void removeOnGlobalLayoutListener(android.view.View v, ViewTreeObserver.OnGlobalLayoutListener l) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                v.getViewTreeObserver().removeGlobalOnLayoutListener(l);
            } else {
                v.getViewTreeObserver().removeOnGlobalLayoutListener(l);
            }
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public static void applyRatio(final android.view.View view, final float ratio) {
//      view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//        @Override
//        public void onGlobalLayout() {
//          if (Build.VERSION.SDK_INT < 16) {
//            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//          } else {
//            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//          }
//
            int width = view.getWidth();
            int height = Util.Resources.getHeight(width, ratio);

            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.height = height;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || view.isLaidOut())
                view.setLayoutParams(lp);
//        }
//      });
        }

        private static final Class<?> CLASS_SCROLL_VIEW = ScrollView.class;
        private static final Field SCROLL_VIEW_FIELD_EDGE_GLOW_TOP;
        private static final Field SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM;

        private static final Class<?> CLASS_LIST_VIEW = AbsListView.class;
        private static final Field LIST_VIEW_FIELD_EDGE_GLOW_TOP;
        private static final Field LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM;

        static {
            Field edgeGlowTop = null, edgeGlowBottom = null;

            for (Field f : CLASS_SCROLL_VIEW.getDeclaredFields()) {
                switch (f.getName()) {
                    case "mEdgeGlowTop":
                        f.setAccessible(true);
                        edgeGlowTop = f;
                        break;
                    case "mEdgeGlowBottom":
                        f.setAccessible(true);
                        edgeGlowBottom = f;
                        break;
                }
            }

            SCROLL_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop;
            SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom;

            for (Field f : CLASS_LIST_VIEW.getDeclaredFields()) {
                switch (f.getName()) {
                    case "mEdgeGlowTop":
                        f.setAccessible(true);
                        edgeGlowTop = f;
                        break;
                    case "mEdgeGlowBottom":
                        f.setAccessible(true);
                        edgeGlowBottom = f;
                        break;
                }
            }

            LIST_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop;
            LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public static void setEdgeGlowColor(AbsListView listView, int color) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    EdgeEffect ee;
                    ee = (EdgeEffect) LIST_VIEW_FIELD_EDGE_GLOW_TOP.get(listView);
                    ee.setColor(color);
                    ee = (EdgeEffect) LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM.get(listView);
                    ee.setColor(color);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public static void setEdgeGlowColor(ScrollView scrollView, int color) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    EdgeEffect ee;
                    ee = (EdgeEffect) SCROLL_VIEW_FIELD_EDGE_GLOW_TOP.get(scrollView);
                    ee.setColor(color);
                    ee = (EdgeEffect) SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM.get(scrollView);
                    ee.setColor(color);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
