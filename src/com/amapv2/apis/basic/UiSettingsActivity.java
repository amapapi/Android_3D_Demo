package com.amapv2.apis.basic;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amapv2.apis.R;
import com.amapv2.apis.util.ToastUtil;

/**
 * UI settings一些选项设置响应事件
 */
public class UiSettingsActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener, LocationSource,
		AMapLocationListener {
	private AMap aMap;
	private MapView mapView;
	private UiSettings mUiSettings;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy aMapManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_settings_activity);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
		}
		Button buttonScale = (Button) findViewById(R.id.buttonScale);
		buttonScale.setOnClickListener(this);
		CheckBox scaleToggle = (CheckBox) findViewById(R.id.scale_toggle);
		scaleToggle.setOnClickListener(this);
		CheckBox zoomToggle = (CheckBox) findViewById(R.id.zoom_toggle);
		zoomToggle.setOnClickListener(this);
		CheckBox compassToggle = (CheckBox) findViewById(R.id.compass_toggle);
		compassToggle.setOnClickListener(this);
		CheckBox mylocationToggle = (CheckBox) findViewById(R.id.mylocation_toggle);
		mylocationToggle.setOnClickListener(this);
		CheckBox scrollToggle = (CheckBox) findViewById(R.id.scroll_toggle);
		scrollToggle.setOnClickListener(this);
		CheckBox zoom_gesturesToggle = (CheckBox) findViewById(R.id.zoom_gestures_toggle);
		zoom_gesturesToggle.setOnClickListener(this);
		CheckBox tiltToggle = (CheckBox) findViewById(R.id.tilt_toggle);
		tiltToggle.setOnClickListener(this);
		CheckBox rotateToggle = (CheckBox) findViewById(R.id.rotate_toggle);
		rotateToggle.setOnClickListener(this);
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.logo_position);
		radioGroup.setOnCheckedChangeListener(this);

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 设置logo位置，左下，底部居中，右下
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (aMap != null) {
			if (checkedId == R.id.bottom_left) {
				mUiSettings
						.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置地图logo显示在左下方
			} else if (checkedId == R.id.bottom_center) {
				mUiSettings
						.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);// 设置地图logo显示在底部居中
			} else if (checkedId == R.id.bottom_right) {
				mUiSettings
						.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
			}
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		/**
		 * 一像素代表多少米
		 */
		case R.id.buttonScale:
			float scale = aMap.getScalePerPixel();
			ToastUtil.show(UiSettingsActivity.this, "每像素代表" + scale + "米");
			break;
		/**
		 * 设置地图默认的比例尺是否显示
		 */
		case R.id.scale_toggle:
			mUiSettings.setScaleControlsEnabled(((CheckBox) view).isChecked());

			break;
		/**
		 * 设置地图默认的缩放按钮是否显示
		 */
		case R.id.zoom_toggle:
			mUiSettings.setZoomControlsEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图默认的指南针是否显示
		 */
		case R.id.compass_toggle:
			mUiSettings.setCompassEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图默认的定位按钮是否显示
		 */
		case R.id.mylocation_toggle:
			aMap.setLocationSource(this);// 设置定位监听
			mUiSettings.setMyLocationButtonEnabled(((CheckBox) view)
					.isChecked()); // 是否显示默认的定位按钮
			aMap.setMyLocationEnabled(((CheckBox) view).isChecked());// 是否可触发定位并显示定位层
			break;
		/**
		 * 设置地图是否可以手势滑动
		 */
		case R.id.scroll_toggle:
			mUiSettings.setScrollGesturesEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图是否可以手势缩放大小
		 */
		case R.id.zoom_gestures_toggle:
			mUiSettings.setZoomGesturesEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图是否可以倾斜
		 */
		case R.id.tilt_toggle:
			mUiSettings.setTiltGesturesEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图是否可以旋转
		 */
		case R.id.rotate_toggle:
			mUiSettings.setRotateGesturesEnabled(((CheckBox) view).isChecked());
			break;
		default:
			break;
		}
	}

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
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (aMapManager == null) {
			aMapManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
			 */
			// Location API定位采用GPS和网络混合定位方式，时间最短是2000毫秒
			aMapManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (aMapManager != null) {
			aMapManager.removeUpdates(this);
			aMapManager.destory();
		}
		aMapManager = null;
	}
}
