/******************************************************************************                                        
 *  Compilation:  javac Viewer.java                                                                                
 *  Execution:    java Viewer -d:folder -f:i,f
 				  java Viewer -d:folder -r:i,f
 				  java Viewer -d:folder -f
 				  java Viewer -d:folder -r                                                                                     
 *  Dependencies: Picture.java                                                                                         
 ******************************************************************************/
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.File;
import java.awt.Color;
public class Viewer extends LinkedList{

  private static boolean go = true;
  private static boolean parameter = false;
  private static boolean reverse = false;
  private static boolean one = false;

  public static Color combine(Color c1, Color c2, double alpha) {
	     int r = (int) (alpha * c1.getRed()   + (1 - alpha) * c2.getRed());
	     int g = (int) (alpha * c1.getGreen() + (1 - alpha) * c2.getGreen());
	     int b = (int) (alpha * c1.getBlue()  + (1 - alpha) * c2.getBlue());
	     return new Color(r, g, b);
  }

  public static void fade(Picture p, Picture p1, Picture p2) {
    Picture picture1 = new Picture(p1);   // begin picture
    Picture picture2 = new Picture(p2);   // end picture
    for (int k = 0; k <= 500; k++) {
      double alpha = 1.0 * k / 500;
      for (int col = 0; col < p.width(); col++) {
        for (int row = 0; row < p.height(); row++) {
          Color c1 = picture1.get(col, row);
          Color c2 = picture2.get(col, row);
          p.set(col, row, combine(c2, c1, alpha));
        }
      }
      p.show();
    }
  }

  public static void usage(){
    System.out.println("Usage: java Viewer -d:<folder> -f:i,f");
    System.out.println("Usage: java Viewer -d:<folder> -r:i,f");
    System.out.println("Usage: java Viewer -d:<folder> -f");
    System.out.println("Usage: java Viewer -d:<folder> -r");
    System.exit(0); 
  }

    public static void main(String[] args) {

      if (args.length != 2) usage();

      // splits the first command
      String[] cmd1 = args[0].split(":");
      // splits the second command
      String[] cmd2 = args[1].split(":");

      if (cmd1.length > 2 || cmd2.length > 2 || cmd1.length == 0 || cmd2.length == 0) usage();
      if(!(cmd1[0].equals("-d"))) usage();
                // get the list of all file names                                                                              
      File folder = new File(cmd1[1]+"/");  // may need to change this 
      File[] listOfFiles = folder.listFiles();
        
        // create a collection of these objects                                                                        
      LinkedList<Picture> photoStack = new LinkedList<Picture>();
      try{
        for (File f: listOfFiles)     // array implements iterable 
          photoStack.addLast(new Picture(f));
      }
      catch(NullPointerException npe){
        System.out.println("Sorry, could not find this folder in the current directory.");
        System.exit(0);
      } 
        
      int i = 1; // default paramters
      int f = listOfFiles.length; //default parameters

      if (cmd2.length == 2) {
        String[] param = cmd2[1].split(",");
        if(param.length != 2){
          usage();
        }
          else {
            try {
                // Parse the string argument into an integer value.
                i = Integer.parseInt(param[0]);
                f = Integer.parseInt(param[1]);
            }
            catch (NumberFormatException nfe) {
              // The first argument isn't a valid integer.  Print
              // an error message, then exit with an error code.
              System.out.println("Sorry, i and f must be integer parameters.");
              System.exit(1);
            }
          }
          parameter = true; // falgging that parameters were provided
          if(i < 1 || i > listOfFiles.length || f < 1 || f > listOfFiles.length){
            System.out.println("Sorry, please check to see if your parameters are between 1 and \nthe number of unique pictures you want to look through.");
            System.exit(0);
          } 
        }
        int count = 1;
        Picture fp = photoStack.getFirst();
        Picture lp = photoStack.getLast();
        Iterator<Picture> it = photoStack.iterator();
        if(cmd2[0].equals("-f")){
            // The following can be used if LinkedList implements Iterator                                                 
            it = photoStack.iterator();
        }
        else if(cmd2[0].equals("-r")){
        	// Implements the reverse iterator
        	reverse = true; 
            it = photoStack.revIterator();
        }
        else {
        	usage();
        	System.exit(1);
        }
        Picture prev = it.next();
        Picture fadePic = new Picture(prev.width(), prev.height());
        if(parameter){ 
          go = false;
		    if(reverse){ 
		      count = listOfFiles.length;
		      if(!(i >= f)){ 
		        System.out.println("Sorry, the parameters you provided for reverse do not work.");
		        System.exit(1);
		      }
		    }
            else {
		      if(!(i <= f)){
		        System.out.println("Sorry, the parameters you provided for forward do not work.");
		        System.exit(1);
		      }
            }
        }

        if(f == i){
        	one = true;
        }

        while (it.hasNext()) {
          if(parameter){
    		if(reverse){
    		  if(count <= i && count >= f+1) go = true;
    		    else go = false;
    		  }
    		if(!reverse){
    		  if(count >= i && count <= f-1) go = true;
    		  else go = false;
				}
    		}
        	Picture next = it.next();		   
        	if(one){
    		  if(reverse && count == i)
			    break;
    		  if(!reverse && count == i)
    			break;
    		}
        	if(go){ 
        		if(!one) fade(fadePic,prev,next);
        	}
    		prev = next;
    		if(!reverse) count++;
    		else count--;
        }
        if(one){
		  prev.show();
		}	                                                                                                                                                                                
    }
}