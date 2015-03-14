package musicxmlconverter;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.pdfjet.*;

//import components.FileChooser;

public class xmlcipher extends DefaultHandler
{

    static final String[] fifthsToKeys = { "C", "G", "D", "A", "E", "B", "F", "C", "G", "D", "A", "E", "B", "F", "C" };
    static final int[] fifthsToAlter = { -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 1, 1 };
    static final float mmToPoint = 2.83464f; // (72/25.4), 1 inch is 25.4mm, and
					     // 1 point is 1/72 inches
    
    static final int MARGIN_OFFSET = 30;

    List<MusicPage> pageList;
    int pageNum;
    List<Measure> listMeasure;
    List<Credit> listCredit;
    List<Words> listWords;
    FileOutputStream fos;
    PDF pdf;

    private String tempVal;
    // private MusicPage tempPage;
    private Measure tempMeasure;
    private Note tempNote;
    private Barline barline;
    private Credit tempCredit;
    private Words tempWords;
    private Words tempRehearsal;
    private char currKey;
    private int currKeyAlter;
    private int octaveOffset;

    // layout
    private float leftMargin;
    private float rightMargin;
    private float topMargin;
    private float pageHeight;
    private float pageWidth;
    private float currBarX;
    private float currBarY;

    private float staffHeightMillimeters;
    private float tenthsPerStaff;
    private float tenthToPoint;

    // options
    private boolean slurDisabled;
    private boolean tieDisabled;

    // flags for parsing
    private boolean pagelayout;
    private int beamNum;

    private Font fontWords;
    private Font fontNote;
    private Font fontAccidentals;
    private Font fontMeasureNumber;
    
//    private FileChooser fileChooser;

    public xmlcipher(int octaveOffset, boolean slurDisabled) throws Exception
    {
	this.slurDisabled = slurDisabled;
	tieDisabled = false;

	listMeasure = new ArrayList<Measure>();
	listWords = new ArrayList<Words>();
	listCredit = new ArrayList<Credit>();
	pageList = new ArrayList<MusicPage>();
	currBarX = leftMargin = 70.0f;
	currBarY = topMargin = 70.0f;
	rightMargin = 70.0f;
	tenthToPoint = 0.0f;
	currKey = 'C';
	currKeyAlter = 0;
	pagelayout = false;
	beamNum = 0;
	this.octaveOffset = octaveOffset;

    }

