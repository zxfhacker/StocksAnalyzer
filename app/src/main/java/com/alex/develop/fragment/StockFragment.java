package com.alex.develop.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alex.develop.entity.Stock;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.util.StockDataAPIHelper;
import com.alex.develop.stockanalyzer.R;
import com.alex.develop.util.NetworkHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 15-5-22.
 * 自选股列表
 */
public class StockFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stock_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stocks = ((Analyzer) act.getApplication()).getStockList();
        loadView = (ImageView) act.findViewById(R.id.loading);

        final ListView stockList = (ListView) act.findViewById(R.id.stockList);
        stockListAdapter = new StockListAdapter();
        stockList.setAdapter(stockListAdapter);

        stockList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                act.go2CandleView(position);
            }
        });

        stockList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (SCROLL_STATE_IDLE == scrollState) {
                    if (queryStart < queryStop) {
                        new UpdateStockInfo().execute(queryStart, queryStop);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                queryStart = firstVisibleItem;
                queryStop = firstVisibleItem + visibleItemCount;

                if (flag && 0 < visibleItemCount) {
                    new UpdateStockInfo().execute(queryStart, queryStop);
                    flag = false;
                }
            }

            private boolean flag = true;
        });
    }

    private Stock[] queryStockList(int start, int end) {

        List<Stock> temp = new ArrayList<>();
        for(int i = start; i<end; ++i) {
            Stock stock = stocks.get(i);
            long stamp = System.currentTimeMillis();
            if(StockDataAPIHelper.SINA_REFRESH_INTERVAL < stamp - stock.getStamp()) {//5秒内不重复查询
                if(!stock.getTime().startsWith("15")) {// 15:00:00以后不重复查询
                    temp.add(stock);
                }
            }
        }

        return temp.toArray(new Stock[temp.size()]);
    }

    private int queryStart;
    private int queryStop;
    private List<Stock> stocks;// 自选股列表
    private ImageView loadView;// 加载
    private StockListAdapter stockListAdapter;
    private static  class ViewHolder {
        TextView stockName;
        TextView stockID;
        TextView stockClose;
        TextView stockIncrease;
    }
    private class StockListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return stocks.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(null == convertView) {
                LayoutInflater inflater = LayoutInflater.from(act);
                convertView = inflater.inflate(R.layout.stock_details, null);
                ViewHolder holder = new ViewHolder();
                holder.stockName = (TextView) convertView.findViewById(R.id.stockName);
                holder.stockID = (TextView) convertView.findViewById(R.id.stockId);
                holder.stockClose = (TextView) convertView.findViewById(R.id.stockClose);
                holder.stockIncrease = (TextView) convertView.findViewById(R.id.stockIncrease);
                convertView.setTag(holder);
            }

            Stock stock = stocks.get(position);
            ViewHolder holder = (ViewHolder) convertView.getTag();

            int textColor = act.getResources().getColor(R.color.stock_rise);
            float increase = stock.getToday().getIncrease();
            String price = stock.getToday().getCloseString();
            String increaseString = stock.getToday().getIncreaseString();

            if(0 > increase) {
                textColor = act.getResources().getColor(R.color.stock_fall);
            }
            if(stock.isSuspended()) {
                textColor = act.getResources().getColor(R.color.stock_suspended);
                price = stock.getToday().getLastCloseString();
                increaseString = act.getString(R.string.trade_suspended);
            }

            // 股票名称
            holder.stockName.setText(stock.getName());

            // 股票代码
            holder.stockID.setText(stock.getId());

            // 股票价格（收盘价）
            holder.stockClose.setText(price);
            holder.stockClose.setTextColor(textColor);

            // 股票涨幅
            holder.stockIncrease.setText(increaseString);
            holder.stockIncrease.setTextColor(textColor);

            return convertView;
        }
    }

    private class UpdateStockInfo extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Animation anim = AnimationUtils.loadAnimation(act, R.anim.loading_data);
            loadView.setVisibility(View.VISIBLE);
            loadView.startAnimation(anim);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            NetworkHelper.queryToday(queryStockList(params[0], params[1]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadView.clearAnimation();
            loadView.setVisibility(View.GONE);
            stockListAdapter.notifyDataSetChanged();
        }
    }
}
