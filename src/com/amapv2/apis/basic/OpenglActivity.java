package com.amapv2.apis.basic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.RemoteException;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amapv2.apis.R;
import com.autonavi.amap.mapcore.FPoint;

/**
 * AMapV2地图中介绍如何显示一个基本地图
 */
public class OpenglActivity extends Activity implements CustomRenderer {
	private LatLng latlng1 = new LatLng(39.924216, 116.3978653);
	private LatLng latlng2 = new LatLng(39.624216, 116.6978653);
	private LatLng latlng3 = new LatLng(39.424216, 116.5978653);

	private MapView mapView;
	private AMap aMap;
	private FloatBuffer lineVertexBuffer;
	private FloatBuffer trianglesVertexBuffer;
	private int triangleSize = 0;
	private List<LatLng> latLngPolygon = new ArrayList<LatLng>();
	private float ScaleFactor = 10000000000.f;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opengl_activity);
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
			latLngPolygon.add(latlng1);
			latLngPolygon.add(latlng2);
			latLngPolygon.add(latlng3);
			aMap.setCustomRenderer(this);
		}
	}

	public void calMapFPoint() throws RemoteException {
		PointF[] polyPoints = new PointF[latLngPolygon.size()];
		float[] lineVertexs = new float[3 * latLngPolygon.size()];
		int i = 0;
		for (LatLng xy : latLngPolygon) {
			polyPoints[i] = aMap.getProjection().toMapLocation(xy);
			lineVertexs[i * 3] = polyPoints[i].x;
			lineVertexs[i * 3 + 1] = polyPoints[i].y;
			lineVertexs[i * 3 + 2] = 0.0f;
			i++;
		}

		PointF[] triangles = triangleForPoints(polyPoints);
		if (triangles.length == 0) {
			if (ScaleFactor == 10000000000.f) {
				ScaleFactor = 100000000.f;
			} else {
				ScaleFactor = 10000000000.f;
			}
			triangles = triangleForPoints(polyPoints);
		}
		float[] trianglesVertexs = new float[3 * triangles.length];
		i = 0;
		for (PointF txy : triangles) {
			trianglesVertexs[i * 3] = txy.x;
			trianglesVertexs[i * 3 + 1] = txy.y;
			trianglesVertexs[i * 3 + 2] = 0.0f;
			i++;
		}

		triangleSize = triangles.length;

		lineVertexBuffer = makeFloatBuffer(lineVertexs);
		trianglesVertexBuffer = makeFloatBuffer(trianglesVertexs);

	}

	private FloatBuffer makeFloatBuffer(float[] fs) {
		ByteBuffer bb = ByteBuffer.allocateDirect(fs.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(fs);
		fb.position(0);
		return fb;
	}

	private PointF[] triangleForPoints(PointF[] points) {
		int count = points.length;
		FPoint[] a = new FPoint[count];
		for (int i = 0; i < count; i++) {
			a[i] = new FPoint(points[i].x * ScaleFactor, points[i].y
					* ScaleFactor);
		}
		PointF[] triangles = new PointF[count];
		for (int i = 0; i < count; i++) {
			triangles[i] = new PointF();
			triangles[i].x = a[i].x / ScaleFactor;
			triangles[i].y = a[i].y / ScaleFactor;
		}
		return triangles;
	}

	private void drawGL(GL10 gl, int fillcolor, int strokeColor,
			FloatBuffer linePoints, float lineWidth, FloatBuffer triangles,
			int pointSize, int triangSize) {
		/**
		 * 绘制polyline
		 */
		drawPolyline(gl, fillcolor, triangles, 100, triangSize);
		/**
		 * 绘制polygone
		 */
		 drawPolygon(gl, strokeColor, triangles, 1, triangSize);
	}

	private  void drawPolyline(GL10 gl, int color,
			FloatBuffer lineVertexBuffer, float lineWidth, int pointSize) {
		if (lineWidth == 0) {
			return;
		}
		gl.glPushMatrix();
		gl.glColor4f(1, 1, 1, 1);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		float colorA = Color.alpha(color) / 255f;
		float colorR = Color.red(color) / 255f;
		float colorG = Color.green(color) / 255f;
		float colorB = Color.blue(color) / 255f;

		// 绘线
		gl.glEnable(GL10.GL_LINE_LOOP);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVertexBuffer);
		gl.glColor4f(colorR, colorG, colorB, colorA);
		gl.glLineWidth(lineWidth);

		gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, pointSize);
		gl.glDisable(GL10.GL_LINE_LOOP);

		gl.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_NICEST);
		float size = lineWidth;
		if (size >= 10) {
			size = 6;
		} else if (size >= 5) {
			size -= 2;
		} else if (size >= 2) {
			size -= 1;
		}
		gl.glColor4f(colorR, colorG, colorB, colorA / 4);
		gl.glPointSize(size);
		gl.glDrawArrays(GL10.GL_POINTS, 1, pointSize - 1);
		gl.glDisable(GL10.GL_POINT_SMOOTH);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glPopMatrix();
		gl.glFlush();

	}

	private  void drawPolygon(GL10 gl, int color,
			FloatBuffer lineVertexBuffer, float lineWidth, int pointSize) {
		if (lineWidth == 0) {
			return;
		}
		gl.glPushMatrix();
		gl.glColor4f(1, 1, 1, 1);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		float colorA = Color.alpha(color) / 255f;
		float colorR = Color.red(color) / 255f;
		float colorG = Color.green(color) / 255f;
		float colorB = Color.blue(color) / 255f;

		// 绘线
		gl.glEnable(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVertexBuffer);
		gl.glColor4f(colorR, colorG, colorB, colorA);
		gl.glLineWidth(lineWidth);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, pointSize);
		gl.glDisable(GL10.GL_VERTEX_ARRAY);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glPopMatrix();
		gl.glFlush();

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
	public void onDrawFrame(GL10 gl) {
		if (lineVertexBuffer == null || trianglesVertexBuffer == null) {
			try {
				calMapFPoint();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		if (lineVertexBuffer != null && trianglesVertexBuffer != null) {
			drawGL(gl, Color.BLACK, Color.BLUE, lineVertexBuffer, 2,
					trianglesVertexBuffer, 4, triangleSize);

		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

	}

	@Override
	public void OnMapReferencechanged() {
		try {
			calMapFPoint();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