    public void convertDoc(String input, String output)
    {
	try
	{
	    fos = new FileOutputStream(output);
	    pdf = new PDF(fos);
	    fontNote = new Font(pdf, CoreFont.COURIER);
	    fontWords = new Font(pdf, CoreFont.TIMES_ROMAN);
	    File fontFile = new File("MusiSync.ttf");
	    FileInputStream fis = new FileInputStream(fontFile);
	    BufferedInputStream bis = new BufferedInputStream(fis);
	    fontAccidentals = new Font(pdf, bis, CodePage.UNICODE, Embed.YES);
	    // fontAccidentals = new Font(pdf, CoreFont.COURIER);
	    fontAccidentals.setSize(18);

	    new Font(pdf, CoreFont.TIMES_ROMAN);
	    fontMeasureNumber = new Font(pdf, CoreFont.HELVETICA);
	    fontMeasureNumber.setSize(11);
	} catch (FileNotFoundException e)
	{
	    e.printStackTrace();
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	parseDocument(input);
	printData();
    }

    private void parseDocument(String input)
    {

	SAXParserFactory spf = SAXParserFactory.newInstance();
	try
	{
	    SAXParser parser = spf.newSAXParser();
	    File xmlFile = new File(input);
	    parser.parse(xmlFile, this);
	} catch (SAXException se)
	{
	    se.printStackTrace();
	} catch (ParserConfigurationException pce)
	{
	    pce.printStackTrace();
	} catch (IOException ie)
	{
	    ie.printStackTrace();
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	
    }

    private float convertUnit(float num)
    {
	// convert units (in Tenths) to inches
	return num * tenthToPoint;
    }

    private void printData()
    {
	try
	{

	    // Page page = new Page(pdf, Letter.PORTRAIT);
	    ArrayList<Page> pages = new ArrayList<Page>();

	    // Page page = new Page(pdf,new float[] {pageWidth,pageHeight});
	    // pages.add(page);
	    pages.add(new Page(pdf, new float[] { pageWidth, pageHeight }));
	    int pageIndex = 0;
	    Page page = pages.get(pageIndex);
	    // Font font2 = new Font(pdf, "AdobeMingStd-Light",
	    // CodePage.UNICODE);

	    // --- Init Page layout
	    currBarX = leftMargin;
	    currBarY = topMargin;
	    fontNote.setSize(12);

	    // init barline
	    float barlineH = convertUnit(tenthsPerStaff);
	    barline = new Barline(pages.get(pageIndex), barlineH, 1.0f);

	    // draw first bar barline
	    // barline.draw(currBarX, currBarY);

	    float noteLineL = 7.0f;
	    float noteLineW = 0.5f;

	    // Slur curve
	    Path slurPath = null;
	    float slurStartX = 0.0f;
	    Boolean slurMode = false;

	    // Ties
	    ArrayList<Note> StartedTieNotes = new ArrayList<Note>();

	    /*
	     * Iterator<MusicPage> mpageItr = pageList.iterator(); while
	     * (mpageItr.hasNext()) { mpageItr.next().printCredits(); }
	     */
	    // Draw texts (credits) for this page first
	    Iterator<Credit> creditItr = listCredit.iterator();
	    while (creditItr.hasNext())
	    {
		Credit credit = creditItr.next();
		if (credit.getPageNum() == (pageIndex + 1))
		{
		    credit.drawOn(page);
		}
	    }

	    // listMeasure.get(0).setNewSystem();
	    Iterator<Measure> measureItr = listMeasure.iterator();
	    while (measureItr.hasNext())
	    {
		Measure tempMeasure = measureItr.next();
		if (tempMeasure.getIsNewPage())
		{
		    // new page
		    pages.add(new Page(pdf, new float[] { pageWidth, pageHeight }));
		    pageIndex++;
		    page = pages.get(pageIndex);
		    barline.setPage(page);

		    currBarX = leftMargin;
		    currBarY = topMargin+MARGIN_OFFSET;

		    // Draw texts (credits) for this page first
		    creditItr = listCredit.iterator();
		    while (creditItr.hasNext())
		    {
			Credit credit = creditItr.next();
			if (credit.getPageNum() == (pageIndex + 1))
			{
			    credit.drawOn(page);
			}
		    }
		}

		if (tempMeasure.getMeasureWidth() == 0.0f)
		{
		    barline.draw(currBarX, currBarY);
		    continue;
		}
		if (tempMeasure.newSystem())
		{
		    // finish any slurs from last system
		    if (slurDisabled == false && slurMode == true)
		    {
			float slurWidth = currBarX - slurStartX;


			slurPath.add(new Point(slurStartX + slurWidth * 0.2, currBarY - barlineH - 8, Point.CONTROL_POINT));
			slurPath.add(new Point(slurStartX + slurWidth * 0.8, currBarY - barlineH - 8, Point.CONTROL_POINT));
			slurPath.add(new Point(slurStartX + slurWidth, currBarY - barlineH));
			slurPath.setWidth(noteLineW);
			slurPath.drawOn(page);
			slurStartX = leftMargin;
			slurPath = new Path();
			slurPath.add(new Point(slurStartX, currBarY + barlineH * 0.25 + tempMeasure.getSystemDistance()));
		    }

		    // handles ties
		    if (tieDisabled == false)
		    {
			// loop through the list of opened ties to find the note
			Iterator<Note> tiesItr = StartedTieNotes.iterator();
			while (tiesItr.hasNext())
			{
			    Note tempTieNote = tiesItr.next();
			    if (tempTieNote.isDanglingTie())
			    {
				tiesItr.remove();
				// StartedTieNotes.remove(tempTieNote);
				continue;
			    }
			    if (tempTieNote.isTieStart())
			    {
				// draw tie
				Path TiePath = new Path();
				float tieY = tempTieNote.getOnPageY() - 7;
				float tieStartX = tempTieNote.getOnPageX() + 7;
				float tieWidth = pageWidth - rightMargin - tieStartX;
				TiePath.add(new Point(tieStartX, tieY));
				TiePath.add(new Point(tieStartX + tieWidth * 0.2, tieY - 5, Point.CONTROL_POINT));
				TiePath.add(new Point(tieStartX + tieWidth * 0.8, tieY - 5, Point.CONTROL_POINT));
				TiePath.add(new Point(tieStartX + tieWidth, tieY - 5));
				TiePath.setWidth(noteLineW);
				TiePath.drawOn(page);

				tempTieNote.setOnPagePosition(leftMargin + 20, currBarY - 5 + barlineH * 0.25f + tempMeasure.getSystemDistance());
				tempTieNote.setDanglingTie();
			    }
			}

		    }

		    // --- New System ---
		    currBarX = leftMargin;
		    currBarY += barlineH + tempMeasure.getSystemDistance();
		    // bar number on new staff
		    TextLine barNum = new TextLine(fontMeasureNumber);
		    barNum.setText('[' + Integer.toString(tempMeasure.getMeasureNum()) + ']');
		    barNum.setPosition(currBarX - barNum.getWidth(), currBarY - barlineH +2);
		    barNum.drawOn(page);

		    // first barline on left
		    barline.draw(currBarX, currBarY);
		}
		if (tempMeasure.getBarlineStyle(Constants.BARLINE_LEFT) != Constants.BARLINE_NONE || tempMeasure.isBarlineRepeat(Constants.BARLINE_LEFT))
		{
		    Barline specialBarline = new Barline(pages.get(pageIndex), barlineH, 1.0f);
		    specialBarline.setStyle(tempMeasure.getBarlineStyle(Constants.BARLINE_LEFT));
		    if (tempMeasure.isBarlineRepeat(Constants.BARLINE_LEFT))
		    {
			specialBarline.setRepeat(Constants.BARLINE_REPEAT_LEFT);
		    }
		    specialBarline.draw(currBarX, currBarY);
		}

		// before notes
		if (tempMeasure.hasAttribute())
		{
		    // Time Signature
		    if (tempMeasure.getBeats() != 0 && tempMeasure.getBeatType() != 0)
		    {

			float posX = currBarX + 5;
			TextLine textTimeSig = new TextLine(fontNote);
			textTimeSig.setText(Integer.toString(tempMeasure.getBeats()));
			textTimeSig.setPosition(posX, currBarY - 7);
			textTimeSig.drawOn(page);
			Line tmpLine = new Line(posX, currBarY - 4, posX + noteLineL, currBarY - 5);
			tmpLine.drawOn(page);
			textTimeSig.setText(Integer.toString(tempMeasure.getBeatType()));
			textTimeSig.setPosition(posX, currBarY + 5);
			textTimeSig.drawOn(page);
		    }
		    // Key Signature
		    if (tempMeasure.getKey() != '0')
		    {
			currKey = tempMeasure.getKey();
			currKeyAlter = tempMeasure.getKeyAlter();
			TextLine keySig = new TextLine(fontNote);
			TextLine keySigAccidental = new TextLine(fontAccidentals);

			float keySigX = currBarX;
			if (tempMeasure.newSystem())
			{
			    keySigX += 15;
			}
			// keySig.setText(tempMeasure.getKeySignature());
			keySig.setText("1=" + Character.toString(tempMeasure.getKey()));
			keySig.setPosition(keySigX, currBarY - barlineH);
			keySig.drawOn(page);

			keySigAccidental.setText(tempMeasure.getKeyAccidental());
			keySigAccidental.setPosition(keySigX + keySig.getWidth() - 3, currBarY - barlineH - keySig.getHeight() * 0.5);
			// keySigAccidental.setTextEffect(Effect.SUPERSCRIPT);
			keySigAccidental.drawOn(page);
		    }
		}/*
		if (tempMeasure.getMultipleRest() > 0)
		{
		    // --- Multiple Rests ---
		    TextBox textBox = new TextBox(fontNote);
		    textBox.setWidth(tempMeasure.getMeasureWidth());
		    textBox.setText("|— " + tempMeasure.getMultipleRest() + " —|");
		    textBox.setTextAlignment(Align.CENTER);
		    textBox.setNoBorders();
		    textBox.setVerticalAlignment(TextAlign.BOTTOM);
		    textBox.setPosition(currBarX, currBarY + 3);
		    textBox.drawOn(page);

		    // TODO (This is duplicate) draw the end barline
		    // (duplicate!!!!)
		    currBarX += tempMeasure.getMeasureWidth();
		    barline.draw(currBarX, currBarY);

//		    continue;// don't draw any notes
		}*/
		
		// --- Draw Words
		Iterator<Words> wordsItr = tempMeasure.wordsList.iterator();

		while (wordsItr.hasNext())
		{
		    Words tempWords = wordsItr.next();
//		    float posX = currBarX + convertUnit(tempWords.getRelativeX());
		    float sizeFont = tempWords.getFontSize() * 0.75f;
		    float posX = currBarX + convertUnit( tempWords.getRelativeX());
		    float posY = currBarY  -sizeFont - convertUnit(tempWords.getY());
		    fontWords.setSize(sizeFont);
		    if (tempWords.getIsRehearsalMark())
		    {
//			posX = currBarX + ( tempWords.getRelativeX()*5);
			posX = currBarX ;
			TextBox rehearsalBox = new TextBox(fontWords,tempWords.getText(),sizeFont+4,sizeFont);
			rehearsalBox.fitBox();
			rehearsalBox.setWidth(rehearsalBox.getWidth()+8);
			rehearsalBox.setTextAlignment(Align.CENTER);
			rehearsalBox.setVerticalAlignment(TextAlign.CENTER);
//			Box rehearsalBox = new Box(posX, posY, sizeFont,sizeFont);
//			rehearsalBox.setLineWidth(0.1);
			posY = currBarY - convertUnit(tempWords.getY()) - rehearsalBox.getHeight();
			rehearsalBox.setPosition(posX, posY);
			rehearsalBox.drawOn(page);// TODO 
//			posX += sizeFont/4;
//			posY += sizeFont*0.75;
//			textWords.placeIn(rehearsalBox);
//			textWords.setLocation(arg0, arg1)
		    }
		    else
		    {
    		    	if (tempWords.getItalicize())
    		    	{
    		    	    fontWords.setItalic(true);
    		    	}
    		    	TextLine textWords = new TextLine(fontWords);
    		    	textWords.setText(tempWords.getText());
			textWords.setPosition(posX, posY);
			textWords.drawOn(page);
			fontWords.setItalic(false);
		    }
		}

		if (tempMeasure.getMultipleRest() > 0)
		{
		    // --- Multiple Rests ---
		    TextBox textBox = new TextBox(fontNote);
		    textBox.setWidth(tempMeasure.getMeasureWidth());
		    textBox.setText("|— " + tempMeasure.getMultipleRest() + " —|");
		    textBox.setTextAlignment(Align.CENTER);
		    textBox.setNoBorders();
		    textBox.setVerticalAlignment(TextAlign.BOTTOM);
		    textBox.setPosition(currBarX, currBarY + 3);
		    textBox.drawOn(page);

		    // TODO (This is duplicate) draw the end barline
		    // (duplicate!!!!)
		    currBarX += tempMeasure.getMeasureWidth();
		    barline.draw(currBarX, currBarY);

		    continue;// don't draw any notes
		}
		
		
		// -- sort the noes
//TODO:asdf 		Collections.sort(tempMeasure.noteList);

		
		// --- Draw Notes
		Iterator<Note> noteItr = tempMeasure.noteList.iterator();

		// Beam Lines
		Line[] beamLines;
		beamLines = new Line[6];

		// Flag for gracenotes
		boolean gracenoteMode = false;
		// Keep track of last note's position to find double notes
		float lastNoteX = 0.0f;
		float lastNoteY = 0.0f;
//		float slurY = 0.0f;
		
		//List<Note> drawnNoteList = new ArrayList<Note>();

		while (noteItr.hasNext())
		{
		    Note tempNote = noteItr.next();
		    float noteX = currBarX + tempNote.getX();
		    float noteY = currBarY;

		    boolean isBottomNote = true;// no beaming for double notes

		    TextLine textNote = new TextLine(fontNote);

		    //check for existing note at the same x
		 /*   for(int i=0;i<drawnNoteList.size();i++)
		    {
		        if(drawnNoteList.get(i).getX() == noteX)
		        {
		            noteY -= textNote.getHeight();
		            
		        }
		    }*/
		    // check for double notes
		    if (tempNote.isChord() || noteX == lastNoteX)
		    {// TODO: consider the topoffset too
			noteX = lastNoteX;
			lastNoteY -= textNote.getHeight();
			noteY = lastNoteY;
			isBottomNote = false;
		    }
		    else
		    {
			lastNoteX = noteX;
			noteY = lastNoteY = currBarY;
			isBottomNote = true;
		    }

		    // check for fullbar rest
		    if (tempNote.isFullBarRest())
		    {
			TextBox textBox = new TextBox(fontNote);
			textBox.setWidth(tempMeasure.getMeasureWidth() - tempNote.getX());
			textBox.setText(") 0 (");
			textBox.setTextAlignment(Align.CENTER);
			textBox.setNoBorders();
			textBox.setVerticalAlignment(TextAlign.BOTTOM);
			textBox.setPosition(noteX, currBarY + 3);
			textBox.drawOn(page);
		    }

		    textNote.setText(tempNote.getNote(currKey, currKeyAlter));

		    if (tempNote.isGraceNote())
		    {
			gracenoteMode = true;
			noteY -= 12;
			textNote.setTextEffect(Effect.SUPERSCRIPT);
		    }

		    else if (gracenoteMode)
		    {// draw the little arc
			gracenoteMode = false;

			Path path = new Path();
			float graceY = noteY - 2;// - textNote.getHeight();
			path.add(new Point(noteX, graceY));
			path.add(new Point(noteX - 5, graceY, Point.CONTROL_POINT));
			path.add(new Point(noteX - 5, graceY, Point.CONTROL_POINT));
			path.add(new Point(noteX - 5, graceY - 5));
			path.setWidth(noteLineW);
			path.drawOn(page);
		    }

		    textNote.setPosition(noteX, noteY);
		    textNote.drawOn(page);

		    // Accidentals for note
		    if (tempNote.isRest() == false)
		    {
			TextLine noteAccidental = new TextLine(fontAccidentals);
			noteAccidental.setText(tempNote.getCipherAccidental(currKey, currKeyAlter));
			noteAccidental.setPosition(noteX - 3, noteY - textNote.getHeight() * 0.5);
			noteAccidental.drawOn(page);
		    }

		    if (tieDisabled == false)
		    {
			// Tie arcs
			if (tempNote.isTieEnd() || tempNote.isTieStart())
			// if (tempNote.isTieEnd())
			{
			    // loop through the list of opened ties to find the
			    // note
			    Iterator<Note> tiesItr = StartedTieNotes.iterator();
			    while (tiesItr.hasNext())
			    {
				Note tempTieNote = tiesItr.next();
				if (tempNote.samePitchAs(tempTieNote))
				{
				    // draw tie
				    Path TiePath = new Path();
				    float tieY = noteY - 7;
				    float tieStartX = tempTieNote.getOnPageX() + 7;
				    float tieWidth = noteX - tieStartX;
				    TiePath.add(new Point(tieStartX, tieY));
				    TiePath.add(new Point(tieStartX + tieWidth * 0.2, tieY - 5, Point.CONTROL_POINT));
				    TiePath.add(new Point(tieStartX + tieWidth * 0.8, tieY - 5, Point.CONTROL_POINT));
				    TiePath.add(new Point(tieStartX + tieWidth, tieY));
				    TiePath.setWidth(noteLineW);
				    TiePath.drawOn(page);

				    tiesItr.remove();
				    // StartedTieNotes.remove(tempTieNote);
				    break;
				}
			    }
			}
			if (tempNote.isTieStart())
			{
			    tempNote.setOnPagePosition(noteX, noteY);
			    StartedTieNotes.add(tempNote);
			}
		    }

		    // === Draw Beams ===============================
		    // boolean hasDrawnBeam = false;

		    float topOffset = noteY - textNote.getHeight() + 5;
		    float bottomOffset = noteY;
		    if (isBottomNote)
		    {
			for (int beamNum = 0; beamNum < 6; beamNum++)
			{
			    if (tempNote.getBeam(beamNum) != Constants.BEAM_NONE)
			    {
				bottomOffset = noteY + 1 + beamNum * 2;
				if (tempNote.getBeam(beamNum) == Constants.BEAM_BEGIN)
				{
				    beamLines[beamNum] = new Line(noteX, bottomOffset, noteX + noteLineL, bottomOffset);
				    beamLines[beamNum].setWidth(noteLineW);
				}
				else if (tempNote.getBeam(beamNum) == Constants.BEAM_END)
				{
				    beamLines[beamNum].setEndPoint(noteX + noteLineL, bottomOffset);
				    beamLines[beamNum].drawOn(page);
				}
				else if (tempNote.getBeam(beamNum) == Constants.BEAM_FORWARD_HOOK || tempNote.getBeam(beamNum) == Constants.BEAM_BACKWARD_HOOK)
				{
				    beamLines[beamNum] = new Line(noteX, bottomOffset, noteX + noteLineL, bottomOffset);
				    beamLines[beamNum].setWidth(noteLineW);
				    beamLines[beamNum].drawOn(page);
				}
			    }
			    else
			    // check if it's a stand-alone note smaller than
			    // quarter
			    {
				if (tempNote.getType() > beamNum)
				{
				    bottomOffset = noteY + 3 + beamNum * 2;
				    beamLines[beamNum] = new Line(noteX, bottomOffset, noteX + noteLineL, bottomOffset);
				    beamLines[beamNum].setWidth(noteLineW);
				    beamLines[beamNum].drawOn(page);
				}
			    }
			}
		    }

		    // draw octave dots
		    // =====================================================
		    int numDots = tempNote.getCipherOctave(currKey, octaveOffset);
		    for (int i = 0; i < Math.abs(numDots); i++)
		    {
			float dotX = noteX + 4;
			float dotY;
			if (numDots > 0)
			{
			    topOffset -= 3;
			    dotY = topOffset;
			}
			else
			{
			    bottomOffset += 3;
			    dotY = bottomOffset;// - textNote.getHeight() ;// +
						// dotDirection * (
						// textNote.getHeight() +
						// numDots*3);
			}
			Point point = new Point(dotX, dotY);
			point.setShape(Point.CIRCLE);
			point.setFillShape(true);
			point.setRadius(1.0f);
			point.drawOn(page);
		    }

		    // draw slurs
		    if (slurDisabled == false && tempNote.getSlur() != Constants.SLUR_NONE)
		    {
			if (tempNote.getSlur() == Constants.SLUR_START)
			{
			    slurStartX = noteX + 2;
			    slurPath = new Path();
			    slurPath.add(new Point(slurStartX, topOffset - 2));
			    slurMode = true;
			}
			else if (tempNote.getSlur() == Constants.SLUR_STOP || slurMode == true)
			{
			    float slurWidth = noteX + 5 - slurStartX;
			    slurPath.add(new Point(slurStartX + slurWidth * 0.2, topOffset - 8, Point.CONTROL_POINT));
			    slurPath.add(new Point(slurStartX + slurWidth * 0.8, topOffset - 8, Point.CONTROL_POINT));
//			    slurPath.add(new Point(slurStartX + slurWidth * 0.2, topOffset - 8, Point.CONTROL_POINT));
//			    slurPath.add(new Point(slurStartX + slurWidth * 0.8, topOffset - 8, Point.CONTROL_POINT));
			    slurPath.add(new Point(slurStartX + slurWidth, topOffset - 2));
			    slurPath.setWidth(noteLineW);
			    slurPath.drawOn(page);
			    slurMode = false;
			}
		    }

		}

		// draw the end barline
		currBarX += tempMeasure.getMeasureWidth();
		barline.draw(currBarX, currBarY);
		if (tempMeasure.getBarlineStyle(Constants.BARLINE_RIGHT) != Constants.BARLINE_NONE || tempMeasure.isBarlineRepeat(Constants.BARLINE_RIGHT))
		{
		    Barline specialBarline = new Barline(pages.get(pageIndex), barlineH, 1.0f);
		    specialBarline.setStyle(tempMeasure.getBarlineStyle(Constants.BARLINE_RIGHT));
		    if (tempMeasure.isBarlineRepeat(Constants.BARLINE_RIGHT))
		    {
			specialBarline.setRepeat(Constants.BARLINE_REPEAT_RIGHT);
		    }
		    specialBarline.draw(currBarX, currBarY);
		}

	    }

	    pdf.flush();
	    fos.close();
	} catch (FileNotFoundException e)
	{
	    e.printStackTrace();
	} catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void ASDFstartElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {

	tempVal = "";
	if (qName.equalsIgnoreCase("measure"))
	{
//	    String sWidth = attributes.getValue("width");
	    if (attributes.getValue("width") == null)
	    {
		throw new SAXException("measure tag has no width property");
	    }
	    float width = Float.parseFloat(attributes.getValue("width"));
	    width = convertUnit(width);
	    tempMeasure = new Measure(Integer.parseInt(attributes.getValue("number")), width);
	    // tempMeasure = new
	    // Measure(Integer.parseInt(attributes.getValue("number")),convert(Float.parseFloat(attributes.getValue("width"))));
	}
	else if (qName.equalsIgnoreCase("note"))
	{
	    if (attributes.getValue("default-x") != null)
	    {// check if has attribute
		tempNote = new Note(convertUnit(Float.parseFloat(attributes.getValue("default-x"))));
	    }
	    else
	    {
		tempNote = new Note(0);
	    }
	}
	else if (qName.equalsIgnoreCase("tie"))
	{
	    if (tempNote != null)
	    {
		if (attributes.getValue("type").equalsIgnoreCase("start"))
		{
		    tempNote.setTieStart(true);
		}
		if (attributes.getValue("type").equalsIgnoreCase("stop"))
		{
		    tempNote.setTieEnd(true);
		}
	    }
	}
	else if (qName.equalsIgnoreCase("beam"))
	{
	    if (tempNote != null)
	    {
		this.beamNum = Integer.parseInt(attributes.getValue("number"));
	    }
	}
	else if (qName.equalsIgnoreCase("slur"))
	{
	    if (tempNote != null)
	    {
		tempNote.setSlur(attributes.getValue("type"));
	    }
	}
	else if (qName.equalsIgnoreCase("chord"))
	{
	    if (tempNote != null)
	    {
		tempNote.setIsChord(true);
	    }
	}
	else if (qName.equalsIgnoreCase("barline"))
	{
	    if (tempMeasure != null)
	    {
		if (attributes.getValue("location") == null || attributes.getValue("location").equalsIgnoreCase("left"))
		{
		    tempMeasure.setBarline(Constants.BARLINE_LEFT);
		}
		else if (attributes.getValue("location").equalsIgnoreCase("right"))
		{
		    tempMeasure.setBarline(Constants.BARLINE_RIGHT);
		}
	    }
	}
	else if (qName.equalsIgnoreCase("repeat"))
	{
	    if (tempMeasure != null)
	    {
		tempMeasure.setBarlineRepeat(attributes.getValue("direction"));
	    }
	}
	else if (qName.equalsIgnoreCase("dynamics"))
	{
	    tempWords = new Words();
	    tempWords.setY(attributes.getValue("default-y"));
	    tempWords.setRelativeX(attributes.getValue("default-x"));
	    tempWords.setItalicize(true);
	}
	else if (qName.equalsIgnoreCase("fff")||qName.equalsIgnoreCase("ff")||qName.equalsIgnoreCase("f")||qName.equalsIgnoreCase("mf")||qName.equalsIgnoreCase("mp")||qName.equalsIgnoreCase("p")||qName.equalsIgnoreCase("pp")||qName.equalsIgnoreCase("ppp"))
	{
	    tempWords.setText(qName);
	}
	// --- Words ---

//	else if (qName.equalsIgnoreCase("words"))
//	{
//	    tempWords = new Words();
//	    tempWords.setY(attributes.getValue("default-y"));
//	    tempWords.setRelativeX(attributes.getValue("relative-x"));
//	    tempWords.setFontSize(attributes.getValue("font-size"));
//	}
//	else if (qName.equalsIgnoreCase("rehearsal"))
//	{
//	    tempRehearsal = new Words();
//	    tempRehearsal.setY(attributes.getValue("default-y"));
//	    tempRehearsal.setRelativeX(attributes.getValue("default-x"));
//	    tempRehearsal.setFontSize(attributes.getValue("font-size"));
//	    tempRehearsal.setIsRehearsalMark(true);
//	}
	// --- Credits ---
	else if (qName.equalsIgnoreCase("credit"))
	{
	    pageNum = Integer.parseInt(attributes.getValue("page"));
	}
	else if (qName.equalsIgnoreCase("credit-words"))
	{
	    try
	    {
		if (attributes.getValue("default-x") != null && attributes.getValue("default-y") != null)
		{
		    float creditX = convertUnit(Float.parseFloat(attributes.getValue("default-x")));
		    float creditY = pageHeight - convertUnit(Float.parseFloat(attributes.getValue("default-y")));
		    tempCredit = new Credit(pdf, creditX, creditY);
		}
		else
		{
		    tempCredit = new Credit(pdf, 0, 0);
		}
	    } catch (NumberFormatException e)
	    {
		e.printStackTrace();
	    } catch (Exception e)
	    {
		e.printStackTrace();
	    }
	    if (attributes.getValue("font-size") != null)
	    {
		tempCredit.setFontSize(Float.parseFloat(attributes.getValue("font-size")));
		// fontCredit.setSize(Float.parseFloat(attributes.getValue("font-size")));
	    }
	    if (attributes.getValue("justify") != null)
	    {
		tempCredit.setJustify(attributes.getValue("justify"));
	    }
	    if (attributes.getValue("halign") != null)
	    {
		tempCredit.setHAlign(attributes.getValue("halign"));
	    }
	    if (attributes.getValue("valign") != null)
	    {
		tempCredit.setVAlign(attributes.getValue("valign"));
	    }
	    tempCredit.setPageNum(pageNum);
	}

	// --- flags ---
	else if (qName.equalsIgnoreCase("page-layout"))
	{
	    pagelayout = true;
	}
	/*
	 * else if (qName.equalsIgnoreCase("print")){ String tempstr =
	 * attributes.getValue("new-system"); if (tempstr != null &&
	 * tempstr.equals("yes")){ newSystem = true; } }
	 */
	else if (qName.equalsIgnoreCase("print"))
	{
	    String tempstr = attributes.getValue("new-page");
	    if (tempstr != null && tempstr.equals("yes"))
	    {
		tempMeasure.setIsNewPage(true);
	    }
	}
    }

    public void ASDFcharacters(char[] ch, int start, int length) throws SAXException
    {
	tempVal = new String(ch, start, length);
    }

    public void ASDFendElement(String uri, String localName, String qName) throws SAXException
    {

	// --- New Measure or Notes ---
	if (qName.equalsIgnoreCase("measure"))
	{
	    // add it to the list
	    listMeasure.add(tempMeasure);
	}
	else if (qName.equalsIgnoreCase("note"))
	{
	    tempMeasure.addNote(tempNote);
	}
	else if (qName.equalsIgnoreCase("dynamics"))
	{
	    tempMeasure.addWords(tempWords);
	}
//	else if (qName.equalsIgnoreCase("words"))
//	{
//	    tempWords.setText(tempVal);
//	    tempMeasure.addWords(tempWords);
//	}
//	else if (qName.equalsIgnoreCase("rehearsal"))
//	{
//	    tempRehearsal.setText(tempVal);
//	    tempMeasure.addWords(tempRehearsal);
//	}
	// --- Note ---
	else if (qName.equalsIgnoreCase("rest"))
	{
	    tempNote.setIsRest(true);
	}
	else if (qName.equalsIgnoreCase("step"))
	{
	    tempNote.setStep(tempVal.charAt(0));
	}
	else if (qName.equalsIgnoreCase("alter"))
	{
	    tempNote.setAlter(Integer.parseInt(tempVal));
	}
	else if (qName.equalsIgnoreCase("octave"))
	{
	    tempNote.setOctave(Integer.parseInt(tempVal));
	}
	else if (qName.equalsIgnoreCase("type"))
	{
	    tempNote.setType(tempVal);
	}
	else if (qName.equalsIgnoreCase("dot"))
	{
	    tempNote.setDotted(true);
	}
	else if (qName.equalsIgnoreCase("grace"))
	{
	    tempNote.setGraceNote(true);
	}
	else if (qName.equalsIgnoreCase("beam"))
	{
	    if (tempVal.equalsIgnoreCase("begin"))
	    {
		tempNote.setBeam(Constants.BEAM_BEGIN, beamNum);
	    }
	    else if (tempVal.equalsIgnoreCase("continue"))
	    {
		tempNote.setBeam(Constants.BEAM_CONTINUE, beamNum);
	    }
	    else if (tempVal.equalsIgnoreCase("end"))
	    {
		tempNote.setBeam(Constants.BEAM_END, beamNum);
	    }
	    else if (tempVal.equalsIgnoreCase("forward hook"))
	    {
		tempNote.setBeam(Constants.BEAM_FORWARD_HOOK, beamNum);
	    }
	    else if (tempVal.equalsIgnoreCase("backward hook"))
	    {
		tempNote.setBeam(Constants.BEAM_BACKWARD_HOOK, beamNum);
	    }

	}
	// else if (qName.equalsIgnoreCase("")) {
	// tempNote.(Integer.parseInt(tempVal));
	//
	// }
	// --- Measure ---
	else if (qName.equalsIgnoreCase("fifths"))
	{
	    tempMeasure.setKeySignature((fifthsToKeys[Integer.parseInt(tempVal) + 7]).charAt(0), fifthsToAlter[Integer.parseInt(tempVal) + 7]);
	}
	else if (qName.equalsIgnoreCase("beats"))
	{
	    tempMeasure.setBeats(Integer.parseInt(tempVal));
	}
	else if (qName.equalsIgnoreCase("beat-type"))
	{
	    tempMeasure.setBeatType(Integer.parseInt(tempVal));
	}
	else if (qName.equalsIgnoreCase("multiple-rest"))
	{
	    tempMeasure.setMultipleRest(Integer.parseInt(tempVal));
	}
	else if (qName.equalsIgnoreCase("top-system-distance"))
	{
	    if (tempMeasure != null)
	    {
		tempMeasure.setSystemDistance(convertUnit(Float.parseFloat(tempVal)));
		tempMeasure.setNewSystem();
	    }
	}
	else if (qName.equalsIgnoreCase("system-distance"))
	{
	    if (tempMeasure != null)
	    {
		tempMeasure.setSystemDistance(convertUnit(Float.parseFloat(tempVal)));
		tempMeasure.setNewSystem();
	    }
	}
	else if (qName.equalsIgnoreCase("bar-style"))
	{
	    if (tempMeasure != null)
	    {
		if (tempVal.equalsIgnoreCase("heavy-light"))
		{
		    tempMeasure.setBarlineStyle(Constants.BARLINE_THICK_THIN);
		}
		else if (tempVal.equalsIgnoreCase("light-heavy"))
		{
		    tempMeasure.setBarlineStyle(Constants.BARLINE_THIN_THICK);
		}
	    }
	}
	else if (qName.equalsIgnoreCase("barline"))
	{
	    tempMeasure.resetBarlines();
	}
	// --- Layout ---
	else if (qName.equalsIgnoreCase("millimeters"))
	{
	    staffHeightMillimeters = Float.parseFloat(tempVal);
	}
	else if (qName.equalsIgnoreCase("tenths"))
	{
	    tenthsPerStaff = Float.parseFloat(tempVal);
	    tenthToPoint = mmToPoint * staffHeightMillimeters / tenthsPerStaff;
	    // conversion = conversion / (Float.parseFloat(tempVal)) * (75.0f /
	    // 25.4f);
	}
	else if (qName.equalsIgnoreCase("left-margin"))
	{
	    if (pagelayout)
	    {
		leftMargin = convertUnit(Float.parseFloat(tempVal));
	    }
	}
	else if (qName.equalsIgnoreCase("right-margin"))
	{
	    if (pagelayout)
	    {
		rightMargin = convertUnit(Float.parseFloat(tempVal));
	    }
	}
	else if (qName.equalsIgnoreCase("top-margin"))
	{
	    topMargin = convertUnit(Float.parseFloat(tempVal));
	}
	else if (qName.equalsIgnoreCase("page-height"))
	{
	    pageHeight = convertUnit(Float.parseFloat(tempVal));
	}
	else if (qName.equalsIgnoreCase("page-width"))
	{
	    pageWidth = convertUnit(Float.parseFloat(tempVal));
	}
	// --- Credits ---

	else if (qName.equalsIgnoreCase("credit-words"))
	{
	    tempCredit.setText(tempVal);
	    listCredit.add(tempCredit);
	}
	
	// --- flags ---
	else if (qName.equalsIgnoreCase("page-layout"))
	{
	    pagelayout = false;
	}

    }

  /*  public static void main(String[] args)
    {
	
	//String xmlFile = "E:\\temp\\ZhengD.xml" ;
	// String xmlFile = "yangqin.xml";
//	String pdfFile = "OutputFiles\\"+fileName+".pdf";
	

	
	String fileName = "Song of the  Yu Mountain Temple Blocks Percussion II - Zhongruan";
	String xmlFile = "D:\\temp\\"+fileName+".xml";
	String pdfFile = "D:\\temp\\"+fileName+"-Cipher.pdf";
	int octaveChange = 0;
	boolean disableSlur = true;

	xmlcipher converter;
	
	
	
	try
	{
	    File inputFile;
//	    File outputFile;
	    if (args.length != 0)
	    {
		inputFile = new File(args[0]);
		if (inputFile.exists())
		{
		    // outputFile =
		    System.out.println(inputFile.getCanonicalPath());
		}// pdfFile = args[1];
	    }
	    else
	    {

	    }
	    converter = new xmlcipher(octaveChange, disableSlur);
	    converter.convertDoc(xmlFile, pdfFile);
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }*/
}
