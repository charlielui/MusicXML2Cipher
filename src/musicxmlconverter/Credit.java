package musicxmlconverter;

//import com.pdfjet.Align;
import com.pdfjet.CoreFont;
import com.pdfjet.Font;
import com.pdfjet.PDF;
import com.pdfjet.Page;
//import com.pdfjet.TextAlign;
//import com.pdfjet.TextBox;
import com.pdfjet.TextLine;

public class Credit
{

    private int pageNum;
    private TextLine textLine;
    private Font textBoxFont;
    private String hAlign;
    private float posX;
    private float posY;
    final static int fontHeight = 10;

    public Credit(PDF pdf) throws Exception
    {
	textBoxFont = new Font(pdf, CoreFont.TIMES_ROMAN);
	textLine = new TextLine(textBoxFont);
	// textLine.setNoBorders();
	// textLine.setWidth(200);
    }

    public Credit(PDF pdf, float x, float y) throws Exception
    {
	this(pdf);
	posX = x;
	posY = y+textLine.getHeight()/2;
	textLine.setPosition(posX, posY);
    }

    public Credit(PDF pdf, float x, float y, float fontSize) throws Exception
    {
	this(pdf, x, y);
	setFontSize(fontSize);
    }

    public void setFontSize(float size)
    {
	textBoxFont.setSize(size);
    }

    public Float getFontSize()
    {
	return (float) textLine.getFont().getSize();
    }

    public void setPageNum(int pageNum)
    {
	this.pageNum = pageNum;
    }

    public int getPageNum()
    {
	return pageNum;
    }

    public void setJustify(String strJustify)
    {
	hAlign = strJustify;
	/*
	 * if (strJustify.equalsIgnoreCase("right")) { //
	 * textLine.setTextAlignment(Align.RIGHT); } else if
	 * (strJustify.equalsIgnoreCase("center")) { //
	 * textLine.setTextAlignment(Align.CENTER); } else { //
	 * textLine.setTextAlignment(Align.LEFT); }
	 */
    }
    
    public void setHAlign(String HorizontalAlign)
    {
	hAlign = HorizontalAlign;
    }

    public void setVAlign(String strVAlign)
    {
	if (strVAlign.equalsIgnoreCase("top"))
	{
	    // textLine.setVerticalAlignment(TextAlign.TOP);
	}
	else if (strVAlign.equalsIgnoreCase("center"))
	{
	    // textLine.setVerticalAlignment(TextAlign.CENTER);
	}
	else
	{
	    // textLine.setVerticalAlignment(TextAlign.BOTTOM);
	}
    }

    public void setText(String text)
    {
	textLine.setText(text);
	// textLine.fitWidth();
	if (hAlign == null)
	{
	    return;
	}
	if (hAlign.equalsIgnoreCase("right"))
	{
	    textLine.setPosition(posX - textLine.getWidth(), posY);
	}
	else if (hAlign.equalsIgnoreCase("center"))
	{
	    textLine.setPosition(posX - textLine.getWidth() / 2, posY);
	}
	else 
	{
	    textLine.setPosition(posX , posY);
	}
	/*
	 * textLine.fitWidth(); if(textLine.getTextAlignment() == Align.RIGHT) {
	 * textLine.setPosition(textLine.getX()-textLine.getWidth(),
	 * textLine.getY()); } else if(textLine.getTextAlignment() ==
	 * Align.CENTER) { //
	 * textLine.setPosition(textLine.getX()-textLine.getWidth()*0.5,
	 * textLine.getY()); }
	 */
    }

    public void drawOn(Page page) throws Exception
    {
	// textBox.getFont().setSize(fontSize);
	textLine.drawOn(page);
    }

}
