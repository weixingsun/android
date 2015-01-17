package cat.app.osmap.util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * AppUtils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-9
 */
public class AppUtils {

    public static void init(Activity activity) {
        //initTrineaInfo(activity);
        initActionBar(activity);
    }

    public static void urlOpen(Context context, String url) {
        Uri uriUrl = Uri.parse(url);
        Intent i = new Intent(Intent.ACTION_VIEW, uriUrl);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private static void initActionBar(final Activity activity) {
        if (activity == null) {
            return;
        }

        ActionBar bar = activity.getActionBar();
        /*if (activity instanceof MainActivity) {
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM
                    | ActionBar.DISPLAY_SHOW_HOME);
        } else {
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
        }*/
    }

/*    private static void initTrineaInfo(final Activity activity) {
        if (activity == null) {
            return;
        }

        Button trineaInfoTv = (Button)activity.findViewById(R.id.trinea_info);
        final String[] result = getText(activity);
        if (result == null) {
            return;
        }

        Spanned text = Html.fromHtml(result[1]);
        trineaInfoTv.setText(text);
        trineaInfoTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                urlOpen(activity, result[0]);
            }
        });
    }*/

/*    private static String[] getText(Activity activity) {
        if (activity == null) {
            return null;
        }

        int prefixSrcId = R.string.description, contentSrcId;
        String url = null;
        Class<?> sourClass = activity.getClass();
        if (sourClass == DownloadManagerDemo.class) {
            url = "http://www.trinea.cn/android/android-downloadmanager/";
            contentSrcId = R.string.desc_download_manager;
        } 
        String[] result = new String[] {url,
                getUrlInfo(activity.getString(prefixSrcId), url, activity.getString(contentSrcId))};
        return result;
    }*/

    private static String getUrlInfo(String prefix, String url, String content) {
        return new StringBuilder().append(prefix).append("<a href=\"").append(url).append("\">").append(content)
                .append("</a>").toString();
    }
}
