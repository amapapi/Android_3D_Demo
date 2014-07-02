package com.amapv2.apis.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.MarkerOptions;
import com.amapv2.apis.R;
import com.amapv2.apis.util.Constants;
import com.amapv2.apis.util.ToastUtil;

/**
 * AMapV2地图中简单介绍一些Camera的用法.
 */
public class CameraActivity extends Activity implements OnClickListener,
		CancelableCallback {
	private static final int SCROLL_BY_PX = 100;
	private MapView mapView;
	private AMap aMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_activity);
	    /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
       // MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
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
			setUpMap();
		}
		Button stopAnimation = (Button) findViewById(R.id.stop_animation);
		stopAnimation.setOnClickListener(this);
		
		ToggleButton animate = (ToggleButton) findViewById(R.id.animate);
		animate.setOnClickListener(this);
		
		Button Lujiazui = (Button) findViewById(R.id.Lujiazui);
		Lujiazui.setOnClickListener(this);
		
		Button Zhongguancun = (Button) findViewById(R.id.Zhongguancun);
		Zhongguancun.setOnClickListener(this);
		
		Button scrollLeft = (Button) findViewById(R.id.scroll_left);
		scrollLeft.setOnClickListener(this);
		
		Button scrollRight = (Button) findViewById(R.id.scroll_right);
		scrollRight.setOnClickListener(this);
		
		Button scrollUp = (Button) findViewById(R.id.scroll_up);
		scrollUp.setOnClickListener(this);
		
		Button scrollDown = (Button) findViewById(R.id.scroll_down);
		scrollDown.setOnClickListener(this);
		
		Button zoomIn = (Button) findViewById(R.id.zoom_in);
		zoomIn.setOnClickListener(this);
		
		Button zoomOut = (Button) findViewById(R.id.zoom_out);
		zoomOut.setOnClickListener(this);
	}

	/**
	 * 往地图上添加一个marker
	 */
	private void setUpMap() {
		aMap.setPointToCenter(0,0);//设置地图以屏幕坐标(0,0)为中心进行旋转，默认是以屏幕中心旋转
		aMap.addMarker(new MarkerOptions()
				.perspective(true)
				.position(Constants.FANGHENG)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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

	/**
	 * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
	 */
	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		boolean animated = ((CompoundButton) findViewById(R.id.animate))
				.isChecked();
		if (animated) {
			aMap.animateCamera(update, 1000, callback);
		} else {
			aMap.moveCamera(update);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 点击停止动画按钮响应事件
		 */
		case R.id.stop_animation:
			aMap.stopAnimation();
			break;
		/**
		 * 点击“去中关村”按钮响应事件
		 */
		case R.id.Zhongguancun:
			changeCamera(
					CameraUpdateFactory.newCameraPosition(new CameraPosition(
							Constants.ZHONGGUANCUN, 18, 0, 30)), null);
			break;

		/**
		 * 点击“去陆家嘴”按钮响应事件
		 */
		case R.id.Lujiazui:
			changeCamera(
					CameraUpdateFactory.newCameraPosition(new CameraPosition(
							Constants.SHANGHAI, 18, 30, 0)), this);
			break;
		/**
		 * 点击向左移动按钮响应事件，camera将向左边移动
		 */
		case R.id.scroll_left:
			changeCamera(CameraUpdateFactory.scrollBy(-SCROLL_BY_PX, 0), null);
			break;
		/**
		 * 点击向右移动按钮响应事件，camera将向右边移动
		 */
		case R.id.scroll_right:
			changeCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX, 0), null);
			break;
		/**
		 * 点击向上移动按钮响应事件，camera将向上边移动
		 */
		case R.id.scroll_up:
			changeCamera(CameraUpdateFactory.scrollBy(0, -SCROLL_BY_PX), null);
			break;
		/**
		 * 点击向下移动按钮响应事件，camera将向下边移动
		 */
		case R.id.scroll_down:
			changeCamera(CameraUpdateFactory.scrollBy(0, SCROLL_BY_PX), null);
			break;
		/**
		 * 点击地图放大按钮响应事件
		 */
		case R.id.zoom_in:
			changeCamera(CameraUpdateFactory.zoomIn(), null);
			break;
		/**
		 * 点击地图缩小按钮响应事件
		 */
		case R.id.zoom_out:
			changeCamera(CameraUpdateFactory.zoomOut(), null);
			break;
		default:
			break;
		}
	}

	/**
	 * 地图动画效果终止回调方法
	 */
	@Override
	public void onCancel() {
		ToastUtil.show(CameraActivity.this, "Animation to 陆家嘴 canceled");
	}

	/**
	 * 地图动画效果完成回调方法
	 */
	@Override
	public void onFinish() {
		ToastUtil.show(CameraActivity.this, "Animation to 陆家嘴 complete");
	}
}
