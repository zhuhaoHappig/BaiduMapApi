package com.zhh.baidu.baidumapapi;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;


/**
 * 此demo实现静态画运动轨迹 同时展示如何使用自定义图标绘制并响应点击事件
 * author zhh
 */
public class StaticDemo extends Activity {

	// 定位相关
	BitmapDescriptor mCurrentMarker;
	MapView mMapView;
	BaiduMap mBaiduMap;
	Polyline mPolyline;
	LatLng target;
	MapStatus.Builder builder;
	List<LatLng> latLngs = new ArrayList<LatLng>();
	
	BitmapDescriptor startBD = BitmapDescriptorFactory
            .fromResource(R.drawable.ic_me_history_startpoint);
	BitmapDescriptor finishBD = BitmapDescriptorFactory
            .fromResource(R.drawable.ic_me_history_finishpoint);
	
	private Marker mMarkerA;
    private Marker mMarkerB;
    private InfoWindow mInfoWindow;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sta);
		
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		
		coordinateConvert();
		
		builder = new MapStatus.Builder();
		builder.target(target).zoom(18f);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

		MarkerOptions oStart = new MarkerOptions();//地图标记覆盖物参数配置类 
		oStart.position(latLngs.get(0));//覆盖物位置点，第一个点为起点
		oStart.icon(startBD);//设置覆盖物图片
		oStart.zIndex(1);//设置覆盖物Index
		mMarkerA = (Marker) (mBaiduMap.addOverlay(oStart)); //在地图上添加此图层

		//添加终点图层
		MarkerOptions oFinish = new MarkerOptions().position(latLngs.get(latLngs.size()-1)).icon(finishBD).zIndex(2);
		mMarkerB = (Marker) (mBaiduMap.addOverlay(oFinish));

	    
	    mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                
                if (marker.getZIndex() == mMarkerA.getZIndex() ) {//如果是起始点图层
                	TextView textView = new TextView(getApplicationContext());
                	textView.setText("起点");
                	textView.setTextColor(Color.BLACK);
                	textView.setGravity(Gravity.CENTER);
                	textView.setBackgroundResource(R.drawable.popup);
                	
                	//设置信息窗口点击回调
                	OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
                        public void onInfoWindowClick() {
                        	Toast.makeText(getApplicationContext(),"这里是起点", Toast.LENGTH_SHORT).show();
                            mBaiduMap.hideInfoWindow();//隐藏信息窗口
                        }
                    };
                    LatLng latLng = marker.getPosition();//信息窗口显示的位置点
                    /**
                     * 通过传入的 bitmap descriptor 构造一个 InfoWindow
                     * bd - 展示的bitmap
					   position - InfoWindow显示的位置点
					   yOffset - 信息窗口会与图层图标重叠，设置Y轴偏移量可以解决
					   listener - 点击监听者
                     */
                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(textView), latLng, -47, listener);
                    mBaiduMap.showInfoWindow(mInfoWindow);//显示信息窗口
                    
                } else if (marker.getZIndex() == mMarkerB.getZIndex()) {//如果是终点图层
                	Button button = new Button(getApplicationContext());
                    button.setText("终点");
                    button.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                        	Toast.makeText(getApplicationContext(),"这里是终点", Toast.LENGTH_SHORT).show();
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    LatLng latLng = marker.getPosition();
                    /**
                     * 通过传入的 view 构造一个 InfoWindow, 此时只是利用该view生成一个Bitmap绘制在地图中，监听事件由自己实现。
                       view - 展示的 view
					   position - 显示的地理位置
					   yOffset - Y轴偏移量
                     */
                    mInfoWindow = new InfoWindow(button, latLng, -47);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                } 
                return true;
            }
        });
		
		mBaiduMap.setOnPolylineClickListener(new BaiduMap.OnPolylineClickListener() {
			@Override
			public boolean onPolylineClick(Polyline polyline) {
				if (polyline.getZIndex() == mPolyline.getZIndex()) {
					Toast.makeText(getApplicationContext(),"点数：" + polyline.getPoints().size() + ",width:" + polyline.getWidth(), Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
		OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(latLngs);
		mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
		mPolyline.setZIndex(3);
	}
	
	/**
	 * 讲google地图的wgs84坐标转化为百度地图坐标
	 */
	private void  coordinateConvert(){
		CoordinateConverter converter  = new CoordinateConverter();  
		converter.from(CoordType.COMMON);  
		double lanSum = 0;
		double lonSum = 0;
		for (int i = 0; i < Const.googleWGS84.length; i++) {
			String[] ll = Const.googleWGS84[i].split(",");
			LatLng sourceLatLng = new LatLng(Double.valueOf(ll[0]), Double.valueOf(ll[1]));
			converter.coord(sourceLatLng);  
			LatLng desLatLng = converter.convert();  
			latLngs.add(desLatLng);
			lanSum += desLatLng.latitude;
			lonSum += desLatLng.longitude;
		}
		target = new LatLng(lanSum/latLngs.size(), lonSum/latLngs.size());
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
		// 为系统的方向传感器注册监听器
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.getMap().clear();
		mMapView.onDestroy();
		mMapView = null;
		startBD.recycle();
        finishBD.recycle();
		super.onDestroy();
	}

}
