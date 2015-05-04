package com.dexter.fuelcard.mbean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import com.dexter.fuelcard.dao.GeneralDAO;
import com.dexter.fuelcard.model.BankRecord;
import com.dexter.fuelcard.model.Car;
import com.dexter.fuelcard.model.Invoice;
import com.dexter.fuelcard.model.Partner;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.faces.context.FacesContextImpl;

@ManagedBean(name = "utilMBean")
@SessionScoped
public class UtilMBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Long partner_id;
	
	private int year;
	private int month;
	
	private Vector<Partner> partners;
	private Vector<Invoice> unpaidInvoices;
	
	@ManagedProperty(value="#{userMBean}")
	private UserMBean userMBean;
	
	public UtilMBean()
	{}
	
	public void payInvoice(Long id)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		if(id != null && getUnpaidInvoices() != null && getUnpaidInvoices().size() > 0)
		{
			Invoice inv = null;
			for(Invoice e : getUnpaidInvoices())
			{
				if(e.getId().longValue() == id.longValue())
				{
					inv = e;
					break;
				}
			}
			
			if(inv != null)
			{
				inv.setPaid(true);
				inv.setPaid_dt(new Date());
				
				GeneralDAO gDAO = new GeneralDAO();
				gDAO.startTransaction();
				if(gDAO.update(inv))
				{
					gDAO.commit();
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Invoice paid successfully!"));
					
					setUnpaidInvoices(null);
				}
				else
				{
					gDAO.rollback();
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", gDAO.getMessage()));
				}
				gDAO.destroy();
			}
			else
			{
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Invoice not found!"));
			}
		}
		else
		{
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Invalid invoice selected!"));
		}
	}
	
	public void downloadInvoice(Long id)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		if(id != null && getUnpaidInvoices() != null && getUnpaidInvoices().size() > 0)
		{
			Invoice inv = null;
			for(Invoice e : getUnpaidInvoices())
			{
				if(e.getId().longValue() == id.longValue())
				{
					inv = e;
					break;
				}
			}
			
			if(inv != null)
			{
				String fileName = inv.getInvoice_no() + ".pdf";
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				try {
					bout.write(inv.getDocument());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				writeFileToResponse(context.getExternalContext(), bout, fileName, "application/pdf");
				
				context.responseComplete();
			}
			else
			{
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Invoice not found!"));
			}
		}
		else
		{
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Invalid invoice selected!"));
		}
	}
	
	public void generateInvoice()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		if(getPartner_id() != null && getYear() > 0 && getMonth() > 0)
		{
			Invoice invoice = null;
			
			GeneralDAO gDAO = new GeneralDAO();
			
			Object partnerObj = gDAO.find(Partner.class, getPartner_id());
			if(partnerObj == null)
			{
				gDAO.destroy();
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Invalid partner!"));
				return;
			}
			Partner partner = (Partner)partnerObj;
			
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("partner.id", getPartner_id().longValue());
			params.put("month", getMonth());
			params.put("year", getYear());
			
			Object foundObj = gDAO.search("Invoice", params);
			if(foundObj != null)
			{
				@SuppressWarnings("unchecked")
				Vector<Invoice> foundInvoiceList = (Vector<Invoice>)foundObj;
				for(Invoice e : foundInvoiceList)
					invoice = e;
			}
			
			if(invoice != null)
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Invoice already exists for partner for selected date!"));
			}
			else
			{
				invoice = new Invoice();
				params = new Hashtable<String, Object>();
				params.put("partner.id", getPartner_id().longValue());
				foundObj = gDAO.search("Invoice", params);
				if(foundObj != null)
				{
					@SuppressWarnings("unchecked")
					Vector<Invoice> foundInvoiceList = (Vector<Invoice>)foundObj;
					invoice.setInvoice_no("INV-" + partner.getCode() + "-" + (foundInvoiceList.size()+1));
				}
				else
					invoice.setInvoice_no("INV-" + partner.getCode() + "-1");
				
				invoice.setYear(getYear());
				invoice.setMonth(getMonth());
				invoice.setPartner(partner);
				invoice.setCrt_dt(new Date());
				invoice.setGenerated_dt(new Date());
				invoice.setPaid(false);
				invoice.setPaid_dt(null);
				invoice.setCreatedBy(userMBean.getActiveUser());
				
				BigDecimal amount = calcMonthAmountDue(partner, getMonth(), getYear());
				invoice.setAmount(amount);
				
				Calendar can = Calendar.getInstance();
				can.set(Calendar.MONTH, getMonth()-1);
				can.set(Calendar.YEAR, getYear());
				
				String formattedAmount = "";
				NumberFormat nf = NumberFormat.getCurrencyInstance();
				nf.setCurrency(Currency.getInstance("NGN"));
				formattedAmount = nf.format(amount.doubleValue());
				
				String formattedChargeAmount = nf.format(partner.getBillingUnitAmt());
				
				byte[] doc = createInvoicePDF(1, 
						"Fuel card Invoice for " + partner.getName(),
						can.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + ", " + getYear(), 
						formattedAmount, partner.getBillingType(), formattedChargeAmount, (invTotalCalc != null) ? invTotalCalc : "");
				invoice.setDocument(doc);
				
				gDAO.startTransaction();
				if(gDAO.save(invoice))
				{
					gDAO.commit();
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Invioce generated successfully! You can now download the invoice."));
					
					setPartner_id(null);
					setYear(0);
					setMonth(0);
					setUnpaidInvoices(null);
				}
				else
				{
					gDAO.rollback();
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", gDAO.getMessage()));
				}
			}
			gDAO.destroy();
		}
		else
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All fields with the '*' sign are required!"));
	}
	
	public byte[] createInvoicePDF(int pageType, String title, String dateperiod, String amount, String chargeType, String chargeAmount, String total)
	{
		try
		{
			Document document = new Document();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, baos);
			writer.setPageEvent(new HeaderFooter());
			writer.setBoxSize("footer", new Rectangle(36, 54, 559, 788));
			if (!document.isOpen())
			{
				document.open();
			}
			
			switch(pageType)
			{
			case 1:
				document.setPageSize(PageSize.A4);
				break;
			case 2:
				document.setPageSize(PageSize.A4.rotate());
				break;
			}
			document.addAuthor("FUELCARD");
			document.addCreationDate();
			document.addCreator("FUELCARD");
			document.addSubject("Invoice");
			document.addTitle(title);
			
			PdfPTable headerTable = new PdfPTable(1);
			
			ServletContext servletContext = (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
			String logo = servletContext.getRealPath("") + File.separator + "resources" + File.separator + "images" + File.separator + "satraklogo.jpg";
			
			PdfPCell c = new PdfPCell(Image.getInstance(logo));
			c.setBorder(0);
			c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			headerTable.addCell(c);
			
			Paragraph stars = new Paragraph(20);
			stars.add(Chunk.NEWLINE);
	        stars.setSpacingAfter(20);
			
			c = new PdfPCell(stars);
			c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			c.setBorder(0);
			headerTable.addCell(c);
			
			BaseFont helvetica = null;
			try
			{
				helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
			}
			catch (Exception e)
			{
				//font exception
			}
			Font font = new Font(helvetica, 16, Font.NORMAL|Font.BOLD);
			
			c = new PdfPCell(new Paragraph(title, font));
			c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			c.setBorder(0);
			headerTable.addCell(c);
			
			if(dateperiod != null)
			{
				stars = new Paragraph(20);
				stars.add(Chunk.NEWLINE);
		        stars.setSpacingAfter(10);
				
				c = new PdfPCell(stars);
				c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				c.setBorder(0);
				headerTable.addCell(c);
				
				new Font(helvetica, 12, Font.NORMAL);
				Paragraph ch = new Paragraph("For " + dateperiod, font);
				c = new PdfPCell(ch);
				c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				headerTable.addCell(c);
			}
			stars = new Paragraph(20);
			stars.add(Chunk.NEWLINE);
	        stars.setSpacingAfter(20);
			
			c = new PdfPCell(stars);
			c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			c.setBorder(0);
			headerTable.addCell(c);
			document.add(headerTable);
			
			PdfPTable pdfTable = new PdfPTable(4);
			try
			{
				helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
			}
			catch (Exception e)
			{}
			font = new Font(helvetica, 8, Font.BOLDITALIC);
			
			pdfTable.addCell(new Paragraph("Charge Type", font)); // % per transaction or flat per license
			pdfTable.addCell(new Paragraph("Charge Amount", font)); // the amount of the charge setting
			
			if(chargeType.equalsIgnoreCase("Percent-Per-Tran"))
			{
				pdfTable.addCell(new Paragraph("Total Amount", font)); // the amount of the charge setting
			}
			else
			{
				pdfTable.addCell(new Paragraph("Total License", font)); // the amount of the charge setting
			}
			//pdfTable.addCell(new Paragraph("Description", font)); // the 
			pdfTable.addCell(new Paragraph("Amount Due", font));
			
			font = new Font(helvetica, 8, Font.NORMAL);
			pdfTable.addCell(new Paragraph(chargeType, font));
			pdfTable.addCell(new Paragraph(chargeAmount, font));
			pdfTable.addCell(new Paragraph(total, font));
			pdfTable.addCell(new Paragraph(amount, font));
			
			pdfTable.setWidthPercentage(100);
			if(pdfTable != null)
				document.add(pdfTable);
			
			//Keep modifying your pdf file (add pages and more)
			
			document.close();
			
			return baos.toByteArray();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static class HeaderFooter extends PdfPageEventHelper
	{
		public void onEndPage(PdfWriter writer, Document document)
		{
			Rectangle rect = writer.getBoxSize("footer");
			BaseFont bf_times;
			try
			{
				bf_times = BaseFont.createFont(BaseFont.TIMES_ROMAN, "Cp1252", false);
				Font font = new Font(bf_times, 9);
				ColumnText.showTextAligned(writer.getDirectContent(),
					    Element.ALIGN_RIGHT, new Phrase(String.format("%d", writer.getPageNumber()), font),  rect.getRight(),
					    rect.getBottom() - 18, 0);
				ColumnText.showTextAligned(writer.getDirectContent(),
						Element.ALIGN_LEFT, new Phrase("Copyright " + Calendar.getInstance().get(Calendar.YEAR), font), 
						rect.getLeft(), rect.getBottom() - 18, 0);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String invTotalCalc = null;
	private BigDecimal calcMonthAmountDue(Partner partner, int month, int year)
	{
		invTotalCalc = null;
		BigDecimal amount = new BigDecimal("0.00");
		
		Calendar can = Calendar.getInstance();
		can.set(Calendar.MONTH, month-1);
		can.set(Calendar.YEAR, year);
		can.set(Calendar.DATE, can.getMinimum(Calendar.DATE));
		can.set(Calendar.HOUR_OF_DAY, can.getMinimum(Calendar.HOUR_OF_DAY));
		can.set(Calendar.MINUTE, can.getMinimum(Calendar.MINUTE));
		can.set(Calendar.SECOND, can.getMinimum(Calendar.SECOND));
		Date start = can.getTime();
		can.set(Calendar.DATE, can.getMaximum(Calendar.DATE));
		can.set(Calendar.HOUR_OF_DAY, can.getMaximum(Calendar.HOUR_OF_DAY));
		can.set(Calendar.MINUTE, can.getMaximum(Calendar.MINUTE));
		can.set(Calendar.SECOND, can.getMaximum(Calendar.SECOND));
		Date end = can.getTime();
		
		if(partner.getBillingType() != null && partner.getBillingType().equals("Percent-Per-Tran"))
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from BankRecord e where e.vehicle.partner = :partner and e.tranTime between :start and :end");
			q.setParameter("partner", partner);
			q.setParameter("start", start);
			q.setParameter("end", end);
			Object foundObjs = gDAO.search(q, 0);
			if(foundObjs != null)
			{
				BigDecimal total = new BigDecimal("0.00");
				@SuppressWarnings("unchecked")
				Vector<BankRecord> list = (Vector<BankRecord>)foundObjs;
				for(BankRecord e : list)
				{
					BigDecimal tamt = new BigDecimal(e.getTranAmt());
					tamt = tamt.setScale(2, RoundingMode.HALF_UP);
					total = total.add(tamt);
					total = total.setScale(2, RoundingMode.HALF_UP);
				}
				
				invTotalCalc = total.toPlainString();
				
				try
				{
					BigDecimal percent = new BigDecimal(partner.getBillingUnitAmt());
					percent = percent.setScale(2, RoundingMode.HALF_UP);
					percent = percent.divide(new BigDecimal("100.00"));
					percent = percent.setScale(2, RoundingMode.HALF_UP);
					
					amount = total.multiply(percent);
					amount = amount.setScale(2, RoundingMode.HALF_UP);
				}
				catch(Exception ex)
				{ ex.printStackTrace(); }
			}
			gDAO.destroy();
		}
		else if(partner.getBillingType() != null && partner.getBillingType().equals("Flat-Per-License"))
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from Car e where e.partner = :partner");
			q.setParameter("partner", partner);
			Object foundObjs = gDAO.search(q, 0);
			if(foundObjs != null)
			{
				@SuppressWarnings("unchecked")
				Vector<Car> list = (Vector<Car>)foundObjs;
				try
				{
					BigDecimal flat = new BigDecimal(partner.getBillingUnitAmt());
					flat = flat.setScale(2, RoundingMode.HALF_UP);
					BigDecimal count = new BigDecimal(list.size());
					count = count.setScale(2, RoundingMode.HALF_UP);
					
					invTotalCalc = count.toPlainString();
					
					amount = flat.multiply(count);
					amount = amount.setScale(2, RoundingMode.HALF_UP);
				}
				catch(Exception ex) { ex.printStackTrace(); }
			}
			gDAO.destroy();
		}
		
		return amount;
	}
	
	private void writeFileToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName, String mimeType)
	{
		try
		{
			externalContext.responseReset();
			externalContext.setResponseContentType(mimeType);
			externalContext.setResponseHeader("Expires", "0");
			externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			externalContext.setResponseHeader("Pragma", "public");
			externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName);
			externalContext.setResponseContentLength(baos.size());
			OutputStream out = externalContext.getResponseOutputStream();
			baos.writeTo(out);
			externalContext.responseFlushBuffer();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public Long getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(Long partner_id) {
		this.partner_id = partner_id;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public Vector<Partner> getPartners() {
		partners = userMBean.getPartners();
		return partners;
	}

	public void setPartners(Vector<Partner> partners) {
		this.partners = partners;
	}

	@SuppressWarnings("unchecked")
	public Vector<Invoice> getUnpaidInvoices() {
		if(unpaidInvoices == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("paid", false);
			Object foundObjs = gDAO.search("Invoice", params);
			if(foundObjs != null)
			{
				unpaidInvoices = (Vector<Invoice>)foundObjs;
			}
		}
		return unpaidInvoices;
	}

	public void setUnpaidInvoices(Vector<Invoice> unpaidInvoices) {
		this.unpaidInvoices = unpaidInvoices;
	}

	public UserMBean getUserMBean() {
		return userMBean;
	}

	public void setUserMBean(UserMBean userMBean) {
		this.userMBean = userMBean;
	}
	
}
