package com.company.View;
import com.company.Controller.HandlePricingMgr;
import com.company.Entity.*;

import com.company.Controller.MovieGoerController;
import com.company.Utils.UserInputOutput;

import java.io.IOException;
import java.util.*;

/**
 * Generate User interface for seat selection, tickets and payment for movie goers
 * @author Group 2 - SS6
 * @version 1.0
 * @since 2019-11-12
 */
public class SeatUI {
	/**
	 * gets user input of cineplex
	 * @param cineplexes All cineplexes
	 * @return index of cineplexes array
	 */
	public int getCineplexSelectionView(ArrayList<Cineplex> cineplexes) {
		System.out.println("Please select cineplex: ");
		for(int i=0;i<cineplexes.size();i++) {
			System.out.println((i+1)+")"+cineplexes.get(i).getCineplexName());
		}
		return UserInputOutput.getUserChoice(1, cineplexes.size())-1;
	}

	/**
	 * gets user input of movie
	 * @param movies
	 * @return index of movies array
	 */
	public int getMovieSelectionView(ArrayList<String> movies) {
		System.out.println("Please select movie: ");
		for(int i=0;i<movies.size();i++) {
			System.out.println((i+1)+")"+ movies.get(i));
		}
		return UserInputOutput.getUserChoice(1, movies.size())-1;
	}

	/**
	 * gets user input of cinema type
	 * @param cinemaType
	 * @return index of cinemaType array
	 */
	public int getCinemaTypeSelectionView(ArrayList<String> cinemaType) {
		System.out.println("Please select cinema: ");
		for(int i=0;i<cinemaType.size();i++) {
			System.out.println((i+1)+")"+ cinemaType.get(i));
		}
		return UserInputOutput.getUserChoice(1, cinemaType.size())-1;
	}

	/**
	 * gets user input of showtime
	 * @param showTimes
	 * @return index of showTimes array
	 */
	public int getShowTimeSelectionView(ArrayList<ShowTime> showTimes) {
		System.out.println("Please select Show Time: ");
		int i;
		Price p = HandlePricingMgr.readPriceFile();

		for(i=0;i<showTimes.size();i++) {
			if(p.isHoliday(showTimes.get(i).getDateTime()))
				System.out.println((i+1)+")"+UserInputOutput.createDayOfWeekString(showTimes.get(i).getDateTime())+"(Holiday)");
			else
				System.out.println((i+1)+")"+UserInputOutput.createDayOfWeekString(showTimes.get(i).getDateTime()));
		}
		System.out.println((i+1)+")"+ "Return to main menu");

		int choice = UserInputOutput.getUserChoice(1, showTimes.size()+1)-1;
		if(choice>=showTimes.size()) return -1;
		else return choice;
	}

	/**
	 * Primary view of selection of seats
	 * @param st Customer Selected Showtime
	 * @param basePrice Base price of selected tickets
	 * @return Hashmap<seatID,age group>
	 *     seatID is the ID of the selected seat
	 *     age group is the age category selected by the user
	 */
	public HashMap<String,String> getSeatSelectionMenu(ShowTime st, float basePrice) {
		MovieGoerController mgc = new MovieGoerController();
		UserInputOutput.displayHeader("Seat Selection");
		HashMap<String,String> chosenSeat = new HashMap<>();
		getSeatListing(st,chosenSeat);
		System.out.println("---------------------------------------------------------------------");
		int choice;
		float totalPrice = 0;
		while(true) {
			System.out.println(
					"1. Select seat\n" +
							"2. Remove selected seat\n" +
							"3. Make Payment");

			choice = UserInputOutput.getUserChoice(1, 3);
			switch(choice) {
				case 1:
					String selectedSeat = getSeatSelectionView(st,chosenSeat);
					if(selectedSeat!=null)
						if(chosenSeat.containsKey(selectedSeat))
							System.out.println("The seat is already chosen!!!");
						else{
							chosenSeat.put(selectedSeat,getAgeSelection());
							totalPrice = mgc.calculateTotalPrice(chosenSeat,basePrice);
						}
					getSeatListing(st,chosenSeat);
					getTicketPriceView(chosenSeat,basePrice);
					System.out.println("Current total price: "+ Float.toString(totalPrice));
					break;
				case 2:
					String removedSeat = getSeatSelectionView(st,chosenSeat);
					if(chosenSeat.size()>0)
						if(removedSeat==null)
							continue;
						else if(chosenSeat.containsKey(removedSeat)){
							chosenSeat.remove(removedSeat);
							totalPrice = mgc.calculateTotalPrice(chosenSeat,basePrice);
						}
						else
							System.out.println("The seat is not chosen!!!");
					else
						System.out.println("You have not chosen any seats!!!");
					getSeatListing(st,chosenSeat);
					getTicketPriceView(chosenSeat,basePrice);
					System.out.println("Current price: "+ totalPrice);
					break;
				case 3:
					if(getPaymentView(chosenSeat,basePrice,st)) return chosenSeat;
					else break;
				default:
					System.out.println("Invalid choice");
			}
		}
	}

	/**
	 * Prints out the ticket types selected by the customer and the corresponding price
	 * @param chosenSeat Hashmap with key seatID and value age group selected by the customer
	 * @param basePrice Base price of the tickets
	 */
	public void getTicketPriceView(HashMap<String,String> chosenSeat, float basePrice){
		MovieGoerController mgc = new MovieGoerController();
		HashMap<String,Integer> ageCount = mgc.getAgeCount(chosenSeat);
		for(Map.Entry<String,Integer> ac : ageCount.entrySet()){
			System.out.println(ac.getKey() + " x" + ac.getValue() + ": " +
					(mgc.calculateTicketPrice(ac.getKey(),basePrice)*ac.getValue()));
		}
	}

