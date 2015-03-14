package musicxmlconverter;

import com.pdfjet.Line;
import com.pdfjet.Page;
import com.pdfjet.Point;

public class Barline
{

    private float lineHeight;
    private Line barline;
    private Page page;
    private int barlineStyle;
    private int barlineRepeat;

    public Barline(Page page, float lineHeight, float lineWidth)
    {
	barline = new Line();
	barlineStyle = Constants.BARLINE_NONE;
	barlineRepeat = Constants.BARLINE_NONE;
	this.lineHeight = lineHeight;
	this.page = page;
	barline.setWidth(lineWidth);
    }

    public void setPage(Page page)
    {
	this.page = page;
    }

    public void setLine(float lineHeight, float lineWidth)
    {
	this.lineHeight = lineHeight;
	barline.setWidth(lineWidth);
    }

    public void setStyle(int style)
    {
	barlineStyle = style;
    }

    public int getStyle()
    {
	return barlineStyle;
    }

    public void setRepeat(int repeat)
    {
	barlineRepeat = repeat;
    }

    public int getRepeat()
    {
	return barlineRepeat;
    }

    public void draw(float posX, float posY) throws Exception
    {
	// System.out.println("x:"+posX+" y: "+posY);
	if (barlineStyle != Constants.BARLINE_NONE)
	{
	    if (barlineStyle == Constants.BARLINE_THICK_THIN)
	    {
		posX -= 2.2;
		barline.setWidth(1.2f);
	    }
	    else if (barlineStyle == Constants.BARLINE_THIN_THICK)
	    {
		posX += 2.2;
		barline.setWidth(1.2f);
	    }
	    else if (barlineStyle == Constants.BARLINE_THIN_THIN)
	    {
		posX += 2.2;
	    }
	    barline.setStartPoint(posX, posY - lineHeight * 0.75);
	    barline.setEndPoint(posX, posY + lineHeight * 0.25);
	    barline.drawOn(page);
	}
	else
	{
	    barline.setStartPoint(posX, posY - lineHeight * 0.75f);
	    barline.setEndPoint(posX, posY + lineHeight * 0.25f);
	    barline.drawOn(page);
	}

	// repeat dots
	if (barlineRepeat != Constants.BARLINE_NONE)
	{
	    float dotsX;
	    float dotsY = posY - lineHeight * 0.4f;
	    if (barlineRepeat == Constants.BARLINE_REPEAT_LEFT)
	    {
		dotsX = posX + 4.0f;
	    }
	    else
	    {
		dotsX = posX - 5.0f;
	    }
	    // firstdot
	    Point point = new Point(dotsX, dotsY);
	    point.setShape(Point.CIRCLE);
	    point.setFillShape(true);
	    point.setRadius(1.0f);
	    point.drawOn(page);
	    // second dot
	    dotsY = posY - lineHeight * 0.1f;
	    point.setPosition(dotsX, dotsY);
	    point.drawOn(page);

	}
    }
}
