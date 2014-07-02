package com.amapv2.apis.overlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amapv2.apis.R;
import com.amapv2.apis.util.Constants;

/**
 * AMapV2地图中简单介绍一些Polyline的用法.
 */
public class PolylineActivity extends Activity implements
		OnSeekBarChangeListener {
	private static final int WIDTH_MAX = 50;
	private static final int HUE_MAX = 255;
	private static final int ALPHA_MAX = 255;

	private AMap aMap;
	private MapView mapView;
	private Polyline polyline;
	private SeekBar mColorBar;
	private SeekBar mAlphaBar;
	private SeekBar mWidthBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.polyline_activity);
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
		mColorBar = (SeekBar) findViewById(R.id.hueSeekBar);
		mColorBar.setMax(HUE_MAX);
		mColorBar.setProgress(50);

		mAlphaBar = (SeekBar) findViewById(R.id.alphaSeekBar);
		mAlphaBar.setMax(ALPHA_MAX);
		mAlphaBar.setProgress(255);

		mWidthBar = (SeekBar) findViewById(R.id.widthSeekBar);
		mWidthBar.setMax(WIDTH_MAX);
		mWidthBar.setProgress(10);
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	private void setUpMap() {
		mColorBar.setOnSeekBarChangeListener(this);
		mAlphaBar.setOnSeekBarChangeListener(this);
		mWidthBar.setOnSeekBarChangeListener(this);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(4));
		aMap.setMapTextZIndex(2);
		// 绘制一个虚线三角形
		polyline = aMap.addPolyline((new PolylineOptions())
				.add(Constants.SHANGHAI,Constants.BEIJING, Constants.CHENGDU)
				.width(10).setDottedLine(true)
				.geodesic(true)
				.color(Color.argb(255, 1, 1, 1)));
		// 绘制一个乌鲁木齐到哈尔滨的大地曲线
		aMap.addPolyline((new PolylineOptions())
				.add(new LatLng(43.828, 87.621), new LatLng(45.808, 126.55))
				.geodesic(true).color(Color.RED));
		// 绘制一条带有纹理的直线
		aMap.addPolyline((new PolylineOptions())
				.add(new LatLng(29.654, 91.139), new LatLng(22.265, 114.188))
				.setCustomTexture(BitmapDescriptorFactory.defaultMarker()));
		
		
	
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

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	/**
	 * Polyline中对填充颜色，透明度，画笔宽度设置响应事件
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (polyline == null) {
			return;
		}
		if (seekBar == mColorBar) {
			polyline.setColor(Color.argb(progress, 1, 1, 1));
		} else if (seekBar == mAlphaBar) {
			float[] prevHSV = new float[3];
			Color.colorToHSV(polyline.getColor(), prevHSV);
			polyline.setColor(Color.HSVToColor(progress, prevHSV));
		} else if (seekBar == mWidthBar) {
			polyline.setWidth(progress);
		}
	}
}
