package csci4370project4;


/*******************************************************************************
 * @file  MovieDB.java
 *
 * @author   John Miller
 */

import static java.lang.System.out;

/*******************************************************************************
 * The MovieDB class makes a Movie Database.  It serves as a template for making
 * other databases.  See "Database Systems: The Complete Book", second edition,
 * page 26 for more information on the Movie Database schema.
 */
class MovieDB
{
    /***************************************************************************
     * Main method for creating, populating and querying a Movie Database.
     * @param args  the command-line arguments
     */
    public static void main (String [] args)
    {
        
        Table movie = new Table ("movie", "title year length genre studioName producerNo",
                                          "String Integer Integer String String Integer", "title year");

        Table cinema = new Table ("cinema", "title year length genre studioName producerNo",
                                            "String Integer Integer String String Integer", "title year");

        Table movieStar = new Table ("movieStar", "name address gender birthdate",
                                                  "String String Character String", "name");

        Table starsIn = new Table ("starsIn", "movieTitle movieYear name",
                                              "String Integer String", "movieTitle movieYear name");

        Table movieExec = new Table ("movieExec", "certNo name address fee",
                                                  "Integer String String Double", "certNo");

        Table studio = new Table ("studio", "name address presNo",
                                            "String String Integer", "name");
        
        Table prefixTest = new Table("prefixTest", "name address randomNum", "String String Integer", "name");
        
        Table wrongKeyTest = new Table("wrongKey", "name address randomNum", "String String Integer", "randomNum");

        Comparable [] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
        Comparable [] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
        Comparable [] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
        Comparable [] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };
        out.println ();
        movie.insert (film0);
        movie.insert (film1);
        movie.insert (film2);
        movie.insert (film3);
        movie.print ();

        Comparable [] film4 = { "Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890 };
        out.println ();
        cinema.insert (film2);
        cinema.insert (film3);
        cinema.insert (film4);
        cinema.print ();

        Comparable [] star0 = { "Carrie_Fisher", "Hollywood", 'F', "9/9/99" };
        Comparable [] star1 = { "Mark_Hamill", "Brentwood", 'M', "8/8/88" };
        Comparable [] star2 = { "Harrison_Ford", "Beverly_Hills", 'M', "7/7/77" };
        out.println ();
        movieStar.insert (star0);
        movieStar.insert (star1);
        movieStar.insert (star2);
        movieStar.print ();

        Comparable [] cast0 = { "Star_Wars", 1977, "Carrie_Fisher" };
        Comparable [] cast1 = {"Star_Wars", 1977,  "Mark_Hamill" };
        Comparable [] cast2 = {"Star_Wars_2", 1980,  "Mark_Hamill" };
        Comparable [] cast3 = {"Star_Wars", 1977,  "Harrison_Ford" };
        out.println ();
        starsIn.insert (cast0);
        starsIn.insert (cast1);
        starsIn.insert (cast2);
        starsIn.insert (cast3);
        starsIn.print ();

        Comparable [] exec0 = { 9999, "S_Spielberg", "Hollywood", 10000.00 };
        out.println ();
        movieExec.insert (exec0);
        movieExec.print ();

        Comparable [] studio0 = { "Fox", "Los_Angeles", 7777 };
        Comparable [] studio1 = { "Universal", "Universal_City", 8888 };
        Comparable [] studio2 = { "DreamWorks", "Universal_City", 9999 };
        out.println ();
        studio.insert (studio0);
        studio.insert (studio1);
        studio.insert (studio2);
        studio.print ();

        Comparable[] pre0 = {"Mark_Hamill", "Tatooine", 001};
        Comparable[] pre1 = {"Harrison_Ford", "Kessel_Run", 007};
        Comparable[] pre2 = {"Carrie_Fisher", "Alderan", 232};
        out.println();
        prefixTest.insert(pre0);
        prefixTest.insert(pre1);
        prefixTest.insert(pre2);
        prefixTest.print();
        
        out.println();
        wrongKeyTest.insert(pre0);
        wrongKeyTest.insert(pre1);
        wrongKeyTest.insert(pre2);
        wrongKeyTest.print();
        
        out.println ();
        Table t_project = movie.project ("title year");
        t_project.print ();
        
        out.println();
        out.println("Should Eliminate Duplicate Tuples");
        Table t_project2 = movieStar.project("gender");
        t_project2.print();

        out.println ();
        Table t_select = movie.select ("title == 'Star_Wars'");
        t_select.print ();
        
        out.println ();
        Table t_select2 = movie.select ("1979 < year & year < 1990");
        t_select2.print ();

//Test cases for union        
        out.println ();
        out.println("Should pass");
        Table t_union = movie.union (cinema);
        t_union.print ();
        
        out.println ();
        out.println("Should pass and display original table, because tables are equal");
        Table t_union3 = movie.union (movie);
        t_union3.print ();
        
        out.println ();
        out.println("Should fail because tables not compatible");
        Table t_union2 = movie.union (starsIn);
        t_union2.print ();

//Test Cases for Minus        
        out.println ();
        out.println("Should pass");
        Table t_minus = movie.minus (cinema);
        t_minus.print ();
        
        out.println ();
        out.println("Should pass and display original table, because tables not compatible");
        Table t_minus2 = movie.minus (starsIn);
        t_minus2.print ();
        
        out.println();
        out.println("Should pass and display empty table, because tables are equal");
        Table t_minus3 = movie.minus(movie);
        t_minus3.print();
        
//Test Cases for Join
        out.println ();
        out.println("Should fail because of # of arguments");
        Table t_join2 = movie.join ("studioName == name && producerNo == pressNo", studio);
        t_join2.print ();
        
        out.println ();
        out.println("Should fail because producerNo does not exist in studio");
        Table t_join3 = movie.join ("studioName == producerNo", studio);
        t_join3.print ();
        
        out.println ();
        out.println("Should fail because pressNo does not exist in movie");
        Table t_join4 = movie.join ("pressNo == name", studio);
        t_join4.print ();
        
        out.println ();
        out.println("Should fail because == is not used");
        Table t_join5 = movie.join ("studioName >= name", studio);
        t_join5.print ();
        
        out.println();
        out.println("Should fail because the foreign key constraints are not followed");
        Table t_join7 = studio.join("name == s.name", movieStar);
        t_join7.print();
        
        out.println();
        out.println("Should fail because the attribute in table2 is not the primary key");
        Table t_join9 = studio.join("name == s.name", wrongKeyTest);
        t_join9.print();
        
        out.println ();
        out.println("Should pass");
        Table t_join =  movie.join ("studioName == name", studio);
        t_join.print ();
        
        out.println ();
        out.println("Should pass");
        Table t_join6 = starsIn.join ("name == s.name", movieStar);
        t_join6.print();
        
        out.println();
        out.println("Should pass");
        Table t_join8 = movieStar.join("name == s.name", prefixTest);
        t_join8.print();
        
    } // main

} // MovieDB class
