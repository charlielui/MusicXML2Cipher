package musicxmlconverter;

//import java.io.IOException;
import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;

public class Measure
{

    private int measure_num;
    private float measure_width;
    private float systemDistance;
    private boolean isNewPage;
    private boolean newSystem;
    // Attributes
    private boolean hasattribute;
    private char key;
    private int keyalter;
    private int beats;
    private int beattype;
    private char clef;
    private int barlineLeft;
    private int barlineRight;
    private boolean barlineLeftRepeat;
    private boolean barlineRightRepeat;    
    
    // Measure Style
    private boolean hasmeasurestyle;
    private int multiplerest;

    // List of notes and rests
    List<Note> noteList;
    List<Words> wordsList;


    public Measure(int num, float width)
    {

	measure_num = num;
	measure_width = width;
	systemDistance = 0.0f;
	hasattribute = false;
	key = '0';
	keyalter = 0;
	beats = 0;
	beattype = 0;
	clef = '0';
	hasmeasurestyle = false;
	multiplerest = 0;
	barlineLeft = Constants.BARLINE_NONE;
	barlineRight = Constants.BARLINE_NONE;
	barlineLeftRepeat = false;
	barlineRightRepeat = false;
	noteList = new ArrayList<Note>();
	wordsList = new ArrayList<Words>();
    }

    public int getMeasureNum()
    {
	return measure_num;
    }

    public void setMeasureNum(int num)
    {
	measure_num = num;
    }

    public Boolean getIsNewPage()
    {
	return isNewPage;
    }

    public void setIsNewPage(Boolean newpage)
    {
	isNewPage = newpage;
	newSystem = true;
    }

    public float getMeasureWidth()
    {
	return measure_width;
    }

    public void setMeasureWidth(int width)
    {
	measure_width = width;
    }

    public float getSystemDistance()
    {
	return systemDistance;
    }

    public void setSystemDistance(float distance)
    {
	systemDistance = distance;
    }

    public boolean newSystem()
    {
	return newSystem;
    }

    public void setNewSystem()
    {
	newSystem = true;
    }

    public boolean hasAttribute()
    {
	return hasattribute;
    }

    public char getKey()
    {
	return key;
    }

    public int getKeyAlter()
    {
	return keyalter;
    }

    public void setKeySignature(char key, int keyalter)
    {
	hasattribute = true;
	this.key = key;
	this.keyalter = keyalter;
    }

    public String getKeySignature()
    {
	String accidental;
	switch (keyalter)
	{
	case -2:
	    accidental = "bb";
	    break;
	case -1:
	    accidental = "b";
	    break;
	case 1:
	    accidental = "#";
	    break;
	case 2:
	    accidental = "x";
	    break;
	default:
	    accidental = "";
	    break;
	}
	return "1=" + key + accidental;
    }

    public String getKeyAccidental()
    {
	String accidental;
	switch (keyalter)
	{
	case -2:
	    accidental = new String("\u266d"+"\u266d"+"bb");
	    break;
	case -1:
	    accidental = new String("\u266d"+"b");
	    break;
	case 1:
	    accidental = new String("\u266F"+"B");
	    break;
	case 2:
	    accidental = "x";
	    break;
	default:
	    accidental = "";
	    break;
	}
	return accidental;
    }

    public int getBeats()
    {
	return beats;
    }

    public int getBeatType()
    {
	return beattype;
    }

    public void setBeats(int beats)
    {
	hasattribute = true;
	this.beats = beats;
    }

    public void setBeatType(int beattype)
    {
	hasattribute = true;
	this.beattype = beattype;
    }

    public char getClef()
    {
	return clef;
    }

    public void setClef(char clef)
    {
	hasattribute = true;
	this.clef = clef;
    }

    public boolean hasMeasureStyle()
    {
	return hasmeasurestyle;
    }

    public int getMultipleRest()
    {
	return multiplerest;
    }

    public void setMultipleRest(int multirest)
    {
	if (multirest != 0)
	{
	    hasmeasurestyle = true;
	    multiplerest = multirest;
	}
    }

    public void resetBarlines()
    {
	if (barlineLeft ==0)
	{
	    barlineLeft = Constants.BARLINE_NONE;
	}
	if (barlineRight == 0)
	{
	    barlineRight = Constants.BARLINE_NONE;
	}
    }
    
    public void setBarline(int barline)
    {
	if (barline == Constants.BARLINE_LEFT)
	{
	    barlineLeft = 0;
	}
	else if (barline == Constants.BARLINE_RIGHT)
	{
	    barlineRight = 0;
	}
    }
    public void setBarlineStyle(int style)
    {
	if (barlineLeft == 0)
	{
	    barlineLeft = style;
	}
	else if (barlineRight == 0)
	{
	    barlineRight = style;
	}
    }
    public int getBarlineStyle(int barline)
    {
	if (barline == Constants.BARLINE_LEFT)
	{
	    return barlineLeft;
	}
	else
	{
	    return barlineRight;
	}
    }
    public void setBarlineRepeat(String repeatDir)
    {
	if (repeatDir.equalsIgnoreCase("forward"))
	{
	    barlineLeftRepeat = true;
	}
	else if (repeatDir.equalsIgnoreCase("backward"))
	{
	    barlineRightRepeat = true;
	}
    }
    public boolean isBarlineRepeat(int barline)
    {
	if (barline == Constants.BARLINE_LEFT)
	{
	    return barlineLeftRepeat;
	}
	else
	{
	    return barlineRightRepeat;
	}
    }
    public void addNote(Note newNote)
    {
	noteList.add(newNote);
    }
    public void addWords(Words newWords)
    {
	wordsList.add(newWords);
    }
    /*
     * public Note getNextNote(){ return this.noteList.get(0); }
     */


}
