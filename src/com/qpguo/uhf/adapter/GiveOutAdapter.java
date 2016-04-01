package com.qpguo.uhf.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.uhf.application.R;
import com.qpguo.uhf.model.BaseDataModel;
import com.qpguo.uhf.model.LargeMatterGiveOutModel;
import com.qpguo.uhf.model.PositionModel;
import com.qpguo.uhf.modelDAO.BaseDataDAO;
import com.qpguo.uhf.modelDAO.LargeMatterGiveOutDAO;
import com.qpguo.uhf.modelDAO.PositionDataDAO;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class GiveOutAdapter extends BaseAdapter
{
	private String TAG = "GiveOutAdapter";
	private Context context;
	public  List<Map<String,Object>> data;
	public GiveOutAdapter(Context context)
	{
		super();
		this.context=context;
		this.data = this.getDataToDisplay();
		for(Map<String,Object> s:data)
		{
			Log.i(TAG, "data值："+s.toString());
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		// TODO Auto-generated method stub
		Holder holder;
		if(convertView==null)
		{
			convertView = View.inflate(context, R.layout.fakalist_item, null);
			holder = new Holder();
			holder.name=(TextView) convertView.findViewById(R.id.fakalistitem_name);
			holder.positionName=(TextView) convertView.findViewById(R.id.fakalistitem_positionName);
			holder.price=(TextView) convertView.findViewById(R.id.fakalistitem_price);
			holder.TheCount=(TextView) convertView.findViewById(R.id.fakalistitem_thecount);
			holder.type=(TextView) convertView.findViewById(R.id.fakalistitem_type);
			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}
		//设置显示内容
		Map<String,Object>map =(HashMap<String,Object>)this.getItem(position);
		holder.name.setText((String)map.get("name"));
		holder.positionName.setText((String)map.get("PositionName"));
		holder.price.setText((String)map.get("price"));
		holder.TheCount.setText(map.get("TheCount").toString());
		holder.type.setText((String)map.get("type"));
		return convertView;
	}
	
	
	
	
	 public  List<Map<String,Object>> getDataToDisplay()
	 {
		 PositionDataDAO pd = new PositionDataDAO(this.context);
		 BaseDataDAO bd = new BaseDataDAO(this.context);
		 List<PositionModel> list = pd.getNotGiveOutData();
		 List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		 for(PositionModel l:list)
		 {
			 Map<String,Object> map = new HashMap<String,Object>();
			 map.put("PositionName", l.getPositionName());
			 map.put("TheCount", l.getTheCount());
			 map.put("StorageId", l.getPositionCode());
			 map.put("MatterId", l.getMatterId());
			 //map.put("MatterId", l.getMatterId());
			 BaseDataModel bdm = bd.findItem(l.getMatterId());
			 map.put("name", bdm.getName());
			 map.put("type", bdm.getType());
			 map.put("price", bdm.getPrice());
			 data.add(map);
		 }
		 /*新增的大件的发卡数据*/
		 LargeMatterGiveOutDAO lmgod = new LargeMatterGiveOutDAO(context);
		 List<LargeMatterGiveOutModel> lst2 = lmgod.getLargeMatterGiveOutData(0);
		 for(LargeMatterGiveOutModel l:lst2)
		 {
			 Log.i(TAG, "找到大件需要发卡的数据:"+l.toString());
			 Map<String,Object> map = new HashMap<String,Object>();
			 /*以下两个字段用于大件发卡写标签*/
			 map.put("LargeMatterId", l.getLargeMatterId());
			 map.put("MatterId", l.getMatterId());
			 Log.i(TAG, "待发卡的大件信息:LargeMatterId:"+l.getLargeMatterId()
					 +"MatterId:"+l.getMatterId());
			 /*以下5个字段用于在列表中大件发卡信息的显示*/
			 map.put("PositionName", "大件");
			 BaseDataModel bdm = bd.findItem(l.getMatterId());
			 Log.i(TAG, "大件信息："+bdm);
			 map.put("name", bdm.getName());//大件名称
			 map.put("type", l.getLargeMatterId());//大件自增编号
			 map.put("price", "无");
			 map.put("TheCount", "1");
			 data.add(map);
		 }
		 return data;
	 }
	
}
class Holder
{
	TextView positionName;
	TextView name;
	TextView type;
	TextView price;
	TextView TheCount;
}


