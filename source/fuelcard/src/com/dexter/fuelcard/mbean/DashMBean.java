package com.dexter.fuelcard.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.dexter.fuelcard.dao.ReportDAO;

@ManagedBean(name = "dashMBean")
@SessionScoped
public class DashMBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private CartesianChartModel vehMakesModel, vehTypesModel, vehTopFuelCompModel;
	
	@ManagedProperty(value="#{userMBean}")
	private UserMBean userMBean;
	
	public DashMBean()
	{}

	public UserMBean getUserMBean() {
		return userMBean;
	}

	public void setUserMBean(UserMBean userMBean) {
		this.userMBean = userMBean;
	}

	public CartesianChartModel getVehMakesModel() {
		if(vehMakesModel == null)
		{
			vehMakesModel = new CartesianChartModel();
			BarChartSeries vehMakeSeries = new BarChartSeries();
			vehMakeSeries.setLabel("Count");
			
			ArrayList<String[]> list = null;
			
			if(userMBean == null)
				System.out.println("User MBean is null");
			else if(userMBean.getActiveUser() == null)
				System.out.println("Active User in User MBean is null");
			else if(userMBean.getActiveUser().getPartner() == null)
				System.out.println("Partner in Active User in User MBean is null");
			
			if(userMBean.getActiveUser().getPartner().isSattrak())
				list = new ReportDAO().getTopVehicleMakes(null);
			else
				list = new ReportDAO().getTopVehicleMakes(userMBean.getActiveUser().getPartner().getId());
			if(list != null && list.size() > 0)
				for(int i=0; i<4; i++)
				{
					if(i+1 <= list.size())
					{
						String[] e = list.get(i);
						vehMakeSeries.set(e[0], Integer.parseInt(e[1]));
					}
					else
						break;
				}
			vehMakesModel.addSeries(vehMakeSeries);
		}
		return vehMakesModel;
	}

	public CartesianChartModel getVehTypesModel() {
		if(vehTypesModel == null)
		{
			vehTypesModel = new CartesianChartModel();
			BarChartSeries vehTypeSeries = new BarChartSeries();
			vehTypeSeries.setLabel("Count");
			
			ArrayList<String[]> list = null;
			if(userMBean.getActiveUser().getPartner().isSattrak())
				list = new ReportDAO().getTopVehicleTypes(null);
			else
				list = new ReportDAO().getTopVehicleTypes(userMBean.getActiveUser().getPartner().getId());
			if(list != null && list.size() > 0)
				for(int i=0; i<4; i++)
				{
					if(i+1 <= list.size())
					{
						String[] e = list.get(i);
						vehTypeSeries.set(e[0], Integer.parseInt(e[1]));
					}
					else
						break;
				}
			
			vehTypesModel.addSeries(vehTypeSeries);
		}
		return vehTypesModel;
	}

	public CartesianChartModel getVehTopFuelCompModel() {
		if(vehTopFuelCompModel == null)
		{
			vehTopFuelCompModel = new CartesianChartModel();
			ChartSeries cSeries = new ChartSeries();
			cSeries.setLabel("Total Litres Used");
			
			Calendar can = Calendar.getInstance();
			can.add(Calendar.DATE, -1);
			can.set(Calendar.HOUR_OF_DAY, 0);
			can.set(Calendar.MINUTE, 0);
			can.set(Calendar.SECOND, 0);
			
			Date d1 = can.getTime();
			
			can.set(Calendar.HOUR_OF_DAY, 23);
			can.set(Calendar.MINUTE, 59);
			can.set(Calendar.SECOND, 59);
			
			Date d2 = can.getTime();
			
			ArrayList<String[]> list = null;
			if(userMBean.getActiveUser().getPartner().isSattrak())
				list = new ReportDAO().highestFuelConsumption(d1, d2, null);
			else
				list = new ReportDAO().highestFuelConsumption(d1, d2, userMBean.getActiveUser().getPartner().getId());
			
			if(list != null && list.size() > 0)
				for(int i=0; i<10; i++)
				{
					if(i+1 <= list.size())
					{
						String[] e = list.get(i);
						cSeries.set(e[0], Double.parseDouble(e[1]));
					}
					else
						break;
				}
			
			vehTopFuelCompModel.addSeries(cSeries);
		}
		return vehTopFuelCompModel;
	}
}