	/**
	 * Get user input of age category
	 * @return age category
	 */
	public String getAgeSelection() {
		System.out.println("Select Age: ");
		System.out.println(
				"1. Adult\n" +
						"2. Senior Citizen\n" +
						"3. Child");
		int choice = UserInputOutput.getUserChoice(1, 3);
		Price prices = new Price();
		switch(choice) {
			case 1:
				return "Adult";
			case 2:
				return "Senior Citizen";
			case 3:
				return "Child";
		}
		return "Adult";
	}

	/**
	 * gets user input of seat
	 * @param st selected showtime
	 * @param chosenSeats previously selected seats
	 * @return seatID
	 */
	public String getSeatSelectionView(ShowTime st, HashMap<String,String> chosenSeats) {
		String row = "-1";
		int column = -1;
		boolean complete = false;
		ArrayList<String> chosen = new ArrayList<>();
		for(String seatId: chosenSeats.keySet()){
			chosen.add(seatId);
		}
		while(!complete){
			while(!complete) {
				System.out.println("Please select row: ");
				try {
					Scanner scanner = new Scanner(System.in);
					row = scanner.next();
				} catch (InputMismatchException e) {
					System.out.println("Invalid row");
					continue;
				}

				if(Arrays.asList(st.getRowID()).contains(row)) {
					complete = true;
				}
				else {
					System.out.println("Invalid row");
				}
			}

			complete = false;
			while(!complete) {
				System.out.println("Please select column: ");
				try {
					Scanner scanner = new Scanner(System.in);
					column = scanner.nextInt();
				} catch (InputMismatchException e) {
					System.out.println("Invalid column");
				}

				if(st.getSeat(row, column)!=null && st.getSeat(row, column).getIsAssigned()) {
					System.out.println("Seat taken!!!");
					return null;
				}
				else if(st.getSeat(row, column)!=null){
					complete = true;
				}
				else {
					System.out.println("Invalid column");
				}
			}
		}
		String rowId = "r"+row+"c"+column;
		return rowId;
	}

	/**
	 * shows the seating arrangement and the occupied seats
	 * @param st Selected showtime
	 * @param chosenSeats selected seats
	 */
	public void getSeatListing(ShowTime st, HashMap<String,String> chosenSeats) {
		ArrayList<String> chosen = new ArrayList<>();
		for(String seatId: chosenSeats.keySet()){
			chosen.add(seatId);
		}
		System.out.println("################################# Screen #############################");
		for(int r=0;r<st.getNumRows();r++) {
			boolean first = true;
			System.out.print(st.getRowID(r));
			for(int c=0;c<st.getNumColumns();c++) {
				Seat s = st.getSeat(r, c);
				if(s !=null) {
					if(first) {
						System.out.print("|");
						first = false;
					}
					if(s.getIsAssigned()) {
						System.out.print(" O |");
					}
					else if(chosen.size()!=0 && chosen.contains(s.getSeatID())) {
						System.out.print(" C |");
					}
					else if(c==8) {
						System.out.print("    |");
					}
					else {
						System.out.print("   |");
					}
				}
				else {
					System.out.print("    ");
				}
			}
			System.out.println();
		}
		System.out.println("   0   1   2   3   4   5   6   7           10  11  12  13  14  15  16");
		System.out.println("---------------------------------------------------------------------");
		System.out.println("O: Occupied, C: Chosen");
	}

	/**
	 * Displays the seats, age category selected and the corresponding prices
	 * confirms user payment
	 * @param chosenSeats selected seats
	 * @param basePrice base price of the tickets
	 * @param st selected showtimes
	 * @return True if payment is successful
	 */
	public boolean getPaymentView(HashMap<String,String> chosenSeats, float basePrice, ShowTime st){
		MovieGoerController mgc = new MovieGoerController();
		System.out.println("---------------------------------------------------------------------");
		for (Map.Entry<String, String> seat : chosenSeats.entrySet()) {
			String seatID = seat.getKey();
			String age = seat.getValue();
			float price = mgc.calculateTicketPrice(age,basePrice);
			System.out.println(age);
			System.out.println("Seat: " + st.getSeat(seatID).getRow() + st.getSeat(seatID).getColumn());
		}
		System.out.println("Total Price: " + mgc.calculateTotalPrice(chosenSeats,basePrice));
		String choice;
		while(true){
			System.out.println("Confirm payment? (y/n)");
			Scanner scanner = new Scanner(System.in);
			choice = scanner.next();
			switch(choice){
				case "y":
					return true;
				case "n":
					return false;
				default:
					System.out.println("Invalid choice!!!");
			}
		}
	}

	/**
	 * Displays movie tickets purchased by the user
	 * @param cineplex chosen cineplex
	 * @param chosenSeats chosen seats
	 * @param cinema chosen cinema
	 * @param st chosen showtime
	 */
	public void getTicketView(Cineplex cineplex, HashMap<String,String> chosenSeats,Cinema cinema, ShowTime st){
		UserInputOutput.displayHeader("Tickets");
		for (Map.Entry<String, String> seat : chosenSeats.entrySet()) {
			System.out.println("===========================================");
			String seatID = seat.getKey();
			String age = seat.getValue();
			System.out.println(st.getMovie().getTitle());
			System.out.println("Cineplex: " + cineplex.getCineplexName());
			System.out.println("Cinema: " + cinema.getCID());
			System.out.println("Seat: " + st.getSeat(seatID).getRow() + st.getSeat(seatID).getColumn());
			System.out.println(age);
			System.out.println("===========================================");
		}

	}
}
