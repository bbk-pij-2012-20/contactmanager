package contactManager;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class ContactManagerImplTest {
	
	private Calendar someFutureDate;
	private Calendar anotherFutureDate;
	private Calendar somePastDate;
	private Calendar anotherPastDate;
	private Set<Contact> someContacts1;
	private Set<Contact> someContacts2;
	private Set<Contact> someNonContacts;
	private String someText;
	private ContactManagerImpl cmi; 
	private Contact nonContact; 
//	*NB: cmi is ..Impl 'cause toString(date) and 
//	generateId() are not in the interface.
	
	@Before
	public void setUp() throws Exception {
	
		someFutureDate = new GregorianCalendar(2014,11,15,17,30);//id=328270 (15/12/14 17:30, though time is not included in generating id) 
		anotherFutureDate = new GregorianCalendar(2015,11,15,17,0);//id=93060 (15/12/15 17:00, though time is not included in generating id)
		somePastDate = new GregorianCalendar(1974,11,15,16,30);//id=975770 (15/12/74 16:30, though time is not included in generating id)
		anotherPastDate = new GregorianCalendar(1975,11,15,16,0);
		someContacts1 = new HashSet<>();
		someContacts2 = new HashSet<>();
		someNonContacts = new HashSet<>();
		someText = "someText";
		cmi = new ContactManagerImpl();
		cmi.meetingMap = new HashMap<>();
		cmi.futureMeetingMap = new HashMap<>();
		cmi.pastMeetingMap = new HashMap<>();
		cmi.contactMap = new HashMap<>();		
		String name1 = "JohnFirstSet";//id=708830
		String name2 = "BillBothSets";//id=333690
		String name3 = "FrankSecSet";//id=261090
		String name4 = "JimSecSet";//id=911670
		String noName = "nobodySetOnly";	
		String notes = "yeah but no";
		String notes2 = "no but yeah";
		cmi.addNewContact(name1,notes);
		cmi.addNewContact(name2,notes2);
		
		for (Contact contact : cmi.contactMap.values()) {
		
			someContacts1.add(contact);
	
		}
				
		cmi.addNewContact(name3,notes);
		cmi.addNewContact(name4,notes2);
		
		for (Contact contact : cmi.contactMap.values()) {
		
			if (contact.getName().equals(name3)) {
		
				someContacts2.add(contact);
			
			}
		
			if (contact.getName().equals(name4)) {
		
				someContacts2.add(contact);
		
			}	
		
			if (contact.getName().equals(name2)) { 	// If I comment
		
				someContacts2.add(contact);			// this out, getFutureMeetingList()
		
			}										// should only return meeting with someContacts1.
		}
		
		nonContact = new ContactImpl(noName);
		someNonContacts.add(nonContact);

	}
	
	@After
	public void tearDown() throws Exception {
	
		cmi = null;
	
	}
	
	/**
	 * This test gets the meeting after calling two other 
	 * methods: addNewContact() and addNewPastingMeeting().
	 * The test only passes when I comment out parts of the
	 * ContactManagerImpl constructor related to the file
	 * reading in. Instead the maps are initialised. 
	 */
	@Test
	public void testGetPastMeeting() {				
		
		String expectedOutput = "19741115";
		cmi.counter = 0;
		cmi.addNewPastMeeting(someContacts1,somePastDate,someText);
		String actualOutput = null;	
		
		for (Map.Entry<Integer,PastMeeting> entry : cmi.pastMeetingMap.entrySet()) {
		
			if (entry.getKey() == 975770) { // 975770 = id for 15/12/74 16:30
	
				Calendar date = cmi.getPastMeeting(entry.getKey()).getDate();
				actualOutput = ""+date.get(Calendar.YEAR)+date.get(Calendar.MONTH)+date.get(Calendar.DAY_OF_MONTH);
		
			}
		}
		
		assertEquals(expectedOutput,actualOutput);	
	}

	@Test
	public void testAddFutureMeeting() {		
		
		cmi.counter = 0;
// The following 15 lines commented out were used for testing	 exceptions.	
/*
 * 		int actualOutput = cmi.addFutureMeeting(someNonContacts,someFutureDate);
 *		int expectedOutput = 328270; // someFutureDate id = 328270 (15/12/14 17:30, though time is not included in generating id)
 * 
 * The 2 lines above correctly throw an exception as one of the contacts not 
 * found in contactMap, it was added directly through the ContactImpl(String name) 
 * constructor rather than via addNewContact().
 */
 
/*		int actualOutput = cmi.addFutureMeeting(someContacts1,somePastDate);
 *		int expectedOutput = 975770; // 975770 = id for 15/12/74 16:30
 * 
 * The 2 lines above correctly throw an exception as the date is in 
 * the past rather than in the future.
 */

		int expectedOutput = 328270; // someFutureDate id = 328270 (15/12/14 17:30, though time is not included in generating id)
		int actualOutput = cmi.addFutureMeeting(someContacts1,someFutureDate);
	
		assertEquals(expectedOutput,actualOutput);

	}
	
	/**
	 * Testing the private method generateId() in ContactManagerImpl with 
	 * name string as inputs. In order to test this method directly 
	 * (i.e. not via the addNewContact() or addNewMeeting() methods), the 
	 * visibility of the private method is changed and then the 
	 * generateId() method is first called and then the contact and key 
	 * are put in directly. This is to show that generateId() checks the 
	 * hashmaps for pre-existing id numbers, which prompt a counter increment.
	 */
	@Test
	public void testGenerateId() {		

		cmi.counter = 0;
		int expectedOutput1 = 977170;//"bob"
		int expectedOutput2 = 78550;//"mac"
		int expectedOutput3 = 78551;//"mac" again
		String name = "bob";
		String name2 = "Mac";
		String name3 = "mac";	
		int actualOutput1 = cmi.generateId(name.toLowerCase());		
		cmi.contactMap.put(977170,new ContactImpl(name.toLowerCase()));
		int actualOutput2 = cmi.generateId(name2);
		cmi.contactMap.put(78550,new ContactImpl(name2.toLowerCase()));
		int actualOutput3 = cmi.generateId(name3.toLowerCase());//a 2nd mac
		
		assertEquals(expectedOutput1,actualOutput1);
		assertEquals(expectedOutput2,actualOutput2);
		assertEquals(expectedOutput3,actualOutput3);
	
	}
	
	/**
	 * Testing with name string as inputs. In order to test this method 
	 * directly (i.e. not via the addNewContact() or addNewMeeting() 
	 * methods), the visibility of the private method is changed and then 
	 * the generateId() method is first called and then the contact and 
	 * key are put in directly. This is to show that generateId() checks 
	 * the hashmaps for pre-existing id numbers, which prompt a counter 
	 * increment.
	 */
	@Test
	public void testGenerateId2() {

		cmi.counter = 0;
		int expectedOutput1 = 328270;// someFutureDate id = 328270 (15/12/14 17:30, though time is not included in generating id)
		int expectedOutput2 = 975770;//somePastDate id = 975770 (15/12/74 16:30)
		int expectedOutput3 = 972691;//same somePastDate again, hence counter should increment.
		int actualOutput1 = cmi.generateId(cmi.toString(someFutureDate));
		cmi.meetingMap.put(328270,new MeetingImpl());
		int actualOutput2 = cmi.generateId(cmi.toString(somePastDate));
		cmi.meetingMap.put(975770,new MeetingImpl());
		int actualOutput3 = cmi.generateId(cmi.toString(somePastDate));
		
		assertEquals(expectedOutput1,actualOutput1);
		assertEquals(expectedOutput2,actualOutput2);
		assertEquals(expectedOutput3,actualOutput3);
	
	}
	
	/**
	 * As with testGetPastMeeting(), this test relies on two other
	 * methods working in order to work and not throw an exception.
	 * In this case, addFutureMeeting(). As before addNewContact()
	 * is used (in the setUp()) to avoid throwing exception 
	 * "one or more contacts does not exist". 
	 */
	@Test
	public void testGetFutureMeeting() {
		
		cmi.counter = 0;
		String expectedOutput = "201411151730";
		cmi.addFutureMeeting(someContacts1,someFutureDate);
		String actualOutput = null;	
	
		for (Entry<Integer, FutureMeeting> entry : cmi.futureMeetingMap.entrySet()) {
		
			if (entry.getKey() == 328270) { // someFutureDate id = 328270 (15/12/14 17:30, though time is not included in generating id)
			
				Calendar date = cmi.getFutureMeeting(entry.getKey()).getDate();
				actualOutput = ""+date.get(Calendar.YEAR)+date.get(Calendar.MONTH)+date.get(Calendar.DAY_OF_MONTH)+date.get(Calendar.HOUR_OF_DAY)+date.get(Calendar.MINUTE);
		
			}
		}
	
		assertEquals(expectedOutput,actualOutput);	
	
	}

	@Test
	public void testGetMeeting() {

		String expectedOutput1 = "201411151730";		
		cmi.counter = 0;
		cmi.addFutureMeeting(someContacts1,someFutureDate);
		String actualOutput1 = null;	
		
		for (Entry<Integer, FutureMeeting> entry : cmi.futureMeetingMap.entrySet()) {
			
			if (entry.getKey() == 328270) { // someFutureDate id = 328270 (15/12/14 17:30, though time is not included in generating id)
			
				Calendar date = cmi.getMeeting(entry.getKey()).getDate();
				actualOutput1 = ""+date.get(Calendar.YEAR)+date.get(Calendar.MONTH)+date.get(Calendar.DAY_OF_MONTH)+date.get(Calendar.HOUR_OF_DAY)+date.get(Calendar.MINUTE);
		
			}
		}
				
		String expectedOutput2 = "197411151630";
		cmi.counter = 0;
		cmi.addNewPastMeeting(someContacts1,somePastDate,someText);
		String actualOutput2 = null;	
		
		for (Map.Entry<Integer,PastMeeting> entry : cmi.pastMeetingMap.entrySet()) {
		
			if (entry.getKey() == 975770) { //somePastDate id = 975770 (15/12/74 16:30)
				Calendar date = cmi.getMeeting(entry.getKey()).getDate();
				actualOutput2 = ""+date.get(Calendar.YEAR)+date.get(Calendar.MONTH)+date.get(Calendar.DAY_OF_MONTH)+date.get(Calendar.HOUR_OF_DAY)+date.get(Calendar.MINUTE);
			}
			
		}
			
		assertEquals(expectedOutput1,actualOutput1);	
		assertEquals(expectedOutput2,actualOutput2);	
	
	}

	/**
	 * A very round about way of testing getFutureMeetingList() but not sure how else to do this. 
	 * First I create two new meetings using two different sets of contacts and dates. 
	 * One of the contacts is present in both sets of contacts (added in the setUp()).
	 * Then I make two lists, one containing just one of the meetings created (using the id that
	 * ContactManagerImpl generates for a date), the other containing both meetings. To compare
	 * the outputs, I fill another list with a member field of the meeting as comparing the objects 
	 * directly did not seem to pass (their memory references were being compared and these were 
	 * always different). As such a list of acquired futureMeetings by getFutureMeetingList() were 
	 * compared with the expected meeting list, via their dates. 
	 *  
	 */
	@Test
	public void testGetFutureMeetingListContactImpl() {
		
		cmi.counter = 0;
		cmi.addFutureMeeting(someContacts1,someFutureDate);
		cmi.counter = 0;
		cmi.addFutureMeeting(someContacts2,anotherFutureDate);
		Contact contact1 = null;
		Contact contact2 = null;

		for (Contact c : cmi.contactMap.values()) {
			
			if (c.getName().equals("JohnFirstSet")) {
			
				contact1 = c;
			
			}
			
			if (c.getName().equals("BillBothSets")) {
			
				contact2 = c;
			
			}	
		}
		
		List<Meeting> expectedList1 = new ArrayList<>();
		expectedList1.add(cmi.getFutureMeeting(328270));// someFutureDate id = 328270 (15/12/14 17:30, though time is not included in generating id)
		List<Integer> expectedDates1 = new ArrayList<>();
		
		for (Meeting meeting : expectedList1) {
		
			expectedDates1.add(meeting.getId());
		
		}
		
		List<Integer> expectedOutput1 = expectedDates1; 
		List<Meeting> expectedList2 = new ArrayList<>();
		expectedList2 = expectedList1;
		expectedList2.add(cmi.getFutureMeeting(93060));// anotherFutureDate id = 93060 (15/12/15 17:00, though time is not included in generating id)
		List<Integer> expectedDates2 = new ArrayList<>();
		
		for (Meeting meeting : expectedList2) {
		
			expectedDates2.add(meeting.getId());
	
		}		
		
		List<Integer> expectedOutput2 = expectedDates2;
		List<Meeting> actualList1 = cmi.getFutureMeetingList(contact1);	
		List<Integer> actualDates1 = new ArrayList<>();
		
		for (Meeting meeting : actualList1) {
		
			actualDates1.add(meeting.getId());
		
		}
		
		List<Integer> actualOutput1 = actualDates1;
		List<Meeting> actualList2 = cmi.getFutureMeetingList(contact2);
		List<Integer> actualDates2 = new ArrayList<>();
		
		for (Meeting meeting : actualList2) {
		
			actualDates2.add(meeting.getId());
		
		}
	
		List<Integer> actualOutput2 = actualDates2;

		assertEquals(expectedOutput1,actualOutput1);
		assertEquals(expectedOutput2,actualOutput2);
		
	}
	
	@Test
	public void testGetFutureMeetingListCalendar() {
		
		cmi.counter = 0;
		cmi.addFutureMeeting(someContacts1,someFutureDate);
// someFutureDate id = 328270 (15/12/14 17:30, though time is not included in generating id)
		cmi.addFutureMeeting(someContacts2,someFutureDate);
// id = 328271, because 328270 already used, so counter increments. 
		cmi.addFutureMeeting(someContacts2,anotherFutureDate);
// id = 93060 for 15/12/15 17:00. (time not used for id) 
	
		List<Meeting> expectedList1 = new ArrayList<>();
		expectedList1.add(cmi.getFutureMeeting(328270));// someFutureDate id = 328270 (15/12/14 17:30, though time is not included in generating id)
		expectedList1.add(cmi.getFutureMeeting(328271));
		List<Integer> expectedId1 = new ArrayList<>();
		
		for(Meeting meeting : expectedList1) {

			expectedId1.add(meeting.getId());
		
		}
				
		List<Integer> expectedOutput1 = expectedId1; 
		List<Meeting> expectedList2 = new ArrayList<>();
		expectedList2.add(cmi.getFutureMeeting(93061));// anotherFutureDate id = 93060 (15/12/15 17:00). As counter's not reset, should be 93061.
		List<Integer> expectedId2 = new ArrayList<>();

		for (Meeting meeting : expectedList2) {
		
			expectedId2.add(meeting.getId());
		
		}

		List<Integer> expectedOutput2 = expectedId2;
		List<Meeting> actualList1 = cmi.getFutureMeetingList(someFutureDate);	
		List<Integer> actualId1 = new ArrayList<>();
		
		for (Meeting meeting : actualList1) {
		
			actualId1.add(meeting.getId());
		
		}
		
		List<Integer> actualOutput1 = actualId1;
		List<Meeting> actualList2 = cmi.getFutureMeetingList(anotherFutureDate);	
		List<Integer> actualId2 = new ArrayList<>();
		
		for (Meeting meeting : actualList2) {
		
			actualId2.add(meeting.getId());
	
		}
	
		List<Integer> actualOutput2 = actualId2;
		
		assertEquals(expectedOutput1,actualOutput1);
		assertEquals(expectedOutput2,actualOutput2);
	
	}

	@Test
	public void testGetPastMeetingList() {
		fail("Not yet implemented");
	}

	/**
	 * In order to test this method, it is first called,
	 * with some contacts, a past date and text.
	 * I know somePastDate produces an Id of 975770. 
	 * So, in order to test for creation of the new meeting,
	 * partly confirmed by the creation of the id, the key-
	 * value pair is put in the map, and finally, the id 
	 * for the new meeting created is compared with the 
	 * expected id number (975770).
	 */
	@Test
	public void testAddNewPastMeeting() {
		
		cmi.counter = 0;
		int expectedOutput = 975770;//id for somePastDate 
		cmi.addNewPastMeeting(someContacts1,somePastDate,someText);
		int actualOutput = 0;
	
		for (Meeting meeting : cmi.pastMeetingMap.values()) {
		
			actualOutput = meeting.getId();
		
		}		
	
		assertEquals(expectedOutput,actualOutput);	
	
	}

	@Test
	public void testAddMeetingNotes() {
		
		cmi.counter = 0;
		cmi.addNewPastMeeting(someContacts1,somePastDate,someText);
		cmi.addMeetingNotes(975770,"testingAddMeetingNotes()");
		String expectedOutput = "someText; testingAddMeetingNotes()";
		String actualOutput = "";
	
		for (PastMeeting meeting : cmi.pastMeetingMap.values()) {
			
			if (meeting.getId() == 975770) {
		
				actualOutput = meeting.getNotes();
			
			}
		}
	
		assertEquals(expectedOutput,actualOutput);
	
	}
	
 	@Test
	public void testAddNewContact() {
 		
 		String name = "Bob John";//with counter=0, id = 499100;
 		String notes = "I dig a pigmy";
 		cmi.addNewContact(name,notes);
 		String expectedOutput1 = "Bob John";
		int expectedOutput2 = 499100;
		 
 		String actualOutput1 = cmi.contactMap.get(499100).getName();
 		int actualOutput2 = 0;
 		
 		for (Map.Entry<Integer,Contact> contact : cmi.contactMap.entrySet()) {
		
 			if (contact.getValue().getName().equals("Bob John")) {
			
				actualOutput2 = contact.getKey();
		
			}
		}
 		
 		assertEquals(expectedOutput1,actualOutput1);
 		assertEquals(expectedOutput2,actualOutput2);
 	
 	}
		
	@Test
	public void testGetContactsIntArray() {
	/* name1 "JohnFirstSet" id=708830 *
	 * name2 "BillBothSets" id=333690 *
	 * name3 "FrankSecSet" id=261090 *
	 * name4 "JimSecSet" id=911670 *
	 * NB: all of these ids are after conversion toLowerCase())
	 */
		int expectedOutput1 = 708830+333690;//name1&name2
		int expectedOutput2 = 333690+261090+911670;//name2&name3&name4
		
		Set<Contact> actualSet1 = cmi.getContacts(708830,333690); 		
		Set<Contact> actualSet2 = cmi.getContacts(333690,261090,911670); 

		int actualOutput1 = 0;
		int actualOutput2 = 0;

		for (Contact c1 : actualSet1) {
			
			if (c1.getName().equals("JohnFirstSet")) {
		
				actualOutput1 += c1.getId();
		
			}
			
			if (c1.getName().equals("BillBothSets")) {
			
				actualOutput1 += c1.getId();
			
			}
			
			if (c1.getName().equals("FrankSecSet")) {
			
				actualOutput1 += c1.getId();
			
			}

			if (c1.getName().equals("JimSecSet")) {
			
				actualOutput1 += c1.getId();
			
			}
		}
		
		for (Contact c2 : actualSet2) {
			
			if (c2.getName().equals("JohnFirstSet")) {
				actualOutput2 += c2.getId();
			}
			
			if (c2.getName().equals("BillBothSets")) {
				actualOutput2 += c2.getId();
			}
			
			if (c2.getName().equals("FrankSecSet")) {
				actualOutput2 += c2.getId();
			}
			
			if (c2.getName().equals("JimSecSet")) {
				actualOutput2 += c2.getId();
			}	
		}
		
		assertEquals(expectedOutput1,actualOutput1);
		assertEquals(expectedOutput2,actualOutput2);
	
	}

	@Test
	public void testGetContactsString() {		
	
		int expectedOutput1 = 708830+333690+261090+911670;
		int expectedOutput2 = 708830+261090;
		int actualOutput1 = 0;
		int actualOutput2 = 0;
		Set<Contact> contacts1 = cmi.getContacts("set");
		Set<Contact> contacts2 = cmi.getContacts("n");
		
		for (Contact contact : contacts1) {
		
			actualOutput1 += contact.getId();
		
		}
		
		for (Contact contact : contacts2) {
		
			actualOutput2 += contact.getId();
	
		}
	
		assertEquals(expectedOutput1,actualOutput1);
		assertEquals(expectedOutput2,actualOutput2);
	
	}

	@Test
	public void testFlush() {
		String XMLFILENAME = "contactsTEST.xml";
			
		XMLDecoder decode = null;
		
		try {
			
			decode = new XMLDecoder(
			
					new BufferedInputStream(
				
					new FileInputStream(XMLFILENAME)));
				
			cmi.meetingMap = (Map<Integer,Meeting>) decode.readObject();
			cmi.futureMeetingMap = (Map<Integer,FutureMeeting>) decode.readObject();
			cmi.pastMeetingMap = (Map<Integer,PastMeeting>) decode.readObject();
			cmi.contactMap = (Map<Integer,Contact>) decode.readObject();
	//		cmi.counter = (int) decode.readObject();
	
		} catch (FileNotFoundException e) {
	
				e.printStackTrace();
				System.out.println("file not found");
		}
		
		cmi.addFutureMeeting(someContacts1,someFutureDate);
		cmi.flush();
		
	}
}