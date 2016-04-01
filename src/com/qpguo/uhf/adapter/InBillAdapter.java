package com.qpguo.uhf.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.uhf.application.R;
import com.qpguo.uhf.model.PlanDataModel;
import com.qpguo.uhf.modelDAO.PlanDataDAO;
import com.qpguo.uhf.utils.LoadInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;




public class InBillAdapter extends BaseAdapter
{
	private List<Map<String,Object>> data;
	private Context context;
	
	public InBillAdapter(Context context)
	{
		super();
		this.context = context;
		this.data = this.getViewData();
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
		ListHolder holder;
		if(convertView==null)
		{
			//获取视图
			holder = new ListHolder();
			convertView = View.inflate(context, R.layout.inbillfunction_menu_list, null);
			holder.MatterNameText = (TextView) convertView.findViewById(R.id.listItem_matterName);
			holder.MatterTypeText = (TextView) convertView.findViewById(R.id.listItem_matterType);
			holder.MatterPlanText = (TextView) convertView.findViewById(R.id.listItem_matterPlan);
			holder.NotExcutedText = (TextView) convertView.findViewById(R.id.listItem_unExcutedNumber);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ListHolder)convertView.getTag();
		}
		//获取显示原始数据
		Map<String,Object> item =(HashMap<String,Object>) this.getItem(position);
		//获取应该显示的内容
		//MatterName:       短轨头 ；P60 200mm ；
		String NameType =String.valueOf(item.get("MatterName"));
		String Name = NameType.split("；")[0];
		String Type = NameType.split("；")[1];
		String plan =PlanDataDAO.explainInOutCount((Integer)item.get("InOutCount"));
		holder.MatterNameText.setText(Name); 
		holder.MatterTypeText.setText(Type); 
		holder.MatterPlanText.setText(plan); 
		//这里虚拟未执行数量与InOutCount一样
		holder.NotExcutedText.setText(String.valueOf(Math.abs((Integer)item.get("NotExcutedNumber"))));
		return convertView;
	}
	
	public List<Map<String,Object>>getViewData()
	{
		//获取当前用户
		LoadInfo info = new LoadInfo(this.context);
		PlanDataDAO pd =new PlanDataDAO(context,info.getUser());
		//获取未执行的计划
		List<PlanDataModel> displayList = pd.getExcutedInfoList(0);
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		if(!displayList.isEmpty())
		{
			for(PlanDataModel p:displayList)
			{
				Map<String,Object> item = new HashMap<String,Object>();
				item.put("MatterId", p.getMatterId());
				item.put("MatterName", p.getMatterName());
				item.put("InOutCount", p.getInOutCount());
				//这个地方只是虚拟出来的数据，需要修改数据库,未执行的数量
				item.put("NotExcutedNumber", p.getNotExcutedNumbers());
				//用于计划完成时生成执行记录
				item.put("InfoId", p.getInfoId());
				result.add(item);
			}
		}
		return result;	
	}
}

class ListHolder
{
	TextView MatterNameText;
	TextView MatterTypeText;
	TextView MatterPlanText;
	TextView NotExcutedText;
}
