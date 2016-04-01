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
			//��ȡ��ͼ
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
		//��ȡ��ʾԭʼ����
		Map<String,Object> item =(HashMap<String,Object>) this.getItem(position);
		//��ȡӦ����ʾ������
		//MatterName:       �̹�ͷ ��P60 200mm ��
		String NameType =String.valueOf(item.get("MatterName"));
		String Name = NameType.split("��")[0];
		String Type = NameType.split("��")[1];
		String plan =PlanDataDAO.explainInOutCount((Integer)item.get("InOutCount"));
		holder.MatterNameText.setText(Name); 
		holder.MatterTypeText.setText(Type); 
		holder.MatterPlanText.setText(plan); 
		//��������δִ��������InOutCountһ��
		holder.NotExcutedText.setText(String.valueOf(Math.abs((Integer)item.get("NotExcutedNumber"))));
		return convertView;
	}
	
	public List<Map<String,Object>>getViewData()
	{
		//��ȡ��ǰ�û�
		LoadInfo info = new LoadInfo(this.context);
		PlanDataDAO pd =new PlanDataDAO(context,info.getUser());
		//��ȡδִ�еļƻ�
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
				//����ط�ֻ��������������ݣ���Ҫ�޸����ݿ�,δִ�е�����
				item.put("NotExcutedNumber", p.getNotExcutedNumbers());
				//���ڼƻ����ʱ����ִ�м�¼
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
