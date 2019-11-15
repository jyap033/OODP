package com.company.Interface;

import com.company.Entity.Movie;
import com.company.Utils.UserInputOutput;
import com.company.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TopTicketMovies extends Top5CurrentMovies{

    @Override
    public ArrayList<Movie> getCurrentShowingMovieList() {

        Collections.sort(super.currentShowingMovieList, new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                return o2.getTotalSales() - o1.getTotalSales();
            }
        });
        return currentShowingMovieList;
    }

    @Override
    public void printTop5Movies() {
        UserInputOutput.displayHeader("Top 5 Movie List based on Ticket Sales");
        int i = 1;
        for (Movie m: getCurrentShowingMovieList()){
            System.out.println("Top "+ i + ": " + m.getTitle() + "(" + m.getTotalSales() + ")");
            i++;
            if(i == 4){
                break;
            }
        }
    }
}