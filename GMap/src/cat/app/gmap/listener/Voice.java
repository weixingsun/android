package cat.app.gmap.listener;

import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;
import cat.app.gmap.R;
import cat.app.gmap.Util;

public class Voice {

		public static void promptSpeechInput(Activity activity) {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
					activity.getString(R.string.speech_prompt));
			try {
				activity.startActivityForResult(intent, Util.REQ_CODE_SPEECH_INPUT);
			} catch (ActivityNotFoundException a) {
				Toast.makeText(activity.getApplicationContext(),
						activity.getString(R.string.speech_not_supported),
						Toast.LENGTH_SHORT).show();
			}
		}
}
