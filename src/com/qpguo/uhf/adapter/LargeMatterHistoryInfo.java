package com.qpguo.uhf.adapter;

import java.util.List;

import com.example.uhf.application.R;
import com.qpguo.uhf.model.LargeMatterModel;
import com.qpguo.uhf.modelDAO.LargeMatterDAO;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LargeMatterHistoryInfo extends BaseAdapter
{
	private String TAG = "LargeMatterHistoryInfo";
	private String LargeMatterId;//根据此LargeMatterId返回一个列表
	private List<LargeMatterModel> data;
	private Context context;
	
	public LargeMatterHistoryInfo(Context context,String LargeMatterId)
	{
		this.context = context;
		this.LargeMatterId = LargeMatterId;
		this.data = this.getDisplayData();
		Log.i(TAG, "大件历史操作数据:");
		for(LargeMatterModel l :data)
		{
			Log.i(TAG, l.toString());
		}
	}
	@Override
	public int getCount() 
	{
		return data.size();
	}
	@Override
	public Object getItem(int position) 
	{
		return data.get(position);
	}
	@Override
	public long getItemId(int position) 
	{
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LargeMatterListHolder holder;
		if(convertView==null)
		{
			convertView = View.inflate(context, R.layout.largematterhistoryinfo, null);
			holder = new LargeMatterListHolder();
			holder.people=(TextView) convertView.findViewById(R.id.historyList_people);
			holder.time=(TextView) convertView.findViewById(R.id.historyList_time);
			holder.opetype=(TextView) convertView.findViewById(R.id.historyList_type);
			convertView.setTag(holder);
		}
		else
		{
			holder = (LargeMatterListHolder) convertView.getTag();
		}
		//设置显示内容
		holder.people.setText(data.get(position).getLoginId());
		holder.time.setText(data.get(position).getExcuteTime());
		holder.opetype.setText(data.get(position).getOpeType());
		return convertView;
	}
	
	/**
	 * 此方法用于根据LargeMatterId查询LargeMatter表获取大件历史操作信息
	 * @return
	 */
	private  List<LargeMatterModel> getDisplayData()
	{
		LargeMatterDAO lmd = new LargeMatterDAO(context);
		return lmd.findHistoryInfoByLargeMatterId(LargeMatterId);
	}
	




}

class LargeMatterListHolder
{
	TextView people;
	TextView time;
	TextView opetype;
}