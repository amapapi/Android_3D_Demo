package com.amapv2.apis.overlay;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.TileOverlay;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.TileProvider;
import com.amap.api.maps.model.UrlTileProvider;
import com.amapv2.apis.R;

/**
 * TileOverlay功能介绍
 */
public class TileOverlayActivity extends Activity implements OnClickListener {
	private MapView mapView;
	private AMap aMap;
	private TileOverlay tileOverlay;
	private Button firstFloor, secondFloor, thridFloor, openTile;
	private String url = "http://sdkdemo.amap.com:8080/tileserver/Tile?x=%d&y=%d&z=%d&f=%d";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tileoverlay_activity);
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
		firstFloor = (Button) findViewById(R.id.firstfloor);
		firstFloor.setOnClickListener(this);
		secondFloor = (Button) findViewById(R.id.secondfloor);
		secondFloor.setOnClickListener(this);
		thridFloor = (Button) findViewById(R.id.thridfloor);
		thridFloor.setOnClickListener(this);
		openTile = (Button) findViewById(R.id.opentile);
		openTile.setOnClickListener(this);

		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					39.910695, 116.372830), 19));
			showTileOverlay(1);
		}
	}

	/**
	 * 显示第几层的tileOverlay
	 */
	private void showTileOverlay(final int floor) {
		if (tileOverlay != null) {
			tileOverlay.remove();
		}
		TileProvider tileProvider = new UrlTileProvider(256, 256) {
			public URL getTileUrl(int x, int y, int zoom) {
				try {
					String s=String.format(url, x, y, zoom, floor);
					return new URL(String.format(url, x, y, zoom, floor));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		if (tileProvider != null) {
			tileOverlay = aMap.addTileOverlay(new TileOverlayOptions()
					.tileProvider(tileProvider)
					.diskCacheDir("/storage/amap/cache").diskCacheEnabled(true)
					.diskCacheSize(100));

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.firstfloor:
			showTileOverlay(1);
			break;
		case R.id.secondfloor:
			showTileOverlay(2);
			break;
		case R.id.thridfloor:
			showTileOverlay(3);
			break;
		case R.id.opentile:
			if ("打开TileOverlay".equals(openTile.getText().toString())) {
				openTile.setText("关闭TileOverlay");
				tileOverlay.setVisible(false);
			} else if ("关闭TileOverlay".equals(openTile.getText().toString())) {
				openTile.setText("打开TileOverlay");
				tileOverlay.setVisible(true);
			}
			break;
		default:
			break;
		}
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

}
