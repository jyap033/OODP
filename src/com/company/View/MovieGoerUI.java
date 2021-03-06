package com.company.View;
import com.company.Controller.MovieGoerController;
import com.company.Entity.*;
import com.company.TopMovies.Top5CurrentMovies;
import com.company.TopMovies.TopMovieFactory;
import com.company.Utils.UserInputOutput;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDate;


/**
 *  MovieGoerUI to generate User interface for movie goers
 * @author Group 2 - SS6
 * @version 1.0
 * @since 2019-11-12
 */
public class MovieGoerUI implements GeneralUI{

	MovieGoerController movieController = new MovieGoerController();

	/**
	*Prints out all the movies available in the database
	*In the end, asks users which movie they want to view the details
	 */

	public void getMovieListingView(){
		ArrayList<Movie> movieList = new ArrayList<Movie>();
		movieList = movieController.getAllMovieList();
		movieController.list(movieList);
		getMovieDetailsView(movieList.get(UserInputOutput.getUserChoice(1, movieList.size())-1));
	}
	/**
	 *Prints the choices available for the users
	 *Asks for users' input
	 *Once they choose, the method will execute to the intended method.
	 */
	public void displayHomePage() {

		boolean loop = true;


		while (loop) {
			UserInputOutput.displayHeader("Movie Goer Main Menu");
			System.out.println("1) Book Ticket");
			System.out.println("2) Movie Listing");
			System.out.println("3) Search Movie detail");
			System.out.println("4) Make a review");
			System.out.println("5) View booking history");
			System.out.println("6) Top 5 Movies");
			System.out.println("7) Quit");
			try {
				switch (UserInputOutput.getUserChoice(1, 7)) {
					case 1:
						movieController.seatSelection();
						break;
					case 2:
						getMovieListingView();
						break;
					case 3:
						searchMovieUI();
						break;
					case 4:
						getMakeAReviewView();
						break;
					case 5:
						getBookingHistoryView();
						break;
					case 6:
						listTopMoviesUI();
						break;
					case 7:
						loop = false;
						break;
					default:
						System.out.println("Please input 1,2,3,4,5,6 or 7.");
						break;
				}
			} catch (InputMismatchException e) {
				System.out.println("Please input an integer.");
			}
		}
	}
	/**
	 *Prints out all the Cineplexes available
	 */
	public void getCineplexView() {
		int i=1;
		ArrayList<Cineplex> cineplexList = new ArrayList<Cineplex>();
		cineplexList = movieController.getCineplexList();
		UserInputOutput.displayHeader("Cineplex List");
		for (Cineplex c: cineplexList)//CineplexList is initiated in main
		{
			System.out.println(i + ") " + c.getCineplexName());
			i++;
		}
	}
	/**
	 *Prints out only the movies with statusType = 'Now showing'
	 *In the end, asks users which movie they want to make reviews
	 */
	public void getMakeAReviewView() {
		System.out.println("Please choose a movie to make review: ");
		int i=1;
		ArrayList<Movie> movieList = new ArrayList<Movie>();
		movieList = movieController.getNowPreviewShowingMovieList();
		UserInputOutput.displayHeader("Movie List");
		for (Movie m: movieList) //MovieLists is initiated in main
		{
			System.out.println(i + ": " + m.getTitle() + "(" + m.getStatusType()+")");
			i++;
		}

		HandleReviewUI.MakeReview(movieList.get(UserInputOutput.getUserChoice(1, movieList.size())-1));
	}

	/**
	 * Prints out all transaction history of customer
	 */
	public void getBookingHistoryView(){
		Price p = new Price();
		Customer customer = movieController.getCusCookie();
		ArrayList<Transaction> transactions = customer.getTransactions();
		if(transactions.size()==0){
			System.out.println("No bookings have been made.");
			return;
		}
		Transaction curTransaction = transactions.get(0);
		UserInputOutput.displayHeader("Booking History");
		System.out.println("Customer name: " + curTransaction.getCustomerName());
		System.out.println("Customer email: " + curTransaction.getCustomerEmail());
		System.out.println("Customer phone: " + curTransaction.getCustomerPhone());
		System.out.println("-----------------------------------------");
		for(Transaction t: transactions){
			System.out.println("TID: "+t.getTID());
			System.out.println("Movie: " + t.getShowTime().getMovie().getTitle());
			System.out.println("Cineplex: " + t.getCineplex().getCineplexName());
			System.out.println("Cinema: " + t.getCinema().getCID());
			if(p.isHoliday(t.getShowTime().getDateTime()))
				System.out.println(UserInputOutput.createDayOfWeekString(t.getShowTime().getDateTime())+"(Holiday)");
			else
				System.out.println(UserInputOutput.createDayOfWeekString(t.getShowTime().getDateTime()));
			System.out.println("Seats:");
			for(Map.Entry<Seat,String> chosenSeat : t.getSeats().entrySet()){
				Seat seat = chosenSeat.getKey();
				String age = chosenSeat.getValue();
				System.out.println(seat.getRow() + seat.getColumn() + ": " + age);
			}
			System.out.println("Total price: " + t.getTotalPrice());
			System.out.println("-----------------------------------------");
		}
	}

