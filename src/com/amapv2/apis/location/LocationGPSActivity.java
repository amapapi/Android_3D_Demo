package com.amapv2.apis.location;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amapv2.apis.R;
import com.amapv2.apis.util.AMapUtil;

/**
 * AMapV2地图中简单介绍GPS定位
 */
public class LocationGPSActivity extends Activity implements
		AMapLocationListener {
	private LocationManagerProxy locationManager;
	private TextView myLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationnetwork_activity);
		init();
	}

	private void init() {
		myLocation = (TextView) findViewById(R.id.myLocation);
		locationManager = LocationManagerProxy
				.getInstance(LocationGPSActivity.this);
		// API定位采用GPS定位方式，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		locationManager.requestLocationUpdates(
				LocationManagerProxy.GPS_PROVIDER, 2000, 10, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (locationManager != null) {
			locationManager.removeUpdates(this);
			locationManager.destory();
		}
		locationManager = null;
	}

	@Override
	protected void onDestroy() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
			locationManager.destory();
		}
		locationManager = null;
		super.onDestroy();
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * gps定位回调方法
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
					+ "\n精    度    :" + location.getAccuracy() + "米"
					+ "\n定位方式:" + location.getProvider() + "\n定位时间:" + AMapUtil
					.convertToTime(location.getTime()));
			myLocation.setText(str);
		}
	}
}
