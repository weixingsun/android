package cat.app.gmap;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

public class GMapConverter {
	private static final String TAG = "GMap.GMapConverter";

	// ���ݵ�ַ��ȡ��Ӧ�ľ�γ��
	public static double[] getLocationInfo(String address) {
    	Log.i(TAG, "find location by name: "+address);
		// ����һ��HttpClient��������ָ����ַ��������
		HttpClient client = new DefaultHttpClient();
		// ��ָ����ַ����GET����
		String parsedValue=null;
		try {
			parsedValue = java.net.URLEncoder.encode(address, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?"
				+ "address=" + parsedValue + "&sensor=false");
		StringBuilder sb = new StringBuilder();
		try {
			// ��ȡ����������Ӧ
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			// ��ȡ��������Ӧ��������
			InputStream stream = entity.getContent();
			int b;
			// ѭ����ȡ��������Ӧ
			while ((b = stream.read()) != -1) {
				sb.append((char) b);
			}
			// �����������ص��ַ���ת��ΪJSONObject����
			JSONObject jsonObject = new JSONObject(sb.toString());
			// ��JSONObject������ȡ������λ�õ�location����
			JSONObject location = jsonObject.getJSONArray("results")
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location");
			// ��ȡ������Ϣ
			double longitude = location.getDouble("lng");
			// ��ȡγ����Ϣ
			double latitude = location.getDouble("lat");
			// �����ȡ�γ����Ϣ���double[]����
			return new double[] { longitude, latitude };
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "exception="+e.getMessage());
		}
		return null;
	}

	// ���ݾ�γ�Ȼ�ȡ��Ӧ�ĵ�ַ
	public static String getAddress(double longitude, double latitude) {
		// ����һ��HttpClient��������ָ����ַ��������
		HttpClient client = new DefaultHttpClient();
		// ��ָ����ַ����GET����
		HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/"
				+ "geocode/json?latlng=" + latitude + "," + longitude
				+ "&sensor=false&ion=cn");
		StringBuilder sb = new StringBuilder();
		try {
			// ִ������
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			// ��ȡ��������Ӧ���ַ���
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				sb.append((char) b);
			}
			// �ѷ�������Ӧ���ַ���ת��ΪJSONObject
			JSONObject jsonObj = new JSONObject(sb.toString());
			// ��������Ӧ����еĵ�ַ����
			return jsonObj.getJSONArray("results").getJSONObject(0)
					.getString("formatted_address");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}