	/**
	 * UI to list Top 5 movies by Sales or rating
	 */
	public void listTopMoviesUI() {
		UserInputOutput.displayHeader("Top 5 Movies");
		System.out.println(
				"1. List top 5 ranking movies by ticket sales.\n" +
						"2. List top 5 ranking movies by Overall reviewers' rating.");
		TopMovieFactory movieFactory = new TopMovieFactory();
		Top5CurrentMovies top5CurrentMovies = null;
		switch (UserInputOutput.getUserChoice(1, 2)) {
			case 1:
				top5CurrentMovies = movieFactory.makeTop5Movie("ticket");
				top5CurrentMovies.printTop5Movies();
				break;
			case 2:
				top5CurrentMovies = movieFactory.makeTop5Movie("rating");
				top5CurrentMovies.printTop5Movies();
				break;
		}
	}

	/**
	 * UI to search for movie
	 */
	public void searchMovieUI() {
		UserInputOutput.displayHeader("Search movie");
		System.out.println("Please type in movie name");
		Scanner sc = new Scanner(System.in);
		String input = sc.next();
		ArrayList<Movie> movieList = movieController.searchMovieLogic(input);
		if (movieList.size() > 0) {
			System.out.println("Movies found: ");

			movieController.list(movieList);

			System.out.println("Please select movie: ");
			int index = UserInputOutput.getUserChoice(1,movieList.size());
			Movie chosenMovie= movieList.get((index - 1));
			getMovieDetailsView(chosenMovie);
		}
		else {
			System.out.println("Movie not found");
		}
	}

    /**
     * 	When a certain movie is passed in, this method will print out
     * 	All the details of that movie following the sequence
     * @param movie movie selected by customer
     */
    public void getMovieDetailsView(Movie movie) {
        int n = 0;
        ArrayList<Review> movieReviews = movie.getMovieReview();
        UserInputOutput.displayHeader("Movie Details");
        System.out.println("The details of " + movie.getTitle() + " :");
        System.out.println("1) Duration: " + movie.getDuration());
        System.out.println("2) Synopsis: " + movie.getSynopsis());
        System.out.println("3) Status: " + movie.getStatusType());
        System.out.println("5) Movie Type: " + movie.getMovieClass());
        System.out.println("6) Age Type: " + movie.getAgeType());
        if (movieReviews.isEmpty())
            System.out.println("7) Review Rating: No ratings yet");
        else {
            DecimalFormat df = new DecimalFormat("#.00");
            System.out.println("7) Review Rating:" + df.format(movie.getOverallReviewRating()));
        }
        String[] movieGenre = movie.getGenre();
        System.out.print("8) Genre: ");
        for (int j = 0; j < movieGenre.length; j++) {
            if (j != movieGenre.length - 1)
                System.out.print(movieGenre[j] + ", ");
            else
                System.out.print(movieGenre[j] + ".\n");}
        System.out.println("9) Director: "+ movie.getDirector());
        System.out.print("10) Cast: ");
        String[] movieCast = movie.getCast();
        for (int i = 0; i < movieCast.length; i++) {
            if (i != movieCast.length - 1)
                System.out.print(movieCast[i] + ", ");
            else
                System.out.print(movieCast[i] + ".\n");	}
        LocalDate showTill = movie.getShowTill();
        if (showTill!= null){
            System.out.println("11) Show Till: " + showTill);
        }
        else{
            System.out.println("11) Show Till: " + "Not specified.");
        }
        System.out.println("12) Reviews: ");
        if (movieReviews.isEmpty())
            System.out.println("12) Review Rating: No reviews yet");
        else{for (Review r : movieReviews) {
            System.out.println("   (" +(n+1)+ ") Rating: "  + r.getRating() + ", " + r.getContent());
            n++;}}

    }
}