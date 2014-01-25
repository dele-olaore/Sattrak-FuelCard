package com.dexter.fmr.mbean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.faces.context.FacesContext;

import jxl.Cell;
import jxl.Workbook;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.dexter.fmr.dao.AuditDAO;
import com.dexter.fmr.model.AuditTrail;
import com.sun.faces.context.FacesContextImpl;

@Name("downloadMBean")
@Scope(ScopeType.SESSION)
@AutoCreate
public class DownloadMBean implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	// could be byte[], File or InputStream
	public byte[] getCsvData()
	{
		// generate data to be downloaded
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		String name = "logs";
		/*try
		{
			name = (String)curContext.getExternalContext().getRequestMap().get("downloadType");
		}
		catch(Exception ex)
		{}*/
		if(name != null && name.equals("logs"))
		{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try
			{
				WritableWorkbook wbk = Workbook.createWorkbook(bout);
				
				WritableSheet sheet = wbk.createSheet("Report Data", 0);
				
				Vector<AuditTrail> audits = new AuditDAO().getAudits();
				int row = 0;
				for(AuditTrail e : audits)
				{
					try
					{
						sheet.addCell(new Label(0, row, e.getUsername()));
						sheet.addCell(new DateTime(1, row, e.getAuditTime()));
						sheet.addCell(new Label(2, row, e.getActionPerformed()));
						sheet.addCell(new Label(3, row, e.isSuccess() ? "Yes" : "No"));
					}
					catch(Exception ex)
					{}
					row+=1;
				}
				
				wbk.write();
				System.out.println("Download Byte Lenght: " + bout.size());
				return bout.toByteArray();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		else
		{
			return null;
		}
	}

	public String getCsvFileName()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		String name = "logs";
		/*try
		{
			name = (String)curContext.getExternalContext().getRequestMap().get("downloadType");
		}
		catch(Exception ex)
		{}*/
		
		return name+".csv";
	}
}
