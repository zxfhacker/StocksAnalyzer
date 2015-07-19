package com.alex.develop.task;

import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import com.alex.develop.entity.Stock;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.stockanalyzer.R;
import com.alex.develop.util.NetworkHelper;

/**
 * Created by alex on 15-7-17.
 * 查询一支或多支股票当日行情
 */

public class QueryStockToday extends AsyncTask<Stock, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Animation anim = AnimationUtils.loadAnimation(Analyzer.getContext(), R.anim.loading_data);
        Analyzer.getLoadView().setVisibility(View.VISIBLE);
        Analyzer.getLoadView().startAnimation(anim);
    }

    @Override
    protected Void doInBackground(Stock... params) {
        NetworkHelper.querySinaToday(params);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Analyzer.getLoadView().setVisibility(View.GONE);
        Analyzer.getLoadView().clearAnimation();
    }
}