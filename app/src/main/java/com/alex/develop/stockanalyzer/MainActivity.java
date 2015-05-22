package com.alex.develop.stockanalyzer;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.alex.develop.fragment.StockFragment;

/**
 * App入口
 * @author Created by alex 2014/10/23
 */
public class MainActivity extends BaseActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changeThemeByTime();
		setContentView(R.layout.main_activity);
		go2StockView();
	}

	public void go2StockView() {
		FragmentTransaction transaction = getTransaction();
		transaction.replace(LAYOUT_CONTENT_ID, new StockFragment());
		transaction.commit();
	}

	private final int LAYOUT_CONTENT_ID = R.id.content;// 用于放置各种不同的Fragment
}