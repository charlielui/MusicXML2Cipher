package musicxmlconverter;

//import com.pdfjet.CoreFont;
//import com.pdfjet.Font;
//import com.pdfjet.PDF;
//import com.pdfjet.Page;
//import com.pdfjet.TextAlign;
//import com.pdfjet.TextBox;
//import com.pdfjet.TextLine;

public class Words
{

//    private TextLine textLine;
//    private Font textBoxFont;
    private String wordText;
    private boolean italicizeFont;
    private boolean isRehearsalMark;
    private float fontSize;
    private float posRelativeX;
    private float posY;
    final static int fontHeight = 10;

    public Words() 
    {
	italicizeFont = false;
	isRehearsalMark = false;
	posRelativeX = 5;
	posY = 10;
	fontSize = 10;
    }

    public void setRelativeX(String strX)
    {
	if (strX == null)
	{
	    return;
	}
	posRelativeX = Float.parseFloat(strX);
    }
    public void setY(String strY)
    {
	if (strY == null)
	{
	    return;
	}
	posY = Float.parseFloat(strY);
    }
    public void setFontSize(String size)
    {
	if (size == null)
	{
	    return;
	}
	fontSize = Float.parseFloat(size);
    }

    public void setText(String text)
    {
	if (text == null)
	{
	    return;
	}
	wordText = text;
    }
    public float getRelativeX()
    {
	return posRelativeX;
    }
    public float getY()
    {
	return posY;
    }
    public float getFontSize()
    {
	return fontSize;
    }
    public String getText()
    {
	return wordText;
    }
    public void setItalicize(boolean b)
    {
	italicizeFont = b;
    }
    public boolean getItalicize()
    {
	return italicizeFont;
    }

    public void setIsRehearsalMark(boolean b)
    {
	isRehearsalMark = b;
	
    }
    public boolean getIsRehearsalMark()
    {
	return isRehearsalMark;
	
    }

}

