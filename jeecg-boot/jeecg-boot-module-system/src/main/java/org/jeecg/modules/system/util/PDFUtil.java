package org.jeecg.modules.system.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFUtil {
	
	private Document document;
	private PdfWriter writer;
	
	public void setDocument(Document document) {
		this.document = document;
	}
	
	public void setWriter(PdfWriter writer) {
		this.writer = writer;
	}
	/**
	 * 开启创建PDF对象
	 * @param pafPath ： 生成pdf的磁盘路径
	 * @return
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public PDFUtil openDocumnet(String pafPath) throws FileNotFoundException, DocumentException{
		Document document = new Document(PageSize.A4.rotate());
		writer = PdfWriter.getInstance(document,new FileOutputStream(pafPath));
		document.open();
		this.document = document;
		return this;
	}
	/**
	 * 添加图片背景
	 * @param imageUrl ：证书图片的磁盘路径
	 * @param absoluteX ：左边距
	 * @param absoluteY ：底边距
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public PDFUtil addImage(String imagePath,float absoluteX, float absoluteY) throws MalformedURLException, IOException, DocumentException{
		Image tImgCover = Image.getInstance(imagePath);
		tImgCover.setAbsolutePosition(absoluteX, absoluteY);
		float heigth = tImgCover.getHeight();
		float width = tImgCover.getWidth();
		// int percent=getPercent(heigth, width);
		//int percent = getPercent2(heigth, width);
		// 设置图片居中显示
		// tImgCover.setAlignment(Image.MIDDLE);
		//System.out.println(percent);
		tImgCover.scalePercent(55);// 表示是原来图像的比例;
		document.add(tImgCover);
		return this;
	}
	/**
	 * 
	 * @param certificateContent :pdf证书的中文内容
	 * @param x ：左边距
	 * @param y ：底边距
	 * @param contentStyle ：中文内容的样式
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public PDFUtil addContent(String certificateContent,float x, float y,ContentStyle contentStyle) throws DocumentException, IOException{
		
		if(contentStyle == null){
			contentStyle = new ContentStyle();
		}
		
		PdfContentByte canvas = writer.getDirectContent();
		BaseFont bf = BaseFont.createFont(contentStyle.getTTFPath(),BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		Font secFont = new Font(bf, contentStyle.getFontSize(), contentStyle.getStyle(), contentStyle.getBaseColor());
		Phrase certificateContentPhrase = new Phrase(certificateContent, secFont);
		ColumnText.showTextAligned(canvas, contentStyle.getAlignment(), certificateContentPhrase, x,y, 0);
		return this;
	}
	/**
	 * 添加日期内容
	 * @param x 插入pdf左边距
	 * @param y 插入pdf底边距
	 * @param contentStyle
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public PDFUtil addDateContent(float x, float y,ContentStyle contentStyle) throws DocumentException, IOException{
		
		if(contentStyle == null){
			contentStyle = new ContentStyle();
		}
		
		Date currentDate = DateTimeUtil.getCurrentDate();
		String currentDateString = DateTimeUtil.DateToString(currentDate);
		
		PdfContentByte canvas = writer.getDirectContent();
		BaseFont bf = BaseFont.createFont(contentStyle.getTTFPath(),BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		Font secFont = new Font(bf, contentStyle.getFontSize(), contentStyle.getStyle(), contentStyle.getBaseColor());
		Phrase certificateDatephrase = new Phrase(currentDateString, secFont);
		ColumnText.showTextAligned(canvas,contentStyle.getAlignment(), certificateDatephrase, x,y, 0);
		return this;
	}
	/**
	 * 释放资源
	 */
	public void close(){
		document.close();
	}
	/**
	 * 第二种解决方案，统一按照宽度压缩
	 * 这样来的效果是，所有图片的宽度是相等的，自我认为给客户的效果是最好的
	 * @param args
	 */
	public int getPercent2(float h,float w)
	{
		int p=0;
		float p2=0.0f;
		p2=595/w*100;
		System.out.println("--"+p2);
		p=Math.round(p2);
		return p;
	}
	/**
	 * 第一种解决方案
	 * 在不改变图片形状的同时，判断，如果h>w，则按h压缩，否则在w>h或w=h的情况下，按宽度压缩
	 * @param h
	 * @param w
	 * @return
	 */
	
	public int getPercent(float h,float w)
	{
		int p=0;
		float p2=0.0f;
		if(h>w)
		{
			p2=297/h*100;
		}
		else
		{
			p2=210/w*100;
		}
		p=Math.round(p2);
		return p;
	}
	
	public static void main(String[] args) throws MalformedURLException, FileNotFoundException, DocumentException, IOException {
		long currentDateTime = new Date().getTime();
		String imagePath = PDFUtil.class.getClassLoader().getResource("certificate.png").getPath();
		String pdfFilePath = "d:/pdf/" +7+ ".pdf";
		PDFUtil pdfUtil = new PDFUtil();
		pdfUtil.openDocumnet(pdfFilePath)
				.addImage(imagePath, 0, 400)
				.addDateContent(330,462,null)
				.addContent("张三",85,655,null)
				.close();
	}
}
