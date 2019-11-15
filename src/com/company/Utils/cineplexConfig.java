package com.company.Utils;

import com.company.Entity.*;
import com.company.View.SeatUI;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class cineplexConfig {
	public static void main(String[] args) {
        //new cineplex
        Cineplex cp1 = new Cineplex("Jem");
        Cineplex cp2 = new Cineplex("West Mall");
        Cineplex cp3 = new Cineplex("The Cathay");
        ArrayList<Cineplex> cp = new ArrayList<Cineplex>();
        cp.add(cp1);
        cp.add(cp2);
        cp.add(cp3);

        //new cinema
        Cinema c1 = new Cinema("wm1");
        Cinema c2 = new Cinema("wm2");
        Cinema c3 = new Cinema("wm3");
        c1.setCinemaType(c1.getCinemaTypes()[1]);
        ArrayList<Cinema> cinemas = new ArrayList<Cinema>();
        cinemas.add(c1);
        cinemas.add(c2);
        cinemas.add(c3);


        for(Cineplex c: cp) {
            if(c.getCineplexName().equals("West Mall")) {
                c.setCinemas(cinemas);
            }
            else {
                c.setCinemas(null);
            }
        }

        ArrayList<Movie> movie = null;
        try {
            movie = (ArrayList<Movie>) Utils.readObject("movie.txt");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ShowTime st1 = new ShowTime(LocalDateTime.of(2019, 12, 10, 12, 10),movie.get(0));
        ShowTime st2 = new ShowTime(LocalDateTime.of(2019, 12, 11, 11, 10),movie.get(0));
        ShowTime st3 = new ShowTime(LocalDateTime.of(2019, 12, 11, 12, 10),movie.get(0));
        try {
            c1.addShowTime(st1);
            c2.addShowTime(st2);
            c3.addShowTime(st3);
        }
        catch(IllegalArgumentException e) {
            System.out.println(e);
        }

        //check day of week of showtime
        System.out.println(st2.getDateTime().getDayOfWeek().ordinal());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println(Utils.createDayOfWeekString(st1.getDateTime()));
        System.out.println(Utils.createDayOfWeekString(st2.getDateTime()));
        System.out.println(Utils.createDayOfWeekString(st3.getDateTime()));
        System.out.println(st1.getDateTime().format(formatter).equals(st2.getDateTime().format(formatter)));

        // print seats

        SeatUI sui = new SeatUI();
        ArrayList<String> chosen = new ArrayList<String>();
        chosen.add("rAc1");
        //sui.getSeatListing(st1,chosen);
        try {
            Utils.writeObject("cineplex.txt", (Object)cp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Price p = new Price();
        try {
            Utils.writeObject("price.txt", (Object)p);
        } catch (IOException e) {
            e.printStackTrace();
        }
			/*
			ArrayList<Cineplex> cpp = (ArrayList<Cineplex>)Utils.readObject("cineplex.txt");
			for(Cineplex c: cpp) {
				ArrayList<Cinema> clist1 = c.getCinemas();
				System.out.println(c.getCineplexName());
				if(c.getCinemas()!=null) {
					for(Cinema ci: clist1) {
						System.out.println(ci.getCID());
					}
				}
			}
			*/
    }
}