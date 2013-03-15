package CSCI4370Project4;

/*******************************************************************************
 * @file  TestTupleGenerator.java
 *
 * @author   Sadiq Charaniya, John Miller
 * Modified by: Jeffrey Swindle
 */

import csci4370project4.Table;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Random;

/*******************************************************************************
 * This class tests the TupleGenerator on the Student Registration Database defined
 * in the Kifer, Bernstein and Lewis 2006 database textbook (see figure 3.6).
 * The primary keys (see figure 3.6) and foreign keys (see example 3.2.2) are as
 * given in the textbook.
 */
public class TestTupleGenerator
{
    /***************************************************************************
     * The main method is the driver for TestGenerator.
     * @param args  the command-line arguments
     */
    public static void main (String [] args)
    {
        TupleGenerator test = new TupleGeneratorImpl ();

        System.out.println("Creating Student Table.");
        
        //Create Student table
        Table Student = new Table ("Student", "id name address status",
                                          "Integer String String String", "id");
        
        System.out.println("Creating Student Schema.");
        
        //Create Student schema for tuple generation
        test.addRelSchema ("Student",
                           "id name address status",
                           "Integer String String String",
                           "id");
        
        System.out.println("Creating Professor Table.");
        
        //Create Professor table
        Table Professor = new Table ("Professor", "id name deptId",
                                         "Integer String String", "id");
        
        System.out.println("Creating Professor Schema");
       
        //Create Professor schema for tuple generation
        test.addRelSchema ("Professor",
                           "id name deptId",
                           "Integer String String",
                           "id");
        
        System.out.println("Creating Course Table.");
        
        //Create Course table
        Table Course = new Table ("Course", "crsCode deptId crsName descr",
                                          "String String String String", "crsCode");
        
        System.out.println("Creating Course Schema.");
        
        //Create Course schema for tuple generation
        test.addRelSchema ("Course",
                           "crsCode deptId crsName descr",
                           "String String String String",
                           "crsCode");
        
        System.out.println("Creating Teaching Table.");
        
        //Create Teaching table
        Table Teaching = new Table ("Teaching", "crsCode semester profId",
                                          "String String Integer", "crsCode semester");
         
        System.out.println("Creating Teaching Schema.");
        
        //Create Teaching schema for tuple gneration
        test.addRelSchema ("Teaching",
                           "crsCode semester profId",
                           "String String Integer",
                           "crsCode semester");
        
        System.out.println("Creating Transcript Table.");
        
        //Create Transcript table
        Table Transcript = new Table ("Transcript", "studId crsCode semester grade",
                                          "Integer String String String", "studId crsCode semester");
        
        System.out.println("Creating Transcript Schema");
        
        //Create Transcript schema for tuple gneration
        test.addRelSchema ("Transcript",
                           "studId crsCode semester grade",
                           "Integer String String String",
                           "studId crsCode semester");
       
        //Set number of tuples to generation for each table
        int tups [] = new int [] { 10000, 1000, 2000, 50000, 5000 };
    
        System.out.println("Generating random tuples.");
        
        //Generate tuple
        Comparable [][][] resultTest = test.generate (tups);
        
        int counter = 0;
        
        /**
         * Insert tuples in respective table
         */
        
        System.out.println("Inserting tuples in Student Table.");
        
        //Insert tuples from Student schema random generation ( 10000 )
        for (int j = 0; j < resultTest [0].length; j++) {
            
            System.out.println ("Inserting Student " + j);

            Student.insert(resultTest[0][counter]);
                    
            counter++;

        } // for
        
        System.out.println("Inserting tuples in Professor Table.");
        
        counter = 0;
        
        //Insert tuples from Professor schema random generation ( 1000 )
        for (int j = 0; j < resultTest [1].length; j++) {
            
            System.out.println ("Inserting Professor " + j);

            Professor.insert(resultTest[1][counter]);
                    
            counter++;
            
        } // for
        
        System.out.println("Inserting tuples in Course Table.");
        
        counter = 0;
        
        //Insert tuples from Course schema random generation ( 2000 )
        for (int j = 0; j < resultTest [2].length; j++) {
                                
            System.out.println ("Inserting Course " + j);

            Course.insert(resultTest[2][counter]);
                    
            counter++;
            
        } // for
        
        System.out.println("Inserting tuples in Teaching Table.");
        
        counter = 0;
        
        //Insert tuples from Teaching schema random generation ( 50000 )
        for (int j = 0; j < resultTest [3].length; j++) {
            
            System.out.println ("Inserting Teaching " + j);
  
            Teaching.insert(resultTest[3][counter]);
                    
            counter++;
            
        } // for
        
        System.out.println("Inserting tuples in Transcript Table.");
        
        counter = 0;
        
        //Insert tuples from Transcript schema random generation ( 5000 )
        for (int j = 0; j < resultTest [4].length; j++) {
            
            System.out.println ("Inserting Transcript " + j);
    
            Transcript.insert(resultTest[4][counter]);
                    
            counter++;
            
        } // for
        
        
        //Variable decs for time recording
        long startTime;
        long endTime;
        double duration;
        DecimalFormat format=new DecimalFormat("#.########");
        
        //Variable decs for random tuple selection
        int studentInt;
        int professorInt;
        int courseInt;
        int transcriptInt;
        int teachingInt;
        Random ran = new SecureRandom();
        
        /**
         * Select - Point Query
         */
        
        System.out.println("Select - Point Query");

        System.out.println("\tStudent point select - 10000 tuples");
        for( int i = 0; i < 50 ; i++){
            studentInt = ran.nextInt(10000);
            startTime = System.nanoTime();
            Student.select ("id == " + resultTest[0][studentInt][0].toString());
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        System.out.println("\tProfessor point select - 1000 tuples");
        for( int i = 0; i < 50 ; i++){
            professorInt = ran.nextInt(1000);
            startTime = System.nanoTime();
            Professor.select ("id == " + resultTest[1][professorInt][0].toString());
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        System.out.println("\tCourse point select - 2000 tuples");
        for( int i = 0; i < 50 ; i++){
            courseInt = ran.nextInt(2000);
            startTime = System.nanoTime();
            Course.select ("crsCode == " + resultTest[2][courseInt][0].toString());
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        System.out.println("\tTeaching point select - 50000 tuples");
        for( int i = 0; i < 50 ; i++){
            teachingInt = ran.nextInt(50000);
            startTime = System.nanoTime();
            Teaching.select ("crsCode == " + resultTest[3][teachingInt][0].toString());
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        System.out.println("\tTranscript point select - 5000 tuples");
        for( int i = 0; i < 50 ; i++){
            transcriptInt = ran.nextInt(5000);
            startTime = System.nanoTime();
            Transcript.select ("crsCode == " + resultTest[4][transcriptInt][0].toString());
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        /**
         * Select - Range Query
         */
        
        System.out.println("Select - Range Query");
        
        System.out.println("\tStudent range select - 10000 tuples");
        for( int i = 0; i < 50 ; i++){
            studentInt = ran.nextInt(10000);
            startTime = System.nanoTime();
            Student.select ("id > " + resultTest[0][studentInt][0].toString());
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        System.out.println("\tProfessor range select - 1000 tuples");
        for( int i = 0; i < 50 ; i++){
            professorInt = ran.nextInt(1000);
            startTime = System.nanoTime();
            Professor.select ("id > " + resultTest[1][professorInt][0].toString());
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        System.out.println("\tCourse range select - 2000 tuples");
        for( int i = 0; i < 50 ; i++){
            courseInt = ran.nextInt(2000);
            startTime = System.nanoTime();
            Course.select ("crsCode > " + resultTest[2][courseInt][0].toString());
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
                
        System.out.println("\tTeaching range select - 50000 tuples");
        for( int i = 0; i < 50 ; i++){
            teachingInt = ran.nextInt(50000);
            startTime = System.nanoTime();
            Teaching.select ("crsCode > " + resultTest[3][teachingInt][0].toString());
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        System.out.println("\tTranscript range select - 5000 tuples");
        for( int i = 0; i < 50 ; i++){
            transcriptInt = ran.nextInt(5000);
            startTime = System.nanoTime();
            Transcript.select ("crsCode > " + resultTest[4][transcriptInt][0].toString());
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
         /**
         * Join Query
         */
             
        System.out.println("Join Query");

        System.out.println("\tStudent join with Transcript - 10000 tuples + 50000 tuples");
        for( int i = 0; i < 50 ; i++){
            startTime = System.nanoTime();
            Table tempTable = Student.join ("id == studId", Transcript);
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        
        System.out.println("\tProfessor join with Teaching - 1000 tuples + 5000 tuples");
        for( int i = 0; i < 50 ; i++){
            startTime = System.nanoTime();
            Table tempTable = Professor.join ("id == profId", Teaching);
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        
        System.out.println("\tCourse join with Transcript - 2000 tuples + 5000 tuples");
        for( int i = 0; i < 50 ; i++){
            startTime = System.nanoTime();
            Table tempTable = Course.join ("crsCode == crsCode", Transcript);
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
        System.out.println("\tTeaching join with Course - 50000 tuples + 2000 tuples");
        for( int i = 0; i < 50 ; i++){
            startTime = System.nanoTime();
            Table tempTable = Teaching.join ("crsCode == crsCode", Course);
            endTime = System.nanoTime();
            duration = (double)(endTime - startTime)/1000000000.0;
            System.out.println("\t\t" + format.format(duration));
        }//for
        
    } // main
} // TestTupleGenerator