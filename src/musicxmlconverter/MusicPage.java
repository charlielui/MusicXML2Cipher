package musicxmlconverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pdfjet.*;

public class MusicPage {

	private List<Credit> creditList;
//	private List<Measure> measurelist;
	Page page;

	public MusicPage(PDF pdf) {

		try {
			page = new Page(pdf, Letter.PORTRAIT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		creditList = new ArrayList<Credit>();
		
	}

	public void addCredit(Credit credit){
		creditList.add(credit);
	}
	
	public void printCredits() throws Exception{
		Iterator<Credit> creditItr = creditList.iterator();
		while(creditItr.hasNext()){
			Credit tempCredit = creditItr.next();
			
			//creditItr.next().drawOn(page);
			tempCredit.drawOn(page);
		}
	}
}
