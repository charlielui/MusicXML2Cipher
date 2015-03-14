package musicxmlconverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Note implements Comparable<Note>
{
    public final static Map<String, Integer> TYPE_TO_NUM_OF_BEAMS;
    static
    {
	Map<String, Integer> aMap = new HashMap<String, Integer>();
	aMap.put("whole", -4); // no beam
	aMap.put("half", -1); // no beam
	aMap.put("quarter", 0);
	aMap.put("eighth", 1);
	aMap.put("16th", 2);
	aMap.put("32nd", 3);
	aMap.put("64th", 4);
	aMap.put("128th", 5);
	aMap.put("256th", 6);
	TYPE_TO_NUM_OF_BEAMS = Collections.unmodifiableMap(aMap);
    }
    public final static Map<String, Integer> NAME2CHROMATIC;
    static
    {
	Map<String, Integer> aMap = new HashMap<String, Integer>();
	aMap.put("C", 0);
	aMap.put("C#", 1);
	aMap.put("D", 2);
	aMap.put("Eb", 3);
	aMap.put("E", 4);
	aMap.put("F", 5);
	aMap.put("F#", 6);
	aMap.put("Gb", 6);
	aMap.put("G", 7);
	aMap.put("G#", 8);
	aMap.put("A", 9);
	aMap.put("Bb", 10);
	aMap.put("B", 11);
	NAME2CHROMATIC = Collections.unmodifiableMap(aMap);
    }
    static final int[] CIPHER2CHROMATIC = { 0, 2, 4, 5, 7, 9, 11 };

    public final static Map<String, Integer> STEP2INTERVAL;
    static
    {
	Map<String, Integer> aMap = new HashMap<String, Integer>();
	/*
	 * aMap.put("A", 1); aMap.put("B", 2); aMap.put("C", 3); aMap.put("D",
	 * 4); aMap.put("E", 5); aMap.put("F", 6); aMap.put("G", 7);
	 */
	aMap.put("C", 1);
	aMap.put("D", 2);
	aMap.put("E", 3);
	aMap.put("F", 4);
	aMap.put("G", 5);
	aMap.put("A", 6);
	aMap.put("B", 7);
	STEP2INTERVAL = Collections.unmodifiableMap(aMap);
    }

    private float x;
    private float onPageX;
    private float onPageY;
    private char step;
    private int alter;
    private int octave;
    private int type;// 1=whole,2=half,4=quarter
    private boolean isChord;
    private boolean dotted;
    private boolean isRest;
    private boolean isGraceNote;
    private boolean isTieStart;
    private boolean isTieEnd;
    private boolean isDanglingTie;
    private boolean isBottomNote;
    private int slur;
    private int[] beams;

    // tuplet?

    public Note(float x)
    {
	this.x = x;
	onPageX = -1.0f;
	onPageY = -1.0f;
	step = '0';
	alter = 0;
	octave = 0;
	type = -1000;
	isTieStart = false;
	isTieEnd = false;
	isDanglingTie = false;
	isBottomNote = true;
	slur = Constants.SLUR_NONE;
	isChord = false;
	dotted = false;
	beams = new int[] { Constants.BEAM_NONE, Constants.BEAM_NONE, Constants.BEAM_NONE, Constants.BEAM_NONE, Constants.BEAM_NONE, Constants.BEAM_NONE };
    }

    public boolean samePitchAs(Note otherNote)
    {
	//if (otherNote.getStep() == step && otherNote.getAlter() == alter)
	if (otherNote.getStep() == step)
	{
	    return true;
	}
	return false;
    }

    public float getX()
    {
	return x;
    }

    public void setOnPagePosition(float x, float y)
    {
	onPageX = x;
	onPageY = y;
    }

    public float getOnPageX()
    {
	return onPageX;
    }

    public float getOnPageY()
    {
	return onPageY;
    }

    public boolean isFullBarRest()
    {
	return (type == -1000);
    }

    public void setStep(char step)
    {
	this.step = step;
    }

    public char getStep()
    {
	return step;
    }

    public void setIsChord(boolean chord)
    {
	isChord = chord;
    }

    public boolean isChord()
    {
	return isChord;
    }

    public void setAlter(int alter)
    {
	this.alter = alter;
    }

    public int getAlter()
    {
	return alter;
    }

    public void setOctave(int octave)
    {
	this.octave = octave;
    }

    public int getOctave()
    {
	return octave;
    }

    public void setIsRest(boolean rest)
    {
	this.isRest = rest;
    }

    public boolean isRest()
    {
	return isRest;
    }

    public void setType(String typename)
    {
	this.type = TYPE_TO_NUM_OF_BEAMS.get(typename);
    }

    public int getType()
    {
	return type;
    }

    public void setDotted(Boolean dotted)
    {
	this.dotted = dotted;
    }

    public boolean isDotted()
    {
	return dotted;
    }

    public void setGraceNote(boolean b)
    {
	this.isGraceNote = b;
	if (b)
	{
	    isTieStart = false;
	    isTieEnd = false;
	}
    }

    public boolean isGraceNote()
    {
	return isGraceNote;
    }

    public void setTieStart(boolean tieStart)
    {
	if (!isGraceNote)
	{
	    isTieStart = tieStart;
	}
    }

    public void setTieEnd(boolean tieEnd)
    {
	if (!isGraceNote)
	{
	    isTieEnd = tieEnd;
	}
    }
    public void setDanglingTie()
    {
	isDanglingTie = true;
    }

    public boolean isTieStart()
    {
	return isTieStart;
    }

    public boolean isTieEnd()
    {
	return isTieEnd;
    }
    public boolean isDanglingTie()
    {
	return isDanglingTie;
    }

    public void setIsBottomNote(boolean bottomNote)
    {
	isBottomNote = bottomNote;
    }
    public boolean isBottomNote()
    {
	return isBottomNote;
    }
    public void setBeam(int beam, int beamNum)
    {
	beams[beamNum] = beam;
    }

    public int getBeam(int beamNum)
    {
	return beams[beamNum];
    }

    public void setSlur(String value)
    {
	if (value.equalsIgnoreCase("start"))
	{
	    slur = Constants.SLUR_START;
	}
	else if (value.equalsIgnoreCase("stop"))
	{
	    slur = Constants.SLUR_STOP;
	}
    }

    public int getSlur()
    {
	return slur;
    }

    public int getCipherOctave(char key, int octaveOffset)
    {
	// TODO: figure out octave formula
	// return ... -2,-1,0,1,2, ...
	if (isRest)
	{
	    return 0;
	}

	if (STEP2INTERVAL.get(Character.toString(step)) < STEP2INTERVAL.get(Character.toString(key)))
	{
	    return octave - 5 + octaveOffset;
	}
	return octave - 4 + octaveOffset;
	// return (STEP2INTERVAL.get(Character.toString(step)) -
	// STEP2INTERVAL.get(Character.toString(key)) + 7) % 7 +1;
    }

    public int getCipher(char key)
    {
	return (STEP2INTERVAL.get(Character.toString(step)) - STEP2INTERVAL.get(Character.toString(key)) + 7) % 7 + 1;
    }

    public String getCipherAccidental(char key, int keyalter)
    {
	/*
	 * int stepChromaticNum = NAME2CHROMATIC.get(Character.toString(step)) +
	 * alter; int keyChromaticNum =
	 * NAME2CHROMATIC.get(Character.toString(key)) + keyalter; int
	 * noteChromaticNum = (stepChromaticNum -keyChromaticNum +12)%12;
	 */
	int noteChromaticNum = (NAME2CHROMATIC.get(Character.toString(step)) + alter - NAME2CHROMATIC.get(Character.toString(key)) - keyalter + 12) % 12;
	int chromaticDiff = noteChromaticNum - CIPHER2CHROMATIC[getCipher(key) - 1];
	// TODO: Natural sign!?
	String accidental;
	switch (chromaticDiff)
	{
	case -2:
	    accidental = "bb";
	    break;
	case -1:
	    accidental = "b";
	    break;
	case 1:
	    accidental = "B";
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

    public int getCipherAccidentalStep(char key, int keyalter)
    {
	int noteChromaticNum = (NAME2CHROMATIC.get(Character.toString(step)) + alter - NAME2CHROMATIC.get(Character.toString(key)) - keyalter + 12) % 12;
	return noteChromaticNum - CIPHER2CHROMATIC[getCipher(key) - 1];
    }

    public String getNote(char key, int keyalter)
    {
	String suffex = "";
	switch (type)
	{
	case -4:
	    suffex = (dotted ? " - - - - -" : " - - -");
	    break;
	case -1:
	    suffex = (dotted ? " - -" : " -");
	    break;
	default:
	    suffex = (dotted ? "." : "");
	    break;
	}
	if (isRest)
	{
	    if (type == -1000)// no note type specified
	    {
		// return ") 0 ("; //now handled in xmlcipher.java
		return "";
	    }
	    return "0" + suffex;
	}
	else
	{
	    // return getCipherAccidental(key, keyalter) + getCipher(key) +
	    // suffex;
	    return getCipher(key) + suffex;
	}
    }

    public int compareTo(Note oNote)
    {
	final int BEFORE = -1;
	final int EQUAL = 0;
	final int AFTER = 1;
	if (this.getX() < oNote.getX())
	{
	    return BEFORE;
	}
	else if (this.getX() > oNote.getX())
	{
	    return AFTER;
	}
	else //same X, sort by pitch
	{
	    if (this.getOctave() < oNote.getOctave())
	    {
		return BEFORE;
	    }
	    else if (this.getOctave() > oNote.getOctave())
	    {
		return AFTER;
	    }
	    else //same Octave, sort y step
	    {
		if(this.getStep() < oNote.getStep())   
		{
		    return BEFORE;
		}
		else if (this.getStep() > oNote.getStep())
		{
		    return AFTER;
		}
		else // same step... check accidentals?
		{
		    if(this.getAlter() < oNote.getAlter())   
		    {
			return BEFORE;
		    }
		    else if (this.getAlter() > oNote.getAlter())
		    {
			return AFTER;
		    }
		    else
		    {
			return EQUAL;
		    }
		}
	    }
	}
    }

}
